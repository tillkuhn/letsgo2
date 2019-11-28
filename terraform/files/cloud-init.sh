#!/usr/bin/env bash
echo "[INFO] Running cloud-init custom script in $1 mode"
if [ "$EUID" -ne 0 ]
  then echo "[FATAL] Detected UID $UID, please run with sudo"
  exit
fi

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
systemctl restart nginx
echo "[INFO] Launching certbot for ${domain_name}"
certbot --nginx -m ${certbot_mail} --agree-tos --redirect -n -d ${domain_name}

echo "[INFO] Backup /etc/letsencrypt to s3://${bucket_name}"
tar -C /etc -zcf /tmp/letsencrypt.tar.gz letsencrypt
aws s3 cp --sse=AES256 /tmp/letsencrypt.tar.gz s3://${bucket_name}/letsencrypt/letsencrypt.tar.gz

echo "[INFO] Replacing system nginx.conf with enhanced version ..."
cp  ${appdir}/nginx.conf /etc/nginx/nginx.conf
# curl http://169.254.169.254/latest/user-data

##
echo "[INFO] Registering and starting ${appid}.service as systemd service"
cp  ${appdir}/app.service /etc/systemd/system/${appid}.service
systemctl enable ${appid}
systemctl start ${appid}
