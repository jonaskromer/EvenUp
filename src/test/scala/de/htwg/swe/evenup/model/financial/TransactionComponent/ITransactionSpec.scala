package de.htwg.swe.evenup.model.financial.TransactionComponent

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date
import de.htwg.swe.evenup.model.financial.TransactionComponent.BaseTransactionImpl.Transaction
import play.api.libs.json.Json

class ITransactionSpec extends AnyWordSpec with Matchers:

  "ITransaction" should {

    val alice       = Person("Alice")
    val bob         = Person("Bob")
    val date        = Date(15, 6, 2025)
    val transaction = Transaction(alice, bob, 50.0, date)

    "serialize to XML correctly" in:
      val xml = transaction.toXml
      xml.label shouldBe "Transaction"
      (xml \ "Amount").text shouldBe "50.0"
      (xml \ "From" \ "Person" \ "Name").text shouldBe "Alice"
      (xml \ "To" \ "Person" \ "Name").text shouldBe "Bob"
      (xml \ "Date" \ "Date" \ "Day").text shouldBe "15"

    "serialize to JSON correctly" in:
      val json = transaction.toJson
      (json \ "amount").as[Double] shouldBe 50.0
      (json \ "from" \ "name").as[String] shouldBe "Alice"
      (json \ "to" \ "name").as[String] shouldBe "Bob"
      (json \ "date" \ "day").as[Int] shouldBe 15
  }

  "TransactionDeserializer" should {

    "deserialize from XML correctly" in:
      val xml = <Transaction>
        <From><Person><Name>Charlie</Name></Person></From>
        <To><Person><Name>David</Name></Person></To>
        <Amount>100.0</Amount>
        <Date><Date><Day>20</Day><Month>12</Month><Year>2024</Year></Date></Date>
      </Transaction>
      val transaction = TransactionDeserializer.fromXml(xml)
      transaction.from.name shouldBe "Charlie"
      transaction.to.name shouldBe "David"
      transaction.amount shouldBe 100.0
      transaction.date.day shouldBe 20

    "deserialize from JSON correctly" in:
      val json = Json.obj(
        "from"   -> Json.obj("name" -> "Eve"),
        "to"     -> Json.obj("name" -> "Frank"),
        "amount" -> 75.0,
        "date"   -> Json.obj("day" -> 25, "month" -> 8, "year" -> 2023)
      )
      val transaction = TransactionDeserializer.fromJson(json)
      transaction.from.name shouldBe "Eve"
      transaction.to.name shouldBe "Frank"
      transaction.amount shouldBe 75.0
      transaction.date.day shouldBe 25

    "roundtrip XML serialization correctly" in:
      val original     = Transaction(Person("Grace"), Person("Henry"), 200.0, Date(1, 1, 2025))
      val xml          = original.toXml
      val deserialized = TransactionDeserializer.fromXml(xml)
      deserialized.from.name shouldBe original.from.name
      deserialized.to.name shouldBe original.to.name
      deserialized.amount shouldBe original.amount

    "roundtrip JSON serialization correctly" in:
      val original     = Transaction(Person("Ivan"), Person("Julia"), 150.0, Date(10, 5, 2024))
      val json         = original.toJson
      val deserialized = TransactionDeserializer.fromJson(json)
      deserialized.from.name shouldBe original.from.name
      deserialized.to.name shouldBe original.to.name
      deserialized.amount shouldBe original.amount
  }
