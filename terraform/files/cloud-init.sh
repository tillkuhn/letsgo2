#!/usr/bin/env bash
SCRIPT=$(basename $${BASH_SOURCE[0]})
# chown $(id -u):$(id -g) file instead of ec2-user
if [ $# -lt 1 ]; then
    set -- help
fi

if [ "$EUID" -ne 0 ]; then
    echo "[FATAL] Detected UID $UID, please run with sudo"
    ## todo: make more friendly, require sudo onlx where necessary
    ## https://askubuntu.com/questions/20578/redirect-the-output-using-sudo
    ## use echo hase|sudo tee protected_file >/dev/null
    exit
fi

if [[ "$*" == *common* ]] || [ "$*" == *all* ]; then
    mkdir -p ${appdir}
    grep -q "alias go2ctl" /home/ec2-user/.bashrc || echo "alias go2ctl=\"sudo ${appdir}/$SCRIPT\"" >>/home/ec2-user/.bashrc
fi

if [[ "$*" == *update* ]]; then
    echo "[INFO] Upating $SCRIPT, please launch again after update"
    aws s3 cp s3://${bucket_name}/deploy/$SCRIPT ${appdir}/$SCRIPT && chmod ugo+x ${appdir}/$SCRIPT
    exit 0
fi

################
# Enable Swap
# https://stackoverflow.com/questions/17173972/how-do-you-add-swap-to-an-ec2-instance
if [[ "$*" == *swapon* ]] || [ "$*" == *all* ]; then
    ## rule of thumb for < 2GB memory: Take memory * 2
    SWAPSIZEMB=$(grep MemTotal /proc/meminfo | awk '$1 == "MemTotal:" {printf "%.0f", $2 / 512 }')
    if [ ! -f /mnt/swapfile ]; then
        echo "[INFO] Enabling Swap Support with $${SWAPSIZEMB}MB"
        dd if=/dev/zero of=/mnt/swapfile bs=1M count=1024
        chown root:root /mnt/swapfile
        chmod 600 /mnt/swapfile
        mkswap /mnt/swapfile
        swapon /mnt/swapfile ## to disable run swapoff -a
        swapon -a
    else
        echo "[DEBUG] Swap already enabled with $${SWAPSIZEMB}MB"
    fi
    if ! egrep "^/mnt/swapfile" /etc/fstab >/dev/null; then
        echo "[INFO] creating fstab enty for swap"
        echo "/mnt/swapfile swap swap defaults 0 0" >>/etc/fstab
    fi
fi

if [[ "$*" == *install* ]] || [[ "$*" == *all* ]]; then
    ## check out if [[ "$*" == *YOURSTRING* ]] https://superuser.com/questions/186272/check-if-any-of-the-parameters-to-a-bash-script-match-a-string
    echo "[INFO] Updating packages, adding openjdk11 and nginx"
    yum install -y -q deltarpm
    yum update -y -q
    amazon-linux-extras install -y -q java-openjdk11
    amazon-linux-extras install -y -q nginx1
    wget -r --no-parent -A 'epel-release-*.rpm' http://dl.fedoraproject.org/pub/epel/7/x86_64/Packages/e/
    rpm -Uvh dl.fedoraproject.org/pub/epel/7/x86_64/Packages/e/epel-release-*.rpm
    yum-config-manager --enable epel*
    yum install -y -q certbot python2-certbot-nginx unzip
fi

if [[ "$*" == *install-security* ]]; then
    echo "[INFO] Updating security packages"
    # yum list-security --security
    sudo yum update -y -q --security
    # https://medium.com/@vbmade2000/a-dead-simple-tutorial-on-how-to-forward-rsyslog-logs-to-a-file-130364c049fb
    logger -t gotoctl "yum security up2date"
    # more /etc/rsyslog.d/21-cloudinit.conf
    ## Log cloudinit generated log messages to file
    #:syslogtag, isequal, "[CLOUDINIT]" /var/log/cloud-init.log
    #& stop
    # sudo systemctl restart rsyslog
fi

if [[ "$*" == *certbot* ]] || [[ "$*" == *all* ]]; then
    echo "[INFO] Checking letsencrypt status"
    if [ -d /etc/letsencrypt/live ]; then
        echo "[INFO] /etc/letsencrypt already exists with content (wip: care for changed domain names)"
    elif aws s3api head-object --bucket ${bucket_name} --key letsencrypt/letsencrypt.tar.gz; then
        echo "[INFO] local /etc/letsencrypt missing but s3 backup is availble, downloading archive"
        aws s3 cp s3://${bucket_name}/letsencrypt/letsencrypt.tar.gz ${appdir}/
        chown -R ec2-user:ec2-user ${appdir}/letsencrypt.tar.gz
        tar -C /etc -xvf ${appdir}/letsencrypt.tar.gz
    else
        echo "[INFO] No local or remote letsencrypt nginx config found, new one will be requested"
    fi

    echo "[INFO] Making sure nginx is registered as nginx service and restarted if running"
    systemctl enable nginx
    systemctl start nginx
    echo "[INFO] Launching certbot for ${domain_name}"
    certbot --nginx -m ${certbot_mail} --agree-tos --redirect -n -d ${domain_name}
    if [ $? -eq 0 ]; then
        echo "[INFO] Backup succeded, backup /etc/letsencrypt folder to s3://${bucket_name}"
        tar -C /etc -zcf /tmp/letsencrypt.tar.gz letsencrypt
        aws s3 cp --sse=AES256 /tmp/letsencrypt.tar.gz s3://${bucket_name}/letsencrypt/letsencrypt.tar.gz
    else
        echo "cerbot exit with status $? so something went wrong, checkout cerbot output for info"
    fi
    echo "[INFO] Certbox init comlete, check out https://${domain_name}"

    # add me https://serverfault.com/questions/790772/cron-job-for-lets-encrypt-renewal
    ## CRON
    ## sudo certbot renew --post-hook "systemctl reload nginx"
fi

if [[ "$*" == *cron* ]] || [[ "$*" == *all* ]]; then
    CERTBOT_JOB=/etc/cron.daily/certbot-renew
    YUMSECURITY_JOB=/etc/cron.daily/yum-update-security
    echo '/usr/bin/certbot renew >>${appdir}/logs/certbot.log 2>>${appdir}/logs/certbot.err' >$${CERTBOT_JOB}
    echo 'sudo yum update -y -q --security >>${appdir}/logs/yum.log 2>>${appdir}/logs/yum.err' >$${YUMSECURITY_JOB}
    chmod 755 $${CERTBOT_JOB} $${YUMSECURITY_JOB}
    echo "[INFO] Scheduled $${CERTBOT_JOB} and $${YUMSECURITY_JOB}"
fi

## Manage backend
if [[ "$*" == *backend* ]] || [ "$*" == *all* ]; then
    ## todo check systemctl list-units --full -all |grep -Fq letsgo2
    ## then install on demand
    echo "[INFO] Pulling app.* artifacts from ${bucket_name}"
    aws s3 sync s3://${bucket_name}/deploy ${appdir}/deploy
    ## todo react on changed config
    if [ ! -f /etc/systemd/system/${appid}.service ]; then
        echo "[INFO] Creating symlink /etc/systemd/system/${appid}.service"
        ln -s ${appdir}/deploy/app.service /etc/systemd/system/${appid}.service
    fi
    ##  ${appdir}/app.jar ${appdir}/app-running.jar
    #if [ -f ${appdir}/app-running.jar ] && ! cmp --silent ${appdir}/app-running.jar ${appdir}/app.jar; then
    #    echo "[INFO] app.jar has changed, stopping ${appid} to force restart"
    #    systemctl stop ${appid}
    #else
    #    echo "[DEBUG] app.jar has not changed (or first run), skip stop"
    #fi
    ###################
    ## file logging
    echo "[DEBUG] Checking /etc/rsyslog.d/25-${appid}.conf"
    if [ ! -f /etc/rsyslog.d/25-${appid}.conf ]; then
        ## https://stackoverflow.com/questions/37585758/how-to-redirect-output-of-systemd-service-to-a-file
        ## https://www.rsyslog.com/doc/v8-stable/configuration/filters.html can also use :syslogtag
        echo "[INFO] Logging not set up, registering ${appid} file logging via rsyslogd"
        touch "${appdir}/logs/stdout.log" && chown $(id -u):$(id -g) "${appdir}/logs/stdout.log"
        echo ":programname, isequal, \"${appid}\" ${appdir}/logs/stdout.log" >/etc/rsyslog.d/25-${appid}.conf ## create
        echo "& stop" >>/etc/rsyslog.d/25-${appid}.conf                                                       ## no need for a copy in s in /var/log/syslog
        echo "[INFO] File logging configured in /etc/rsyslog.d/25-${appid}.conf, restarting rsyslogd"
        systemctl restart rsyslog
    fi
    systemctl daemon-reload
    echo "[INFO] Starting ${appid}" && systemctl start ${appid}
    systemctl status ${appid}
fi

## Manage frontend
if [[ "$*" == *frontend* ]] || [ "$*" == *all* ]; then
    echo "[INFO] Pulling frontend and nginx.config from ${bucket_name}"
    aws s3 sync s3://${bucket_name}/deploy ${appdir} --exclude "*" --include "nginx.conf" --include "webapp.*"
    echo "[INFO] Cleaning /usr/share/nginx/html"
    rm -rf /usr/share/nginx/html/*
    echo "[INFO] Inflating ${appdir}/webapp.tgz"
    tar -C /usr/share/nginx/html -xf ${appdir}/webapp.tgz
    echo "[INFO] Replacing system nginx.conf with enhanced version ..."
    cp ${appdir}/nginx.conf /etc/nginx/nginx.conf
    echo "[INFO] Reloading nginx" && systemctl reload nginx
    systemctl status nginx
fi

## on demand tarfgets, not for all
if [[ "$*" == *status* ]]; then
    systemctl status ${appid} --lines=6
    systemctl status nginx --lines=2
fi

## display usage
if [[ "$*" == *help* ]]; then
    echo "Usage: $0 [target]"
    echo
    echo "Targets:"
    echo "  all         Runs all target except help and update"
    echo "  update      Update version of this tool"
    echo "  install     Installes required yum packages"
    echo "  certbot     Handles certbot certification for SSL certs"
    echo "  frontend    Redeploys frontend"
    echo "  backend     Redeploys backend"
    echo "  swapon      Checks swap status and activates if necessary"
    echo "  status      Display status of ${appid} services"
    echo "  cron        Created / update cronjobs"
    echo "  help        This help"
    echo
fi

# curl http://169.254.169.254/latest/user-data ## get current user data
# echo "@reboot ec2-user /usr/bin/date >>/opt/letsgo2/logs/reboot.log" | sudo tee /etc/cron.d/reboot >/dev/null
# comment out the following line to allow CLOUDINIT messages through.
# systemctl show letsgo2 # last log messages
# https://unix.stackexchange.com/questions/87223/rsyslog-execute-script-on-matching-log-event
# https://stackoverflow.com/questions/34137616/pass-comand-line-parameters-to-shell-script-via-omprog-rsyslog-module
# journalctl -u letsgo2 -n 100 --no-pager
# echo "BIN SDABEI" |logger -t klaus2; sudo tail /var/log/messages;
# Dec 21 11:39:52 ip-172-31-15-88 klaus2: BIN SDABEI
#aws dynamodb put-item --table-name loggin --item '{"Artist": {"S": "Obscure Indie Band"},"SongTitle": {"S": "Call Me Today"}}'
# aws s3 cp s3://timafe-letsgo2-data/deploy/cloud-init.sh . && chmod ugo+x cloud-init.sh
