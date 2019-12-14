variable "appid" {
    description = "appid prefix for rolenames"
}

variable "tags" {
    type = map
    description = "Tags to attached to the table, Name tag will be added by the module"
    default = {}
}

variable "user_pool_name" {
    type = string
}
