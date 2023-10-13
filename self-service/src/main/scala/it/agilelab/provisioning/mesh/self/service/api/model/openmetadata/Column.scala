package it.agilelab.provisioning.mesh.self.service.api.model.openmetadata

/** Column case class defined according to
  * the open metadata specifications available
  * from https://docs.open-metadata.org/metadata-standard/schemas/entities/table#column
  *
  * Only the 'tags' field differs from the specifications
  */
final case class Column(
  name: String,
  dataType: ColumnDataType,
  arrayDataType: Option[ColumnDataType],
  dataLength: Option[Int],
  dataTypeDisplay: Option[String],
  description: Option[String],
  fullyQualifiedName: Option[String],
  tags: Option[Seq[String]],
  constraint: Option[ColumnConstraint],
  ordinalPosition: Option[Int],
  jsonSchema: Option[String],
  children: Option[Seq[Column]]
)
