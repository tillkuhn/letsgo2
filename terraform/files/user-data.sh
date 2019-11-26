#!/usr/bin/env bash
# ATTENTION!!! Chaning user-data will result in destroy/recreate of the EC2 Instance
INIT_SCRIPT="cloud-init.sh"
echo "[INFO] Preparing appdir ${appdir} "
sudo mkdir -p ${appdir}/logs
sudo chown ec2-user:ec2-user ${appdir}
grep -q "alias l='ls -CF'" /home/ec2-user/.bashrc || echo "alias l='ls -CF'" >>/home/ec2-user/.bashrc
grep -q "cd ${appdir}" /home/ec2-user/.bashrc || echo "cd ${appdir}" >>/home/ec2-user/.bashrc

echo "[INFO] Pulling $INIT_SCRIPT from s3://${bucket_name}/deploy"
sudo aws s3 sync s3://${bucket_name}/deploy ${appdir} --exclude "*" --include $INIT_SCRIPT
chmod ugo+x ${appdir}/$INIT_SCRIPT

echo "[INFO] Launching ${appdir}/$INIT_SCRIPT"
sudo ${appdir}/$INIT_SCRIPT all

