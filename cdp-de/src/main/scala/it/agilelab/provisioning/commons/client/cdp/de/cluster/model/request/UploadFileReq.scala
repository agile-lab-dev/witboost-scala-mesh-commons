package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.request

final case class UploadFileReq(
  resource: String,
  filePath: String,
  mimeType: String,
  file: Array[Byte]
)
