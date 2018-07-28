resource "aws_lambda_function" "recount" {
  filename         = "${var.recount_lambda_zip_path}"
  function_name    = "recount"
  role             = "${aws_iam_role.iam_for_lambda.arn}"
  handler          = "au.id.tmm.ausvotes.lambda.recount.RecountLambda"
  source_code_hash = "${base64sha256(file(var.recount_lambda_zip_path))}"
  runtime          = "java8"
  timeout          = 30
  memory_size      = 256

  environment {
    variables = {
      PREFERENCES_BUCKET = "${aws_s3_bucket.preference_tree_bucket.id}",
      CANDIDATES_BUCKET = "${aws_s3_bucket.candidates_bucket.id}"
    }
  }
}

resource "aws_lambda_permission" "recount" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = "${aws_lambda_function.recount.arn}"
  principal     = "apigateway.amazonaws.com"

  # More: http://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-control-access-using-iam-policies-to-invoke-api.html
  source_arn = "${aws_api_gateway_rest_api.recount_api.execution_arn}/*/*/*"
}

