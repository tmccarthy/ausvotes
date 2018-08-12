resource "aws_cloudwatch_log_group" "recount_log_group" {
  name = "recount_log_group"
}

resource "aws_lambda_function" "recount" {
  filename         = "${var.recount_lambda_zip_path}"
  function_name    = "recount"
  role             = "${aws_iam_role.iam_for_lambda.arn}"
  handler          = "au.id.tmm.ausvotes.lambdas.recount.RecountLambda"
  source_code_hash = "${base64sha256(file(var.recount_lambda_zip_path))}"
  runtime          = "java8"
  timeout          = 300
  memory_size      = 3008

  environment {
    variables = {
      RECOUNT_DATA_BUCKET = "${aws_s3_bucket.recount_data_bucket.id}",
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

