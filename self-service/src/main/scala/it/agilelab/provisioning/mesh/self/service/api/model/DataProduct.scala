package it.agilelab.provisioning.mesh.self.service.api.model

import io.circe.Json

final case class DataProduct[SPECIFIC](
  id: String,
  name: String,
  domain: String,
  environment: String,
  version: String,
  dataProductOwner: String,
  specific: SPECIFIC,
  components: Seq[Json]
)
