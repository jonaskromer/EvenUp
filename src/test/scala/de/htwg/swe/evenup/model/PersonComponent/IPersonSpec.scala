package de.htwg.swe.evenup.model.PersonComponent

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import play.api.libs.json.Json

class IPersonSpec extends AnyWordSpec with Matchers:

  "IPerson" should {

    val person = Person("Alice")

    "serialize to XML correctly" in:
      val xml = person.toXml
      xml.label shouldBe "Person"
      (xml \ "Name").text shouldBe "Alice"

    "serialize to JSON correctly" in:
      val json = person.toJson
      (json \ "name").as[String] shouldBe "Alice"
  }

  "PersonDeserializer" should {

    "deserialize from XML correctly" in:
      val xml    = <Person><Name>Bob</Name></Person>
      val person = PersonDeserializer.fromXml(xml)
      person.name shouldBe "Bob"

    "deserialize from nested XML correctly" in:
      val xml    = <Person><Person><Name>Charlie</Name></Person></Person>
      val person = PersonDeserializer.fromXml(xml)
      person.name shouldBe "Charlie"

    "deserialize from JSON correctly" in:
      val json   = Json.obj("name" -> "David")
      val person = PersonDeserializer.fromJson(json)
      person.name shouldBe "David"

    "roundtrip XML serialization correctly" in:
      val original     = Person("Eve")
      val xml          = original.toXml
      val deserialized = PersonDeserializer.fromXml(xml)
      deserialized.name shouldBe original.name

    "roundtrip JSON serialization correctly" in:
      val original     = Person("Frank")
      val json         = original.toJson
      val deserialized = PersonDeserializer.fromJson(json)
      deserialized.name shouldBe original.name
  }
