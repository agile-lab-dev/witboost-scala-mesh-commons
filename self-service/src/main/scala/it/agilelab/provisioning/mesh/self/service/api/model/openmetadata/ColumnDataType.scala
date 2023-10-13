package it.agilelab.provisioning.mesh.self.service.api.model.openmetadata

import io.circe.{ Decoder, Encoder, Json }

/** ColumnDataType sealed trait defined according to openmetadata
  * from https://docs.open-metadata.org/metadata-standard/schemas/entities/table#datatype
  */
sealed trait ColumnDataType extends Product with Serializable

object ColumnDataType {

  case object NUMBER     extends ColumnDataType
  case object TINYINT    extends ColumnDataType
  case object SMALLINT   extends ColumnDataType
  case object INT        extends ColumnDataType
  case object BIGINT     extends ColumnDataType
  case object BYTEINT    extends ColumnDataType
  case object BYTES      extends ColumnDataType
  case object FLOAT      extends ColumnDataType
  case object DOUBLE     extends ColumnDataType
  case object DECIMAL    extends ColumnDataType
  case object NUMERIC    extends ColumnDataType
  case object TIMESTAMP  extends ColumnDataType
  case object TIME       extends ColumnDataType
  case object DATE       extends ColumnDataType
  case object DATETIME   extends ColumnDataType
  case object INTERVAL   extends ColumnDataType
  case object STRING     extends ColumnDataType
  case object MEDIUMTEXT extends ColumnDataType
  case object TEXT       extends ColumnDataType
  case object CHAR       extends ColumnDataType
  case object VARCHAR    extends ColumnDataType
  case object BOOLEAN    extends ColumnDataType
  case object BINARY     extends ColumnDataType
  case object VARBINARY  extends ColumnDataType
  case object ARRAY      extends ColumnDataType
  case object BLOB       extends ColumnDataType
  case object LONGBLOB   extends ColumnDataType
  case object MEDIUMBLOB extends ColumnDataType
  case object MAP        extends ColumnDataType
  case object STRUCT     extends ColumnDataType
  case object UNION      extends ColumnDataType
  case object SET        extends ColumnDataType
  case object GEOGRAPHY  extends ColumnDataType
  case object ENUM       extends ColumnDataType
  case object JSON       extends ColumnDataType

  implicit val columnDataTypeEncoder: Encoder[ColumnDataType] = {
    case NUMBER     => Json.fromString("NUMBER")
    case TINYINT    => Json.fromString("TINYINT")
    case SMALLINT   => Json.fromString("SMALLINT")
    case INT        => Json.fromString("INT")
    case BIGINT     => Json.fromString("BIGINT")
    case BYTEINT    => Json.fromString("BYTEINT")
    case BYTES      => Json.fromString("BYTES")
    case FLOAT      => Json.fromString("FLOAT")
    case DOUBLE     => Json.fromString("DOUBLE")
    case DECIMAL    => Json.fromString("DECIMAL")
    case NUMERIC    => Json.fromString("NUMERIC")
    case TIMESTAMP  => Json.fromString("TIMESTAMP")
    case TIME       => Json.fromString("TIME")
    case DATE       => Json.fromString("DATE")
    case DATETIME   => Json.fromString("DATETIME")
    case INTERVAL   => Json.fromString("INTERVAL")
    case STRING     => Json.fromString("STRING")
    case MEDIUMTEXT => Json.fromString("MEDIUMTEXT")
    case TEXT       => Json.fromString("TEXT")
    case CHAR       => Json.fromString("CHAR")
    case VARCHAR    => Json.fromString("VARCHAR")
    case BOOLEAN    => Json.fromString("BOOLEAN")
    case BINARY     => Json.fromString("BINARY")
    case VARBINARY  => Json.fromString("VARBINARY")
    case ARRAY      => Json.fromString("ARRAY")
    case BLOB       => Json.fromString("BLOB")
    case LONGBLOB   => Json.fromString("LONGBLOB")
    case MEDIUMBLOB => Json.fromString("MEDIUMBLOB")
    case MAP        => Json.fromString("MAP")
    case STRUCT     => Json.fromString("STRUCT")
    case UNION      => Json.fromString("UNION")
    case SET        => Json.fromString("SET")
    case GEOGRAPHY  => Json.fromString("GEOGRAPHY")
    case ENUM       => Json.fromString("ENUM")
    case JSON       => Json.fromString("JSON")
  }

  implicit val columnDataTypeDecoder: Decoder[ColumnDataType] = Decoder[String].emap {
    case "NUMBER" | "number"         => Right(NUMBER)
    case "TINYINT" | "tinyint"       => Right(TINYINT)
    case "SMALLINT" | "smallint"     => Right(SMALLINT)
    case "INT" | "int"               => Right(INT)
    case "BIGINT" | "bigint"         => Right(BIGINT)
    case "BYTEINT" | "byteint"       => Right(BYTEINT)
    case "BYTES" | "bytes"           => Right(BYTES)
    case "FLOAT" | "float"           => Right(FLOAT)
    case "DOUBLE" | "double"         => Right(DOUBLE)
    case "DECIMAL" | "decimal"       => Right(DECIMAL)
    case "NUMERIC" | "numeric"       => Right(NUMERIC)
    case "TIMESTAMP" | "timestamp"   => Right(TIMESTAMP)
    case "TIME" | "time"             => Right(TIME)
    case "DATE" | "date"             => Right(DATE)
    case "DATETIME" | "datetime"     => Right(DATETIME)
    case "INTERVAL" | "interval"     => Right(INTERVAL)
    case "STRING" | "string"         => Right(STRING)
    case "MEDIUMTEXT" | "mediumtext" => Right(MEDIUMTEXT)
    case "TEXT" | "text"             => Right(TEXT)
    case "CHAR" | "char"             => Right(CHAR)
    case "VARCHAR" | "varchar"       => Right(VARCHAR)
    case "BOOLEAN" | "boolean"       => Right(BOOLEAN)
    case "BINARY" | "binary"         => Right(BINARY)
    case "VARBINARY" | "varbinary"   => Right(VARBINARY)
    case "ARRAY" | "array"           => Right(ARRAY)
    case "BLOB" | "blob"             => Right(BLOB)
    case "LONGBLOB" | "longblob"     => Right(LONGBLOB)
    case "MEDIUMBLOB" | "mediumblob" => Right(MEDIUMBLOB)
    case "MAP" | "map"               => Right(MAP)
    case "STRUCT" | "struct"         => Right(STRUCT)
    case "UNION" | "union"           => Right(UNION)
    case "SET" | "set"               => Right(SET)
    case "GEOGRAPHY" | "geography"   => Right(GEOGRAPHY)
    case "ENUM" | "enum"             => Right(ENUM)
    case "JSON" | "json"             => Right(JSON)
    case other                       => Left(s"Invalid column constraint: $other")
  }
}
