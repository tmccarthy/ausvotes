resource "aws_api_gateway_rest_api" "recount_api" {
  name = "recount"
}

resource "aws_api_gateway_deployment" "recount_api_deployment" {
  depends_on = ["aws_api_gateway_integration.integration"]
  rest_api_id = "${aws_api_gateway_rest_api.recount_api.id}"
  stage_name = "prod"
}

resource "aws_api_gateway_method_settings" "recount_api_gateway_method_settings" {
  rest_api_id = "${aws_api_gateway_rest_api.recount_api.id}"
  stage_name  = "${aws_api_gateway_deployment.recount_api_deployment.stage_name}"
  method_path = "${aws_api_gateway_resource.resource.path_part}/${aws_api_gateway_method.method.http_method}"

  settings {
    metrics_enabled = false
  }
}

resource "aws_api_gateway_resource" "resource" {
  path_part = "display"
  parent_id = "${aws_api_gateway_rest_api.recount_api.root_resource_id}"
  rest_api_id = "${aws_api_gateway_rest_api.recount_api.id}"
}

resource "aws_api_gateway_method" "method" {
  rest_api_id   = "${aws_api_gateway_rest_api.recount_api.id}"
  resource_id   = "${aws_api_gateway_resource.resource.id}"
  http_method   = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "integration" {
  rest_api_id             = "${aws_api_gateway_rest_api.recount_api.id}"
  resource_id             = "${aws_api_gateway_resource.resource.id}"
  http_method             = "${aws_api_gateway_method.method.http_method}"
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2018-07-28/functions/${aws_lambda_function.recount.arn}/invocations"

  request_templates {
    "application/json" = ""
  }
}
