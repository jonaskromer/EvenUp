package de.htwg.swe.evenup.view.tui

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import de.htwg.swe.evenup.model.{Share, Person}
import java.io.PrintStream
import java.io.ByteArrayOutputStream

class ParserSpec extends AnyWordSpec with Matchers:

  "Parser" should {

    val parser = new Parser

    "parse a new user correctly" in:
      parser.parseNewUser("John") shouldBe Some("John")
      parser.parseNewUser("  Alice  ") shouldBe Some("Alice")

    "return Some even for empty string" in:
      parser.parseNewUser("") shouldBe Some("")

    "parse valid shares correctly" in:
      val input = "John:30_Peter:20"
      val shares = parser.parseShares(Some(input)).get
      shares.map(_.person.name) should contain theSameElementsAs List("John", "Peter")
      shares.map(_.amount) should contain theSameElementsAs List(30.0, 20.0)

    "return None for invalid share format" in:
      val invalidInput = "John30_Peter:20"
      parser.parseShares(Some(invalidInput)) shouldBe None

    "return None for None input" in:
      parser.parseShares(None) shouldBe None

    "return None for empty share input" in:
      parser.parseShares(Some("")) shouldBe None

    "return None and print 'Wrong Usage' when input cannot be parsed" in:
      val parser = new Parser()
      val outStream = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outStream)):
        val result = parser.parseNewUser(null) // simulate failure
        result shouldBe None
      val output = outStream.toString
      output should include("Wrong Usage")
  }
