resource "aws_api_gateway_rest_api" "recount_api" {
  name = "ausvotes"
}

resource "aws_api_gateway_deployment" "recount_api_deployment" {
  depends_on = ["aws_api_gateway_integration.integration"]
  rest_api_id = "${aws_api_gateway_rest_api.recount_api.id}"
  stage_name = "prod"
}

resource "aws_api_gateway_usage_plan" "recount_usage_plan" {
  name         = "recount-usage-plan"

  api_stages {
    api_id = "${aws_api_gateway_rest_api.recount_api.id}"
    stage  = "${aws_api_gateway_deployment.recount_api_deployment.stage_name}"
  }

  quota_settings {
    limit  = 25
    offset = 2
    period = "DAY"
  }
}

resource "aws_api_gateway_resource" "recount_resource" {
  path_part = "recount"
  parent_id = "${aws_api_gateway_rest_api.recount_api.root_resource_id}"
  rest_api_id = "${aws_api_gateway_rest_api.recount_api.id}"
}

resource "aws_api_gateway_resource" "recount_election_resource" {
  path_part = "{election}"
  parent_id = "${aws_api_gateway_resource.recount_resource.id}"
  rest_api_id = "${aws_api_gateway_rest_api.recount_api.id}"
}

resource "aws_api_gateway_resource" "recount_election_state_resource" {
  path_part = "{state}"
  parent_id = "${aws_api_gateway_resource.recount_election_resource.id}"
  rest_api_id = "${aws_api_gateway_rest_api.recount_api.id}"
}

resource "aws_api_gateway_method" "recount_election_state_resource_method" {
  rest_api_id   = "${aws_api_gateway_rest_api.recount_api.id}"
  resource_id   = "${aws_api_gateway_resource.recount_election_state_resource.id}"
  http_method   = "GET"
  authorization = "NONE"

  request_parameters {
    method.request.path.election = true
    method.request.path.state = true
    method.request.querystring.vacancies = false
    method.request.querystring.ineligibleCandidates = false
  }
}

resource "aws_api_gateway_integration" "integration" {
  rest_api_id             = "${aws_api_gateway_rest_api.recount_api.id}"
  resource_id             = "${aws_api_gateway_resource.recount_election_state_resource.id}"
  http_method             = "${aws_api_gateway_method.recount_election_state_resource_method.http_method}"
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.recount.arn}/invocations"

  request_templates {
    "application/json" = <<EOF
{
   "body" : $input.json('$')
}
EOF
  }
}
