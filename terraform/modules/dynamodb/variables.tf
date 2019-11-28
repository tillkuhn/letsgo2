variable "name" {
    description = "the name of the table"
}

variable "wcu" {
    description = "Default write capacity units defaults to 1"
    default = "1"
}

variable "rcu" {
    description = "Default read capacity units defaults to 1"
    default = "1"
}

variable "tags" {
    type = "map"
    default = {}
}

variable "primary_key" {
    default = "id"
}


variable "primary_key_type" {
    default = "S"
}
