resource "aws_s3_bucket" "preference_tree_bucket" {
  bucket = "${var.preference_tree_bucket_name}"
  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "s3:GetObject"
      ],
      "Effect": "Allow",
      "Resource": "arn:aws:s3:::${var.preference_tree_bucket_name}/*",
      "Principal": {
        "AWS": [
          "${aws_iam_role.iam_for_lambda.arn}"
        ]
      }
    }
  ]
}
EOF
}

resource "aws_s3_bucket" "candidates_bucket" {
  bucket = "${var.candidates_bucket_name}"
  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "s3:GetObject"
      ],
      "Effect": "Allow",
      "Resource": "arn:aws:s3:::${var.candidates_bucket_name}/*",
      "Principal": {
        "AWS": [
          "${aws_iam_role.iam_for_lambda.arn}"
        ]
      }
    }
  ]
}
EOF
}

// TODO add preference tree objects
//resource "aws_s3_bucket_object" "object" {
//  bucket  = "${aws_s3_bucket.preference_tree_bucket.id}"
//  key     = "${var.object_name}"
//  content = "0"
//}
