## see also group_vars/all/vars.yml and stage specific subfolders group_vars/??/vars.yml

variable "aws_region" {
  type = string
  default = "eu-central-1"
}

variable "aws_vpc_name" {
  type = string
  description = "Name tag of your vpc"
}

variable "aws_subnet_name" {
  type = string
  description = "Name tag of your subnet"
}

variable "aws_ssh_security_group_name" {
  type = string
  description = "Name of the security group that manages ssh access into your instance"
}

variable "aws_instance_type" {
  type = string
  description = "type of the EC2 instance"
  default = "t3a.nano"
}

variable "appid" {
  type = string
  description = "The Applicaction Id"
}

variable "aws_s3_prefix" {
  type = string
  description = "Prefix for s3 buckets to make them unique e.g. domain"
}

## Amazon Linux 2 AMI (HVM), SSD Volume Type (64-bit x86)
variable "aws_instance_ami" {
  type = string
  default = "ami-0f3a43fbf2d3899f7" ## aws linux
  #default = "ami-07e308cdb030da01e" ## https://coreos.com/os/docs/latest/booting-on-ec2.html
}

variable "ssh_pubkey_file" {
  type = string
  description = "The path to the ssh pub key"
  default = "files/mykey.pem.pub"
}

variable "hosted_zone_id" {
  type = string
  description = "The id of the zone in Route53"
}

variable "domain_name" {
  type = string
  description = "The fully qualified domain e.g. xyz.example.com"
}

variable "certbot_mail" {
  type = string
  description = "mail for certbot interaction"
}

## oauth
variable oauth2_issuer_uri {
    type = string
    description = "e.g. https://cognito-idp.eu-central-1.amazonaws.com/eu-central-somepool"
}

variable oauth2_client_id {
    type = string
}

variable oauth2_client_secret {
    type = string
}

variable dynamodb_table_prefix {
    description = "Prefix for your dynamodb table names e.g. myapp-, preferably use appid-"
    type = string
}
