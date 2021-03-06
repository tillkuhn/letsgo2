#https://www.terraform.io/docs/providers/aws/r/iam_user.html
locals {
    common_tags = map("terraformModule", "deploy")
}

resource "aws_iam_user" "deploy" {
    name = "${var.appid}-deploy"
    #path = "/system/"

    tags = merge(local.common_tags,var.tags)
}

//resource "aws_iam_access_key" "deploy" {
//    user = aws_iam_user.deploy.name
//}

resource "aws_iam_user_policy" "deploy" {
    name = "${var.appid}-deploy-policy"
    user = aws_iam_user.deploy.name

    policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "AllowInstanceToListBuckets",
      "Action": ["s3:ListBucket"],
      "Effect": "Allow",
      "Resource": [ "arn:aws:s3:::${var.bucket_name}" ]
    },
    {
      "Sid": "AllowDeployUserToUploadFiles",
      "Effect": "Allow",
      "Action": [
        "s3:DeleteObject",
        "s3:GetBucketLocation",
        "s3:GetObject",
        "s3:ListBucket",
        "s3:PutObject"
      ],
      "Resource": [
         "arn:aws:s3:::${var.bucket_name}/${var.bucket_path}*"
        ]
    }
  ]
}
EOF
}
