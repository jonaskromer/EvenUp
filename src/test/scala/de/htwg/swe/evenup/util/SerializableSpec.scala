package de.htwg.swe.evenup.util

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json
import scala.xml.Elem

class SerializableSpec extends AnyWordSpec with Matchers:

  class TestSerializable extends Serializable:
    val testValue = "test"

    def toJson = Json.obj("value" -> testValue)

    def toXml: Elem = <Test><Value>{testValue}</Value></Test>

  class TestDeserializer extends Deserializer[String]:
    def fromXml(xml: Elem): String                          = (xml \ "Value").text
    def fromJson(json: play.api.libs.json.JsObject): String = (json \ "value").as[String]

  "Serializable" should {

    "define toJson method" in:
      val serializable = new TestSerializable
      val json         = serializable.toJson
      (json \ "value").as[String] shouldBe "test"

    "define toXml method" in:
      val serializable = new TestSerializable
      val xml          = serializable.toXml
      xml.label shouldBe "Test"
      (xml \ "Value").text shouldBe "test"
  }

  "Deserializer" should {

    "define fromXml method" in:
      val deserializer = new TestDeserializer
      val xml          = <Test><Value>hello</Value></Test>
      deserializer.fromXml(xml) shouldBe "hello"

    "define fromJson method" in:
      val deserializer = new TestDeserializer
      val json         = Json.obj("value" -> "world")
      deserializer.fromJson(json) shouldBe "world"
  }
