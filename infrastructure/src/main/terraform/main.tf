provider "aws" {
  region = "ap-southeast-2"
}

data "aws_region" "current" {}

variable recount_lambda_zip_path {}

variable recount_data_bucket_name {
  default = "recount-data.buckets.ausvotes.info"
}

output "recount_data_bucket_name" {
  value = "${var.recount_data_bucket_name}"
}
