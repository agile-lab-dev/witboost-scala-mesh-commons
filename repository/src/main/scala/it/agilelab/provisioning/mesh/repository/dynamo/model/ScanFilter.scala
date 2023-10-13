package it.agilelab.provisioning.mesh.repository.dynamo.model

import software.amazon.awssdk.services.dynamodb.model.AttributeValue

final case class ScanFilter(filterExpression: String, filterAttributeValues: Map[String, AttributeValue])
