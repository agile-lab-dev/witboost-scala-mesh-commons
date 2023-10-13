package it.agilelab.provisioning.commons.client.cdp.de

import com.cloudera.cdp.de.model._
import it.agilelab.provisioning.commons.client.cdp.de.CdpDeClientError.{ ServiceNotFound, VcNotFound }
import it.agilelab.provisioning.commons.client.cdp.de.wrapper.DeClientWrapper
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class DefaultCdpDeClientTest extends AnyFunSuite with MockFactory with CdpDeClientTestSupport {

  val deClientWrapper: DeClientWrapper = stub[DeClientWrapper]
  val cdpDeClient: DefaultCdpDeClient  = new DefaultCdpDeClient(deClientWrapper)
  val service1                         = new ServiceSummary()
  val service2                         = new ServiceSummary()
  val vc1                              = new VcSummary()
  val vc2                              = new VcSummary()

  service1.setClusterId("serviceId1")
  service1.setName("service1")

  service2.setClusterId("serviceId2")
  service2.setName("service2")

  vc1.setClusterId("serviceId1")
  vc1.setVcId("vcId1")
  vc1.setVcName("vc1")

  vc2.setClusterId("serviceId1")
  vc2.setVcId("vcId2")
  vc2.setVcName("vc2")

  test("findAllServices return Right") {
    (deClientWrapper.listServices _)
      .when(new ListServicesRequest())
      .returns(Seq(service1, service2))

    val actual   = cdpDeClient.findAllServices()
    val expected = Right(Seq(service1, service2))

    assert(actual == expected)
  }

  test("findAllServices return Left") {
    val err = new IllegalArgumentException("x")
    (deClientWrapper.listServices _)
      .when(new ListServicesRequest())
      .throws(err)

    val actual = cdpDeClient.findAllServices()
    assertFindAllServiceErr(actual, "x")
  }

  test("findServiceByName return Right") {
    (deClientWrapper.listServices _)
      .when(new ListServicesRequest())
      .returns(Seq(service1, service2))

    val actual   = cdpDeClient.findServiceByName("service2")
    val expected = Right(Some(service2))

    assert(actual == expected)
  }

  test("findServiceByName return Left") {
    val err = new IllegalArgumentException("x")
    (deClientWrapper.listServices _)
      .when(new ListServicesRequest())
      .throws(err)

    val actual = cdpDeClient.findServiceByName("service2")
    assertFindAllServiceErr(actual, "x")
  }

  test("findAllVcs return Right") {
    val req = new ListVcsRequest()
    req.setClusterId("serviceId1")

    (deClientWrapper.listVcs _).when(req).returns(Seq(vc1, vc2))

    val actual   = cdpDeClient.findAllVcs("serviceId1")
    val expected = Right(Seq(vc1, vc2))

    assert(actual == expected)
  }

  test("findAllVcs return Left") {
    val req = new ListVcsRequest()
    req.setClusterId("serviceId1")

    (deClientWrapper.listVcs _).when(req).throws(new IllegalArgumentException("x"))

    val actual = cdpDeClient.findAllVcs("serviceId1")
    assertFindAllVcsErr(actual, "serviceId1", "x")
  }

  test("findVcByName return Right") {
    val req = new ListVcsRequest()
    req.setClusterId("serviceId1")

    (deClientWrapper.listVcs _).when(req).returns(Seq(vc1, vc2))

    val actual   = cdpDeClient.findVcByName("serviceId1", "vc1")
    val expected = Right(Option(vc1))

    assert(actual == expected)
  }

  test("findVcByName return Left") {
    val req = new ListVcsRequest()
    req.setClusterId("serviceId1")

    (deClientWrapper.listVcs _).when(req).throws(new IllegalArgumentException("x"))

    val actual = cdpDeClient.findVcByName("serviceId1", "vc1")
    assertFindAllVcsErr(actual, "serviceId1", "x")
  }

  test("describeService return Right") {
    val req = new DescribeServiceRequest()
    req.setClusterId("serviceId1")

    val serviceDescription = new ServiceDescription()
    serviceDescription.setClusterId("serviceId1")

    (deClientWrapper.describeService _).when(req).returns(serviceDescription)

    val actual   = cdpDeClient.describeService("serviceId1")
    val expected = Right(serviceDescription)

    assert(actual == expected)
  }

  test("describeService return Left") {
    val req = new DescribeServiceRequest()
    req.setClusterId("serviceId1")

    (deClientWrapper.describeService _).when(req).throws(new IllegalArgumentException("x"))

    val actual = cdpDeClient.describeService("serviceId1")
    assertDescribeServiceErr(actual, "serviceId1", "x")
  }

  test("describeServiceByName return Right") {
    val req = new DescribeServiceRequest()
    req.setClusterId("serviceId1")

    val serviceDescription = new ServiceDescription()
    serviceDescription.setClusterId("serviceId1")

    (deClientWrapper.listServices _)
      .when(new ListServicesRequest())
      .returns(Seq(service1, service2))

    (deClientWrapper.describeService _)
      .when(req)
      .returns(serviceDescription)

    val actual   = cdpDeClient.describeServiceByName("service1")
    val expected = Right(serviceDescription)

    assert(actual == expected)
  }

  test("describeServiceByName return Left(Error) not found") {
    val req = new DescribeServiceRequest()
    req.setClusterId("serviceId1")

    val serviceDescription = new ServiceDescription()
    serviceDescription.setClusterId("serviceId1")

    (deClientWrapper.listServices _)
      .when(new ListServicesRequest())
      .returns(Seq(service1, service2))

    val actual = cdpDeClient.describeServiceByName("service3")

    val expected = Left(ServiceNotFound("service3"))
    assert(actual == expected)
  }

  test("describeServiceByName return Left(Error) exception") {
    val req = new DescribeServiceRequest()
    req.setClusterId("serviceId1")

    val serviceDescription = new ServiceDescription()
    serviceDescription.setClusterId("serviceId1")

    (deClientWrapper.listServices _)
      .when(new ListServicesRequest())
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

    (deClientWrapper.describeVc _).when(req).returns(vcDescription)

    val actual   = cdpDeClient.describeVc("serviceId1", "vcId")
    val expected = Right(vcDescription)

    assert(actual == expected)
  }

  test("describeVc return Left") {
    val req = new DescribeVcRequest()
    val err = new IllegalArgumentException("x")
    req.setClusterId("serviceId1")
    req.setVcId("vcId")

    (deClientWrapper.describeVc _).when(req).throws(err)
    val actual = cdpDeClient.describeVc("serviceId1", "vcId")
    assertDescribeVcErr(actual, "serviceId1", "vcId", "x")
  }

  test("describeVcByName return Right") {
    val listVcReq = new ListVcsRequest()
    listVcReq.setClusterId("serviceId1")

    (deClientWrapper.listVcs _)
      .when(listVcReq)
      .returns(Seq(vc1, vc2))

    val describeVcReq = new DescribeVcRequest()
    describeVcReq.setClusterId("serviceId1")
    describeVcReq.setVcId("vcId1")
    val vcDescription = new VcDescription()
    vcDescription.setClusterId("serviceId1")
    vcDescription.setVcId("vcId1")

    (deClientWrapper.describeVc _)
      .when(describeVcReq)
      .returns(vcDescription)

    val actual   = cdpDeClient.describeVcByName("serviceId1", "vc1")
    val expected = Right(vcDescription)

    assert(actual == expected)
  }

  test("describeVcByName return Left(Error) not found vc") {
    val listVcReq = new ListVcsRequest()
    listVcReq.setClusterId("serviceId1")

    (deClientWrapper.listVcs _)
      .when(listVcReq)
      .returns(Seq(vc1, vc2))

    val describeVcReq = new DescribeVcRequest()
    describeVcReq.setClusterId("serviceId1")
    describeVcReq.setVcId("vcId1")
    val vcDescription = new VcDescription()
    vcDescription.setClusterId("serviceId1")
    vcDescription.setVcId("vcId1")

    (deClientWrapper.describeVc _)
      .when(describeVcReq)
      .returns(vcDescription)

    val actual = cdpDeClient.describeVcByName("serviceId1", "vc3")

    val expected = Left(VcNotFound("serviceId1", "vc3"))
    assert(actual == expected)
  }

  test("describeVcByName return Left(Error) exception") {
    val listVcReq = new ListVcsRequest()
    listVcReq.setClusterId("serviceId1")

    (deClientWrapper.listVcs _)
      .when(listVcReq)
      .returns(Seq(vc1, vc2))

    val describeVcReq = new DescribeVcRequest()
    describeVcReq.setClusterId("serviceId1")
    describeVcReq.setVcId("vcId1")
    val vcDescription = new VcDescription()
    vcDescription.setClusterId("serviceId1")
    vcDescription.setVcId("vcId1")

    (deClientWrapper.describeVc _)
      .when(describeVcReq)
      .throws(new IllegalArgumentException("x"))

    val actual = cdpDeClient.describeVcByName("serviceId1", "vc1")
    assertDescribeVcErr(actual, "serviceId1", "vcId1", "x")
  }
}
