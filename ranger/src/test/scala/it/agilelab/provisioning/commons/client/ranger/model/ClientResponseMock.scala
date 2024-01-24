package it.agilelab.provisioning.commons.client.ranger.model

import com.sun.jersey.api.client.ClientResponse
import org.scalamock.scalatest.MockFactory

object ClientResponseMock extends MockFactory {

  /** Helper to mock ClientResponse, as we cannot instantiate as it is a complex object,
    * so we mock it and add handlers for the relevant methods
    */
  def apply(status: ClientResponse.Status, message: String = ""): ClientResponse = {
    val response: ClientResponse = stub[ClientResponse]
    (response.getStatus _).when().returns(status.getStatusCode)
    (response.getEntity(_: Class[String])).when(classOf[String]).returns(message)
    response
  }
}
