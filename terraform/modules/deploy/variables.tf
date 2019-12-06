variable "tags" {
    type = "map"
    description = "Tags to attached to the table, Name tag will be added by the module"
    default = {}
}

variable "appid" {
    description = "appid prefix for roles, users, buckets"
}

variable "bucket_name" {
    type = string
    description = "the deployment bucket"
}

variable "bucket_path" {
    type = string
    default = "deploy/"
    description = "optional path e.g. deploy/, can be blank to deploy to root"
}
