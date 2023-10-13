package it.agilelab.provisioning.mesh.repository.dynamo

import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.mesh.repository.RepositoryError._
import it.agilelab.provisioning.mesh.repository.RepositoryTestSupport
import it.agilelab.provisioning.mesh.repository.dynamo.model.Item
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class DynamoDBRepositoryWithAuditTest extends AnyFunSuite with MockFactory with RepositoryTestSupport {

  val audit: Audit                                   = mock[Audit]
  val baseRepository: DynamoDBRepository             = stub[DynamoDBRepository]
  val repository: DefaultDynamoDBRepositoryWithAudit = new DefaultDynamoDBRepositoryWithAudit(
    baseRepository,
    audit
  )
  val key1: Map[String, AttributeValue]              = Map("x" -> AttributeValue.builder().s("y").build())
  val item1: Map[String, AttributeValue]             = Map(
    "key"   -> AttributeValue.builder().s("my-key").build(),
    "field" -> AttributeValue.builder().s("value").build()
  )

  test("findById logs info on success") {
    (baseRepository.findById _)
      .when(Item(key1))
      .returns(Right(None))

    inSequence(
      (audit.info _)
        .expects("Executing FindById(Item(Map(x -> AttributeValue(S=y))))")
        .once(),
      (audit.info _)
        .expects("FindById(Item(Map(x -> AttributeValue(S=y)))) completed successfully")
        .once()
    )
    assert(repository.findById(Item(key1)) == Right(None))
  }

  test("findById logs error on failure") {
    (baseRepository.findById _)
      .when(Item(key1))
      .returns(Left(FindEntityByIdErr(Item(key1), new IllegalArgumentException("error"))))

    inSequence(
      (audit.info _)
        .expects("Executing FindById(Item(Map(x -> AttributeValue(S=y))))")
        .once(),
      (audit.error _)
        .expects(where { s: String =>
          s.startsWith(
            "FindById(Item(Map(x -> AttributeValue(S=y)))) failed. Details: FindEntityByIdErr(Item(Map(x -> AttributeValue(S=y))),java.lang.IllegalArgumentException: error"
          )
        })
        .once()
    )
    assertFindEntityByIdErr(
      repository.findById(Item(key1)),
      Item(key1),
      "error"
    )
  }

  test("findAll logs info on success") {
    (baseRepository.findAll _)
      .when(None)
      .returns(Right(Seq.empty))

    inSequence(
      (audit.info _)
        .expects("Executing FindAll(None)")
        .once(),
      (audit.info _)
        .expects("FindAll(None) completed successfully")
        .once()
    )
    assert(repository.findAll(None) == Right(Seq.empty))
  }

  test("findAll logs error on success") {
    (baseRepository.findAll _)
      .when(None)
      .returns(Left(FindAllEntitiesErr(None, new IllegalArgumentException("error"))))

    inSequence(
      (audit.info _)
        .expects("Executing FindAll(None)")
        .once(),
      (audit.error _)
        .expects(where { s: String =>
          s.startsWith(
            "FindAll(None) failed. Details: FindAllEntitiesErr(None,java.lang.IllegalArgumentException: error"
          )
        })
        .once()
    )
    assertFindAllEntitiesErr(
      repository.findAll(None),
      None,
      "error"
    )
  }

  test("create logs info on success") {
    (baseRepository.create _)
      .when(*)
      .returns(Right())
    inSequence(
      (audit.info _)
        .expects("Executing Create(Item(Map(key -> AttributeValue(S=my-key), field -> AttributeValue(S=value))))")
        .once(),
      (audit.info _)
        .expects(
          "Create(Item(Map(key -> AttributeValue(S=my-key), field -> AttributeValue(S=value)))) completed successfully"
        )
        .once()
    )
    assert(repository.create(Item(item1)) == Right())
  }

  test("create logs error on failure") {
    (baseRepository.create _)
      .when(*)
      .returns(Left(CreateEntityFailureErr(Item(item1), new IllegalArgumentException("error"))))

    inSequence(
      (audit.info _)
        .expects("Executing Create(Item(Map(key -> AttributeValue(S=my-key), field -> AttributeValue(S=value))))")
        .once(),
      (audit.error _)
        .expects(
          where { s: String =>
            s.startsWith(
              "Create(Item(Map(key -> AttributeValue(S=my-key), field -> AttributeValue(S=value)))) failed. Details: CreateEntityFailureErr(Item(Map(key -> AttributeValue(S=my-key), field -> AttributeValue(S=value))),java.lang.IllegalArgumentException: error"
            )
          }
        )
        .once()
    )
    assertCreateEntityFailureErr(repository.create(Item(item1)), Item(item1), "error")
  }

  test("delete logs info on success") {
    (baseRepository.delete _)
      .when(*)
      .returns(Right())
    inSequence(
      (audit.info _)
        .expects("Executing Delete(Item(Map(x -> AttributeValue(S=y))))")
        .once(),
      (audit.info _)
        .expects("Delete(Item(Map(x -> AttributeValue(S=y)))) completed successfully")
        .once()
    )
    assert(repository.delete(Item(key1)) == Right())
  }

  test("delete logs error on failure") {
    (baseRepository.delete _)
      .when(*)
      .returns(Left(DeleteEntityErr(Item(key1), new IllegalArgumentException("error"))))

    inSequence(
      (audit.info _)
        .expects("Executing Delete(Item(Map(x -> AttributeValue(S=y))))")
        .once(),
      (audit.error _)
        .expects(where { s: String =>
          s.startsWith(
            "Delete(Item(Map(x -> AttributeValue(S=y)))) failed. Details: DeleteEntityErr(Item(Map(x -> AttributeValue(S=y))),java.lang.IllegalArgumentException: error"
          )
        })
        .once()
    )
    assertDeleteEntityErr(
      repository.delete(Item(key1)),
      Item(key1),
      "error"
    )
  }

  test("update logs info on success") {
    (baseRepository.update _)
      .when(*)
      .returns(Right())

    inSequence(
      (audit.info _)
        .expects("Executing Update(Item(Map(key -> AttributeValue(S=my-key), field -> AttributeValue(S=value))))")
        .once(),
      (audit.info _)
        .expects(
          "Update(Item(Map(key -> AttributeValue(S=my-key), field -> AttributeValue(S=value)))) completed successfully"
        )
        .once()
    )
    assert(repository.update(Item(item1)) == Right())
  }

  test("update logs error on failure") {
    (baseRepository.update _)
      .when(*)
      .returns(Left(UpdateEntityFailureErr(Item(item1), new IllegalArgumentException("error"))))

    inSequence(
      (audit.info _)
        .expects("Executing Update(Item(Map(key -> AttributeValue(S=my-key), field -> AttributeValue(S=value))))")
        .once(),
      (audit.error _)
        .expects(
          where { s: String =>
            s.startsWith(
              "Update(Item(Map(key -> AttributeValue(S=my-key), field -> AttributeValue(S=value)))) failed. Details: UpdateEntityFailureErr(Item(Map(key -> AttributeValue(S=my-key), field -> AttributeValue(S=value))),java.lang.IllegalArgumentException: error"
            )
          }
        )
        .once()
    )
    assertUpdateEntityFailureErr(repository.update(Item(item1)), Item(item1), "error")
  }

}
