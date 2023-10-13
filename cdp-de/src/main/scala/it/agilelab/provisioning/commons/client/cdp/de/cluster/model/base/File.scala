package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.base

import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.constant.MimeType._

/** File model
  *
  * from cde swagger API doc.
  *
  * @param resource: [[String]] resource name
  * @param filePath: [[String]] file path
  * @param mimeType: [[String]] mime type
  * @param file: file content as byte array
  */
final case class File(
  resource: String,
  filePath: String,
  mimeType: String,
  file: Array[Byte]
)

/** File companion object
  *
  * contains some utilities method to create specific File type
  */
object File {

  /** Create a File instance that describe a jar file
    * @param resource: [[String]] resource name
    * @param filePath: [[String]] file path
    * @param file: file content as Array[Byte]
    * @return File
    */
  def jar(
    resource: String,
    filePath: String,
    file: Array[Byte]
  ): File =
    File(resource, filePath, JAVA_ARCHIVE, file)

  /** Create a File instance that describe a text file
    * @param resource: [[String]] resource name
    * @param filePath: [[String]] file path
    * @param file: file content as Array[Byte]
    * @return File
    */
  def file(
    resource: String,
    filePath: String,
    file: Array[Byte]
  ): File =
    File(resource, filePath, FILE, file)
}
