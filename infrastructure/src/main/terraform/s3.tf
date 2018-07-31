resource "aws_s3_bucket" "recount_data_bucket" {
  bucket = "${var.recount_data_bucket_name}"
  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "s3:GetObject"
      ],
      "Effect": "Allow",
      "Resource": "arn:aws:s3:::${var.recount_data_bucket_name}/*",
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
