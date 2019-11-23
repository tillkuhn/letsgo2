 #! /bin/bash
 # ATTENTION!!! Chaning user-data will result in destroy/create of the EC2 Instance
sudo aws s3 sync s3://${bucket_name}/deploy ${appdir}
INIT_SCRIPT="${appdir}/cloud-init.sh"
chmod ugo+x $INIT_SCRIPT
echo "[INFO] Launching $INIT_SCRIPT"
sudo $INIT_SCRIPT
