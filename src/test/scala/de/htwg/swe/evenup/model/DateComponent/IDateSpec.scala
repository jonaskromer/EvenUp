package de.htwg.swe.evenup.model.DateComponent

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date
import play.api.libs.json.Json

class IDateSpec extends AnyWordSpec with Matchers:

  "IDate" should {

    val date = Date(15, 6, 2025)

    "serialize to XML correctly" in:
      val xml = date.toXml
      xml.label shouldBe "Date"
      (xml \ "Day").text shouldBe "15"
      (xml \ "Month").text shouldBe "6"
      (xml \ "Year").text shouldBe "2025"

    "serialize to JSON correctly" in:
      val json = date.toJson
      (json \ "day").as[Int] shouldBe 15
      (json \ "month").as[Int] shouldBe 6
      (json \ "year").as[Int] shouldBe 2025
  }

  "DateDeserializer" should {

    "deserialize from XML correctly" in:
      val xml  = <Date><Day>20</Day><Month>12</Month><Year>2024</Year></Date>
      val date = DateDeserializer.fromXml(xml)
      date.day shouldBe 20
      date.month shouldBe 12
      date.year shouldBe 2024

    "deserialize from nested XML correctly" in:
      val xml  = <Wrapper><Date><Day>1</Day><Month>1</Month><Year>2000</Year></Date></Wrapper>
      val date = DateDeserializer.fromXml(xml)
      date.day shouldBe 1
      date.month shouldBe 1
      date.year shouldBe 2000

    "deserialize from JSON correctly" in:
      val json = Json.obj("day" -> 25, "month" -> 8, "year" -> 2023)
      val date = DateDeserializer.fromJson(json)
      date.day shouldBe 25
      date.month shouldBe 8
      date.year shouldBe 2023

    "roundtrip XML serialization correctly" in:
      val original     = Date(10, 5, 2025)
      val xml          = original.toXml
      val deserialized = DateDeserializer.fromXml(xml)
      deserialized.day shouldBe original.day
      deserialized.month shouldBe original.month
      deserialized.year shouldBe original.year

    "roundtrip JSON serialization correctly" in:
      val original     = Date(30, 11, 2024)
      val json         = original.toJson
      val deserialized = DateDeserializer.fromJson(json)
      deserialized.day shouldBe original.day
      deserialized.month shouldBe original.month
      deserialized.year shouldBe original.year
  }
