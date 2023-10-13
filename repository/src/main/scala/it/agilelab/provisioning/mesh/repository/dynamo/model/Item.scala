package it.agilelab.provisioning.mesh.repository.dynamo.model

import software.amazon.awssdk.services.dynamodb.model.AttributeValue

final case class Item(values: Map[String, AttributeValue])
