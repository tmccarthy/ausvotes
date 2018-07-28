provider "aws" {
  region = "ap-southeast-2"
}

data "aws_region" "current" {}

variable recount_lambda_zip_path {}

variable preference_tree_bucket_name {
  default = "preference-trees.buckets.ausvotes.info"
}

variable candidates_bucket_name {
  default = "candidates.buckets.ausvotes.info"
}
