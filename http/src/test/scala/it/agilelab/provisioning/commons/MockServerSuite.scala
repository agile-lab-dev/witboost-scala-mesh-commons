package it.agilelab.provisioning.commons

import org.mockserver.integration.ClientAndServer
import org.mockserver.integration.ClientAndServer.startClientAndServer
import org.mockserver.mock.Expectation
import org.mockserver.model.Header.header
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.{ HttpRequest, JsonBody }
import org.scalatest.{ BeforeAndAfterAll, Suite }

import java.net.ServerSocket
import scala.util.Using

trait MockServerSuite extends BeforeAndAfterAll {
  this: Suite =>

  var srvProtocol: String      = "http"
  var srvHost: String          = "localhost"
  var srvPort: Int             = _
  var srvUri: String           = _
  var mockSrv: ClientAndServer = _

  override protected def beforeAll(): Unit = {
    Using(new ServerSocket(0))(_.getLocalPort).map { p =>
      srvPort = p
      mockSrv = startClientAndServer(p)
    }
    srvUri = s"$srvProtocol://$srvHost:$srvPort"
    super.beforeAll()
  }

  override protected def afterAll(): Unit = {
    mockSrv.stop()
    super.afterAll()
  }

  /** Mock a GET request to a specific path with specific headers
    * @param path: path of the request
    * @param headers: headers on the request
    * @param responseCode: response code
    * @param responseBody: response body
    */
  def mockGetReq(
    path: String,
    headers: Map[String, String],
    responseCode: Int,
    responseBody: Option[String]
  ): Unit =
    mockReq("GET", path, headers, None, None, responseCode, responseBody)

  /** Mock a POST request to a specific path with specific headers
    * @param path: path of the request
    * @param headers: headers on the request
    * @param body: post request body
    * @param responseCode: response code
    * @param responseBody: response body
    */
  def mockPostReq(
    path: String,
    headers: Map[String, String],
    body: String,
    responseCode: Int,
    responseBody: Option[String]
  ): Unit =
    mockReq("POST", path, headers, Some(body), None, responseCode, responseBody)

  /** Mock a PUT request to a specific path with specific headers
    * @param path: path of the request
    * @param headers: headers on the request
    * @param body: PUT request body
    * @param responseCode: response code
    * @param responseBody: response body
    */
  def mockPutReq(
    path: String,
    headers: Map[String, String],
    body: String,
    responseCode: Int,
    responseBody: Option[String]
  ): Unit =
    mockReq("PUT", path, headers, Some(body), None, responseCode, responseBody)

  /** Mock a PATCH request to a specific path with specific headers
    * @param path: path of the request
    * @param headers: headers on the request
    * @param body: PUT request body
    * @param responseCode: response code
    * @param responseBody: response body
    */
  def mockPatchReq(
    path: String,
    headers: Map[String, String],
    body: String,
    responseCode: Int,
    responseBody: Option[String]
  ): Unit =
    mockReq("PATCH", path, headers, Some(body), None, responseCode, responseBody)

  /** Mock a Put multipart request to a specific path with specific headers
    * @param path: path of the request
    * @param headers: headers on the request
    * @param substringBody: PUT request body
    * @param responseCode: response code
    * @param responseBody: response body
    */
  def mockPutMultiReq(
    path: String,
    headers: Map[String, String],
    substringBody: String,
    responseCode: Int,
    responseBody: Option[String]
  ): Unit =
    mockReq("PUT", path, headers, None, Some(substringBody), responseCode, responseBody)

  /** Mock a DELETE request to a specific path with specific headers
    *
    * @param path: path of the request
    * @param headers: headers on the request
    * @param responseCode: response code
    * @param responseBody: response body
    */
  def mockDeleteReq(
    path: String,
    headers: Map[String, String],
    responseCode: Int,
    responseBody: Option[String]
  ): Unit =
    mockReq("DELETE", path, headers, None, None, responseCode, responseBody)

  private def mockReq(
    method: String,
    path: String,
    headers: Map[String, String],
    body: Option[String],
    substringBody: Option[String],
    responseCode: Int,
    responseBody: Option[String]
  ): Array[Expectation] =
    mockSrv
      .when(req(method, path, headers, body))
      .respond(response(responseBody.orNull).withStatusCode(responseCode))

  private def req(
    method: String,
    path: String,
    headers: Map[String, String],
    body: Option[String]
  ): HttpRequest = {
    val defaultReq =
      request(path).withMethod(method).withHeaders(headers.map(e => header(e._1, e._2)).toSeq: _*)
    body.map(b => defaultReq.withBody(new JsonBody(b))).getOrElse(defaultReq)
  }

}
