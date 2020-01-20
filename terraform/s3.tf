## s3 bucket for deploy/* artifacts
resource "aws_s3_bucket" "data" {
    bucket = "${var.aws_s3_prefix}-${var.appid}-data"
    region = var.aws_region
    tags = map("Name", "${var.appid}-data", "appid", var.appid, "managedBy", "terraform")
}

## bucket object resources from /files
resource "aws_s3_bucket_object" "appserviceenv" {
    bucket = aws_s3_bucket.data.bucket
    key = "deploy/app.service.env"
    content = templatefile("${path.module}/files/app.service.env", {
        domain_name = var.domain_name,
        appdir = "/opt/${var.appid}",
        appid = var.appid,
        oauth2_client_id = var.oauth2_client_id,
        oauth2_client_secret = var.oauth2_client_secret,
        oauth2_issuer_uri = var.oauth2_issuer_uri
        dynamodb_table_prefix = var.dynamodb_table_prefix
    })
    storage_class = "REDUCED_REDUNDANCY"
}

resource "aws_s3_bucket_object" "appservice" {
    bucket = aws_s3_bucket.data.bucket
    key = "deploy/app.service"
    content = templatefile("${path.module}/files/app.service", {
        domain_name = var.domain_name,
        appdir = "/opt/${var.appid}",
        appid = var.appid,
        bucket_name = aws_s3_bucket.data.bucket,
        oauth2_client_id = var.oauth2_client_id,
        oauth2_client_secret = var.oauth2_client_secret,
        oauth2_issuer_uri = var.oauth2_issuer_uri
    })
    storage_class = "REDUCED_REDUNDANCY"
}

resource "aws_s3_bucket_object" "installscript" {
    bucket = aws_s3_bucket.data.bucket
    key = "deploy/cloud-init.sh"
    content = templatefile("${path.module}/files/cloud-init.sh", {
        certbot_mail = var.certbot_mail,
        domain_name = var.domain_name,
        bucket_name = aws_s3_bucket.data.bucket,
        appdir = "/opt/${var.appid}",
        appid = var.appid
    })
    storage_class = "REDUCED_REDUNDANCY"
}

resource "aws_s3_bucket_object" "nginxconf" {
    bucket = aws_s3_bucket.data.bucket
    key = "deploy/nginx.conf"
    content = templatefile("${path.module}/files/nginx.conf", {
        domain_name = var.domain_name,
        appdir = "/opt/${var.appid}"
    })
    storage_class = "REDUCED_REDUNDANCY"
}

