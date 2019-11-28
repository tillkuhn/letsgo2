## Main Entry point for terraform infrastructure
provider "aws" {
    region = "eu-central-1"
    version = "~> 2.34"
}

## see terraform-backend.tf.tmpl and remove extension
## to enable s3 backend for remote shared terraform state

## locate existing vpc by name
data "aws_vpc" "vpc" {
    filter {
        name = "tag:Name"
        values = [
            var.aws_vpc_name]
    }
}

## target subnet for ec2 instance
data "aws_subnet" "app_onea" {
    filter {
        name = "tag:Name"
        values = [
            var.aws_subnet_name]
    }
    vpc_id = data.aws_vpc.vpc.id
}

## expect dedicated security group to grant inbound ssh access
## should be as limited as possible, e.g. IP ranges of your ISP
data "aws_security_group" "ssh" {
    filter {
        name = "tag:Name"
        values = [
            var.aws_ssh_security_group_name]
    }
}
## Existing SSH Pub key for instance (BYOK)
## make sure you have access to the private key (and don't put it to version control)
resource "aws_key_pair" "ssh_key" {
    key_name = var.appid
    public_key = file(var.ssh_pubkey_file)
}

## NEW modules support
module "dynamodb" {
    source = "./modules/dynamodb"
    name = "${var.appid}-country"
    tags =  map("Name", "${var.appid}-country", "appid", var.appid, "managedBy", "terraform")
}
