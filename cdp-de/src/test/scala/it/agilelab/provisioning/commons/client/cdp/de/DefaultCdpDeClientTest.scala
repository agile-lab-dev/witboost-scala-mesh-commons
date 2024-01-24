package it.agilelab.provisioning.commons.client.cdp.de

import com.cloudera.cdp.de.api.DeClient
import com.cloudera.cdp.de.model._
import it.agilelab.provisioning.commons.client.cdp.de.CdpDeClientError.{ ServiceNotFound, VcNotFound }
import it.agilelab.provisioning.commons.client.cdp.de.wrapper.DeClientWrapper
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class DefaultCdpDeClientTest extends AnyFunSuite with MockFactory with CdpDeClientTestSupport {

  val deClientWrapper: DeClientWrapper = mock[DeClientWrapper]
  val cdpDeClient: DefaultCdpDeClient  = new DefaultCdpDeClient(deClientWrapper)
  val service1                         = new ServiceSummary()
  val service2                         = new ServiceSummary()
  val service3                         = new ServiceSummary()
  val vc1                              = new VcSummary()
  val vc2                              = new VcSummary()

  service1.setClusterId("serviceId1")
  service1.setName("service1")
  service1.setStatus("ClusterCreationCompleted")

  service2.setClusterId("serviceId2")
  service2.setName("service2")
  service2.setStatus("ClusterDeletionCompleted")

  service3.setClusterId("serviceId3")
  service3.setName("service3")
  service3.setStatus("ClusterCreationCompleted")

  vc1.setClusterId("serviceId1")
  vc1.setVcId("vcId1")
  vc1.setVcName("vc1")

  vc2.setClusterId("serviceId1")
  vc2.setVcId("vcId2")
  vc2.setVcName("vc2")

  val services = Seq(service1, service2, service3)

  test("findAllServices return Right") {

    (deClientWrapper.listServices _)
      .expects(*)
      .onCall { (listServicesRequest: ListServicesRequest) =>
        if (listServicesRequest == null)
          null
        else if (listServicesRequest.getRemoveDeleted)
          services.filter(s => s.getStatus != "ClusterDeletionCompleted")
        else
          services
      }

    val actual = cdpDeClient.findAllServices()

    val expected = Right(Seq(service1, service3))

    assert(actual == expected)
  }

  test("findAllServices return Left") {
    val err                 = new IllegalArgumentException("x")
    val listServicesRequest = new ListServicesRequest
    listServicesRequest.setRemoveDeleted(true)

    (deClientWrapper.listServices _)
      .expects(listServicesRequest)
      .throws(err)

    val actual = cdpDeClient.findAllServices()
    assertFindAllServiceErr(actual, "x")
  }

  test("findServiceByName return Right") {
    val listServicesRequest = new ListServicesRequest
    listServicesRequest.setRemoveDeleted(true)

    (deClientWrapper.listServices _)
      .expects(listServicesRequest)
      .returns(Seq(service1))

    val actual   = cdpDeClient.findServiceByName("service1")
    val expected = Right(Some(service1))

    assert(actual == expected)
  }

  test("findServiceByName return Left") {
    val err                 = new IllegalArgumentException("x")
    val listServicesRequest = new ListServicesRequest
    listServicesRequest.setRemoveDeleted(true)

    (deClientWrapper.listServices _)
      .expects(listServicesRequest)
      .throws(err)

    val actual = cdpDeClient.findServiceByName("service2")
    assertFindAllServiceErr(actual, "x")
  }

  test("findAllVcs return Right") {
    val req = new ListVcsRequest()
    req.setClusterId("serviceId1")

    (deClientWrapper.listVcs _).expects(req).returns(Seq(vc1, vc2))

    val actual   = cdpDeClient.findAllVcs("serviceId1")
    val expected = Right(Seq(vc1, vc2))

    assert(actual == expected)
  }

  test("findAllVcs return Left") {
    val req = new ListVcsRequest()
    req.setClusterId("serviceId1")

    (deClientWrapper.listVcs _).expects(req).throws(new IllegalArgumentException("x"))

    val actual = cdpDeClient.findAllVcs("serviceId1")
    assertFindAllVcsErr(actual, "serviceId1", "x")
  }

  test("findVcByName return Right") {
    val req = new ListVcsRequest()
    req.setClusterId("serviceId1")

    (deClientWrapper.listVcs _).expects(req).returns(Seq(vc1, vc2))

    val actual   = cdpDeClient.findVcByName("serviceId1", "vc1")
    val expected = Right(Option(vc1))

    assert(actual == expected)
  }

  test("findVcByName return Left") {
    val req = new ListVcsRequest()
    req.setClusterId("serviceId1")

    (deClientWrapper.listVcs _).expects(req).throws(new IllegalArgumentException("x"))

    val actual = cdpDeClient.findVcByName("serviceId1", "vc1")
    assertFindAllVcsErr(actual, "serviceId1", "x")
  }

  test("describeService return Right") {
    val req = new DescribeServiceRequest()
    req.setClusterId("serviceId1")

    val serviceDescription = new ServiceDescription()
    serviceDescription.setClusterId("serviceId1")

    (deClientWrapper.describeService _).expects(req).returns(serviceDescription)

    val actual   = cdpDeClient.describeService("serviceId1")
    val expected = Right(serviceDescription)

    assert(actual == expected)
  }

  test("describeService return Left") {
    val req = new DescribeServiceRequest()
    req.setClusterId("serviceId1")

    (deClientWrapper.describeService _).expects(req).throws(new IllegalArgumentException("x"))

    val actual = cdpDeClient.describeService("serviceId1")
    assertDescribeServiceErr(actual, "serviceId1", "x")
  }

  test("describeServiceByName return Right") {
    val req                 = new DescribeServiceRequest()
    val listServicesRequest = new ListServicesRequest
    listServicesRequest.setRemoveDeleted(true)

    req.setClusterId("serviceId1")

    val serviceDescription = new ServiceDescription()
    serviceDescription.setClusterId("serviceId1")

    (deClientWrapper.listServices _)
      .expects(listServicesRequest)
      .returns(Seq(service1, service2))

    (deClientWrapper.describeService _)
      .expects(req)
      .returns(serviceDescription)

    val actual   = cdpDeClient.describeServiceByName("service1")
    val expected = Right(serviceDescription)

    assert(actual == expected)
  }

  test("describeServiceByName return Left(Error) not found") {
    val req                 = new DescribeServiceRequest()
    val listServicesRequest = new ListServicesRequest
    listServicesRequest.setRemoveDeleted(true)

    req.setClusterId("serviceId1")

    val serviceDescription = new ServiceDescription()
    serviceDescription.setClusterId("serviceId1")

    (deClientWrapper.listServices _)
      .expects(listServicesRequest)
      .returns(Seq(service1, service2))

    val actual = cdpDeClient.describeServiceByName("service3")

    val expected = Left(ServiceNotFound("service3"))
    assert(actual == expected)
  }

  test("describeServiceByName return Left(Error) exception") {
    val req                 = new DescribeServiceRequest()
    req.setClusterId("serviceId1")
    val listServicesRequest = new ListServicesRequest
    listServicesRequest.setRemoveDeleted(true)

    val serviceDescription = new ServiceDescription()
    serviceDescription.setClusterId("serviceId1")

    (deClientWrapper.listServices _)
      .expects(listServicesRequest)
      .throws(new IllegalArgumentException("x"))

    val actual = cdpDeClient.describeServiceByName("service3")
    assertFindAllServiceErr(actual, "x")
  }

  test("describeVc return Right") {
    val req = new DescribeVcRequest()
    req.setClusterId("serviceId1")
    req.setVcId("vcId")

    val vcDescription = new VcDescription()
    vcDescription.setClusterId("serviceId1")
    vcDescription.setVcId("vcId")

    (deClientWrapper.describeVc _).expects(req).returns(vcDescription)

    val actual   = cdpDeClient.describeVc("serviceId1", "vcId")
    val expected = Right(vcDescription)

    assert(actual == expected)
  }

  test("describeVc return Left") {
    val req = new DescribeVcRequest()
    val err = new IllegalArgumentException("x")
    req.setClusterId("serviceId1")
    req.setVcId("vcId")

    (deClientWrapper.describeVc _).expects(req).throws(err)
    val actual = cdpDeClient.describeVc("serviceId1", "vcId")
    assertDescribeVcErr(actual, "serviceId1", "vcId", "x")
  }

  test("describeVcByName return Right") {
    val listVcReq = new ListVcsRequest()
    listVcReq.setClusterId("serviceId1")

    (deClientWrapper.listVcs _)
      .expects(listVcReq)
      .returns(Seq(vc1, vc2))

    val describeVcReq = new DescribeVcRequest()
    describeVcReq.setClusterId("serviceId1")
    describeVcReq.setVcId("vcId1")
    val vcDescription = new VcDescription()
    vcDescription.setClusterId("serviceId1")
    vcDescription.setVcId("vcId1")

    (deClientWrapper.describeVc _)
      .expects(describeVcReq)
      .returns(vcDescription)

    val actual   = cdpDeClient.describeVcByName("serviceId1", "vc1")
    val expected = Right(vcDescription)

    assert(actual == expected)
  }

  test("describeVcByName return Left(Error) not found vc") {
    val listVcReq = new ListVcsRequest()
    listVcReq.setClusterId("serviceId1")

    (deClientWrapper.listVcs _)
      .expects(listVcReq)
      .returns(Seq(vc1, vc2))

    val describeVcReq = new DescribeVcRequest()
    describeVcReq.setClusterId("serviceId1")
    describeVcReq.setVcId("vcId1")

    val vcDescription = new VcDescription()
    vcDescription.setClusterId("serviceId1")
    vcDescription.setVcId("vcId1")

    val actual = cdpDeClient.describeVcByName("serviceId1", "vc3")

    val expected = Left(VcNotFound("serviceId1", "vc3"))
    assert(actual == expected)
  }

  test("describeVcByName return Left(Error) exception") {
    val listVcReq = new ListVcsRequest()
    listVcReq.setClusterId("serviceId1")

    (deClientWrapper.listVcs _)
      .expects(listVcReq)
      .returns(Seq(vc1, vc2))

    val describeVcReq = new DescribeVcRequest()
    describeVcReq.setClusterId("serviceId1")
    describeVcReq.setVcId("vcId1")
    val vcDescription = new VcDescription()
    vcDescription.setClusterId("serviceId1")
    vcDescription.setVcId("vcId1")

    (deClientWrapper.describeVc _)
      .expects(describeVcReq)
      .throws(new IllegalArgumentException("x"))

    val actual = cdpDeClient.describeVcByName("serviceId1", "vc1")
    assertDescribeVcErr(actual, "serviceId1", "vcId1", "x")
  }
}
