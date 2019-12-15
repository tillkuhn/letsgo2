#!/usr/bin/env bash
echo "[INFO] Running cloud-init custom script in $1 mode"
if [ "$EUID" -ne 0 ]
  then echo "[FATAL] Detected UID $UID, please run with sudo"
  exit
fi

if [[  "$*" == *update*  ]]; then
    SCRIPT=$(basename $${BASH_SOURCE[0]})
    echo "[INFO] Upating $SCRIPT, please launch again after update"
    aws s3 cp s3://${bucket_name}/deploy/$SCRIPT ${appdir}/$SCRIPT && chmod ugo+x ${appdir}/$SCRIPT
    exit 0
fi


################
# Enable Swap
# https://stackoverflow.com/questions/17173972/how-do-you-add-swap-to-an-ec2-instance
if [[  "$*" == *swapon*  ]] || [ "$*" == *all*  ]; then
    ## rule of thumb for < 2GB memory: Take memory * 2
    SWAPSIZEMB=$(grep MemTotal /proc/meminfo | awk '$1 == "MemTotal:" {printf "%.0f", $2 / 512 }')
    if [ ! -f /mnt/swapfile ]; then
        echo "[INFO] Enabling Swap Support with $${SWAPSIZEMB}MB"
        dd if=/dev/zero of=/mnt/swapfile bs=1M count=1024
        chown root:root /mnt/swapfile
        chmod 600 /mnt/swapfile
        mkswap /mnt/swapfile
        swapon /mnt/swapfile  ## to disable run swapoff -a
        swapon -a
    else
        echo "[DEBUG] Swap already enabled with $${SWAPSIZEMB}MB"
    fi
    if ! egrep "^/mnt/swapfile" /etc/fstab >/dev/null; then
        echo "[INFO] creating fstab enty for swap"
        echo "/mnt/swapfile swap swap defaults 0 0" >>/etc/fstab
    fi
fi


if [[ "$*" == *all* ]]; then
    ## check out if [[ "$*" == *YOURSTRING* ]] https://superuser.com/questions/186272/check-if-any-of-the-parameters-to-a-bash-script-match-a-string
    echo "[INFO] Updating packages, installing openjdk11 and nginx"
    yum install -y -q deltarpm
    yum update -y -q
    amazon-linux-extras install  -y -q java-openjdk11
    amazon-linux-extras install  -y -q nginx1
    wget -r --no-parent -A 'epel-release-*.rpm' http://dl.fedoraproject.org/pub/epel/7/x86_64/Packages/e/
    rpm -Uvh dl.fedoraproject.org/pub/epel/7/x86_64/Packages/e/epel-release-*.rpm
    yum-config-manager --enable epel*
    yum install -y -q certbot python2-certbot-nginx unzip

    echo "[INFO] Downloading all deploy artifacts from s3://${bucket_name}/deploy to ${appdir}"
    mkdir -p ${appdir}
    aws s3 sync s3://${bucket_name}/deploy ${appdir}
    chown -R ec2-user:ec2-user /opt/letsgo2/
    unzip -o ${appdir}/webapp.zip -d /usr/share/nginx/html

    echo "[INFO] Checking letsencrypt status "
    if [ -d /etc/letsencrypt/live ]; then
      echo "[INFO] /etc/letsencrypt already exists with contend, nothing do do"
    elif aws s3api head-object --bucket ${bucket_name} --key letsencrypt/letsencrypt.tar.gz; then
      echo "[INFO] local /etc/letsencrypt missing, downloading letsencrypt config from sr"
      aws s3 cp s3://${bucket_name}/letsencrypt/letsencrypt.tar.gz ${appdir}/
      chown -R ec2-user:ec2-user ${appdir}/letsencrypt.tar.gz
      cd /etc
      tar -xvf ${appdir}/letsencrypt.tar.gz
    else
      echo "[INFO] No local or remote letsencrypt  nginx config found, new one will be requested"
    fi
    echo "[INFO] Making sure nginx is registered as nginx service and restarted if running"
    systemctl enable nginx
    systemctl start nginx
    echo "[INFO] Launching certbot for ${domain_name}"
    certbot --nginx -m ${certbot_mail} --agree-tos --redirect -n -d ${domain_name}

    echo "[INFO] Backup /etc/letsencrypt to s3://${bucket_name}"
    tar -C /etc -zcf /tmp/letsencrypt.tar.gz letsencrypt
    aws s3 cp --sse=AES256 /tmp/letsencrypt.tar.gz s3://${bucket_name}/letsencrypt/letsencrypt.tar.gz

    echo "[INFO] Replacing system nginx.conf with enhanced version ..."
    cp  ${appdir}/nginx.conf /etc/nginx/nginx.conf
    systemctl restart nginx

    echo "[INFO] Registering and starting ${appid}.service as systemd service"
    #cp  ${appdir}/app.service /etc/systemd/system/${appid}.service
    ln ${appdir}/app.service /etc/systemd/system/${appid}.service
    systemctl enable ${appid}
    systemctl start ${appid}

    echo "[INFO] Init comlete, check out https://${domain_name}"
fi

#if [[ "$*" == *all* ]] || [[  "$*" == *backend*  ]]; then
if [[  "$*" == *backend*  ]]; then
    echo "[INFO] Stopping ${appid}"
    systemctl stop ${appid}
    echo "[INFO] Pulling app.* artifacts from ${bucket_name}"
    aws s3 sync s3://${bucket_name}/deploy ${appdir} --exclude "*" --include "app.*"
    if [ ! -f  /etc/systemd/system/${appid}.service ]; then
        echo "[INFO] Creating symlink /etc/systemd/system/${appid}.service"
        ln ${appdir}/app.service /etc/systemd/system/${appid}.service
    fi
    ###################
    ## file logging
    if [ ! -f /etc/rsyslog.d/25-${appid}.conf ]; then
        ## https://stackoverflow.com/questions/37585758/how-to-redirect-output-of-systemd-service-to-a-file
        ## https://www.rsyslog.com/doc/v8-stable/configuration/filters.html can also use :syslogtag
         echo ":programname, isequal, \"${appid}\" ${appdir}/logs/stdout.log" >/etc/rsyslog.d/25-${appid}.conf ## create
         echo "& stop" >>/etc/rsyslog.d/25-${appid}.conf ## no need for a copy in s in /var/log/syslog
         echo "[INFO] File logging configured in /etc/rsyslog.d/25-${appid}.conf"
         systemctl restart rsyslog
    fi
    systemctl daemon-reload
    systemctl start ${appid}
    systemctl status ${appid}
fi

if [[  "$*" == *frontend*  ]]; then
    echo "[INFO] Pulling frontend and nginx.config from ${bucket_name}"
    aws s3 sync s3://${bucket_name}/deploy ${appdir} --exclude "*" --include "nginx.conf" --include "webapp.*"
    echo "[INFO] Cleaning /usr/share/nginx/html"
    rm -rf /usr/share/nginx/html/*
    echo "[INFO] Inflating ${appdir}/webapp.tgz"
    tar -C /usr/share/nginx/html -xf ${appdir}/webapp.tgz
    #unzip -q -o ${appdir}/webapp.zip -d /usr/share/nginx/html
    echo "[INFO] Restating nginx"
    systemctl restart nginx
    systemctl status nginx
fi

## experimental goals (call explicity, not run by all)
if [[  "$*" == *help* ]]; then
    echo "Help is arriving soon"
fi

if [[  "$*" == *security* ]]; then
     yum --security --quiet update # security updates only
fi



# curl http://169.254.169.254/latest/user-data ## get current user data
# echo "@reboot ec2-user /usr/bin/date >>/opt/letsgo2/logs/reboot.log" | sudo tee /etc/cron.d/reboot >/dev/null

# comment out the following line to allow CLOUDINIT messages through.
# systemctl show letsgo2 # last log messages
# journalctl -u letsgo2 -n 100 --no-pager

# aws s3 cp s3://timafe-letsgo2-data/deploy/cloud-init.sh . && chmod ugo+x cloud-init.sh
