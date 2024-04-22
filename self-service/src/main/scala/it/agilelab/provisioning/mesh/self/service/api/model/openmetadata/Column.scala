package it.agilelab.provisioning.mesh.self.service.api.model.openmetadata

/** Column case class defined according to
  * the open metadata specifications available
  * from https://docs.open-metadata.org/v1.3.x/main-concepts/metadata-standard/schemas/entity/data/table
  */
final case class Column(
  name: String,
  dataType: ColumnDataType,
  arrayDataType: Option[ColumnDataType],
  dataLength: Option[Int],
  dataTypeDisplay: Option[String],
  description: Option[String],
  fullyQualifiedName: Option[String],
  tags: Option[Seq[Tag]],
  constraint: Option[ColumnConstraint],
  ordinalPosition: Option[Int],
  jsonSchema: Option[String],
  children: Option[Seq[Column]]
)
