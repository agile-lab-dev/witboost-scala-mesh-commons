package it.agilelab.provisioning.aws.secrets.gateway.model

final case class AwsSecret(arn: String, name: String, value: String)
