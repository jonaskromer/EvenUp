package de.htwg.swe.evenup.model.financial.ShareComponent

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.financial.ShareComponent.BaseShareImpl.Share
import play.api.libs.json.Json

class IShareSpec extends AnyWordSpec with Matchers:

  "IShare" should {

    val person = Person("Alice")
    val share  = Share(person, 25.50)

    "serialize to XML correctly" in:
      val xml = share.toXml
      xml.label shouldBe "Share"
      (xml \ "Amount").text shouldBe "25.5"
      (xml \ "Person" \ "Person" \ "Name").text shouldBe "Alice"

    "serialize to JSON correctly" in:
      val json = share.toJson
      (json \ "amount").as[Double] shouldBe 25.50
      (json \ "person" \ "name").as[String] shouldBe "Alice"
  }

  "ShareDeserializer" should {

    "deserialize from XML correctly" in:
      val xml =
        <Share>
        <Person><Person><Name>Bob</Name></Person></Person>
        <Amount>30.0</Amount>
      </Share>
      val share = ShareDeserializer.fromXml(xml)
      share.person.name shouldBe "Bob"
      share.amount shouldBe 30.0

    "deserialize from JSON correctly" in:
      val json = Json.obj(
        "person" -> Json.obj("name" -> "Charlie"),
        "amount" -> 45.0
      )
      val share = ShareDeserializer.fromJson(json)
      share.person.name shouldBe "Charlie"
      share.amount shouldBe 45.0

    "roundtrip XML serialization correctly" in:
      val original     = Share(Person("David"), 100.0)
      val xml          = original.toXml
      val deserialized = ShareDeserializer.fromXml(xml)
      deserialized.person.name shouldBe original.person.name
      deserialized.amount shouldBe original.amount

    "roundtrip JSON serialization correctly" in:
      val original     = Share(Person("Eve"), 75.50)
      val json         = original.toJson
      val deserialized = ShareDeserializer.fromJson(json)
      deserialized.person.name shouldBe original.person.name
      deserialized.amount shouldBe original.amount
  }
