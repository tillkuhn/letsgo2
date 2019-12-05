## https://docs.aws.amazon.com/cognito/latest/developerguide/role-based-access-control.html
//      "Condition": {
//        "StringEquals": {
//          "cognito-identity.amazonaws.com:aud": "${aws_cognito_identity_pool.main.id}"
//        },
//        "ForAnyValue:StringLike": {
//          "cognito-identity.amazonaws.com:amr": "authenticated"
//        }
//      }

locals {
    common_tags = map("terraformModule", "cognito")
}

resource "aws_cognito_user_pool" "main" {
    name = var.appid
    tags = merge(local.common_tags,var.tags)
}

output "pool_id" {
    value = aws_cognito_user_pool.main.id
}
resource "aws_iam_role" "user" {
    name = "${var.appid}-cognito-role-user"
    tags = merge(local.common_tags,var.tags)
    assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "cognito-identity.amazonaws.com"
      },
      "Action": "sts:AssumeRoleWithWebIdentity"
    }
  ]
}
EOF

}


resource "aws_iam_role" "guest" {
    name = "${var.appid}-cognito-role-guest"
    tags = merge(local.common_tags,var.tags)
    assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "cognito-identity.amazonaws.com"
      },
      "Action": "sts:AssumeRoleWithWebIdentity"
    }
  ]
}
EOF
}

resource "aws_iam_role" "admin" {
    name = "${var.appid}-cognito-role-admin"
    tags = merge(local.common_tags,var.tags)
    assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "cognito-identity.amazonaws.com"
      },
      "Action": "sts:AssumeRoleWithWebIdentity"
    }
  ]
}
EOF
}
