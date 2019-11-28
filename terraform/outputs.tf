## output private ip
output "table_country_arn" {
    value = "${module.table_country.arn}"
}

output "instance_id" {
    value = "Instance Id: ${aws_instance.instance.id}"
}

output "ssh_string" {
    value = "ssh -i mykey.pem ec2-user@${aws_instance.instance.public_ip}"
}


## convert files first to substitute variables
resource "local_file" "setenv_sh" {
    content = "# ${var.appid} runtime variables\ninstance_id=${aws_instance.instance.id}\npublic_ip=${aws_instance.instance.public_ip}\n"
    filename = "${path.module}/local/setenv.sh"
}
