## bucket for artifacts
resource "aws_s3_bucket" "data" {
  bucket = "${var.aws_s3_prefix}-${var.appid}-data"
  region = var.aws_region
  tags = map("Name", "${var.appid}-data", "appid", var.appid, "managedBy", "terraform")
}
