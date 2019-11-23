 #! /bin/bash
 # ATTENTION!!! Chaning user-data will result in destroy/create of the EC2 Instance
echo "[INFO] Preparing ${appdir} "
sudo mkdir -p ${appdir}
sudo chown ec2-user:ec2-user ${appdir}
INIT_SCRIPT="${appdir}/cloud-init.sh"
echo "[INFO] Pulling $INIT_SCRIPT from s3://${bucket_name}"
sudo aws s3 sync s3://${bucket_name}/deploy ${appdir}
chmod ugo+x $INIT_SCRIPT
echo "[INFO] Launching $INIT_SCRIPT"
sudo $INIT_SCRIPT
