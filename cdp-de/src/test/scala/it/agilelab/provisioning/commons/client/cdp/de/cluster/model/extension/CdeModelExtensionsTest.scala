package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.extension

import com.cloudera.cdp.de.model.ServiceDescription
import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.extension.CdeModelExtensions.ServiceDescriptionOps
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class CdeModelExtensionsTest extends AnyFunSuite with MockFactory {

  test("getServiceUrl") {
    val serviceDescription = stub[ServiceDescription]
    (serviceDescription.getClusterFqdn _).when().returns("my-cluster-fqdn")
    assert(serviceDescription.getServiceUrl == "https://my-cluster-fqdn")
  }
}
