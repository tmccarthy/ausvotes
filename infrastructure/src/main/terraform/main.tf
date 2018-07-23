provider "aws" {
  region = "ap-southeast-2"
}

resource "aws_s3_bucket" "preference_trees_bucket" {
  bucket = "preference-trees"
}

