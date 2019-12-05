output "role_user_arn" {
    value       = aws_iam_role.user.arn
    description = "The ARN of the user role"
}

output "role_guest_arn" {
    value       = aws_iam_role.guest.arn
    description = "The ARN of the user role"
}

output "role_admin_arn" {
    value       = aws_iam_role.admin.arn
    description = "The ARN of the user role"
}
