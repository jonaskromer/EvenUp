package de.htwg.swe.evenup.view.tui

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.financial.ShareComponent.BaseShareImpl.Share

class ShareParserSpec extends AnyWordSpec with Matchers:

  "ShareParser" should {

    "parse single share" in:
      val result = ShareParser.parseShares("Alice:10")
      result shouldBe a[Right[?, ?]]
      val shares = result.toOption.get
      shares.length shouldBe 1
      shares.head.person.name shouldBe "Alice"
      shares.head.amount shouldBe 10.0

    "parse multiple shares" in:
      val result = ShareParser.parseShares("Alice:10_Bob:20_Charlie:30")
      result shouldBe a[Right[?, ?]]
      val shares = result.toOption.get
      shares.length shouldBe 3

    "parse shares with decimal amounts" in:
      val result = ShareParser.parseShares("Alice:10.50")
      result shouldBe a[Right[?, ?]]
      val shares = result.toOption.get
      shares.head.amount shouldBe 10.50

    "return error for empty input" in:
      val result = ShareParser.parseShares("")
      result shouldBe a[Left[?, ?]]
      result.swap.toOption.get shouldBe a[ShareParser.ParseError.InvalidFormat]

    "return error for invalid format" in:
      val result = ShareParser.parseShares("invalid")
      result shouldBe a[Left[?, ?]]

    "return error for empty person name" in:
      val result = ShareParser.parseShares(":10")
      result shouldBe a[Left[?, ?]]
      result.swap.toOption.get shouldBe a[ShareParser.ParseError.EmptyPersonName]

    "return error for invalid amount" in:
      val result = ShareParser.parseShares("Alice:abc")
      result shouldBe a[Left[?, ?]]
      result.swap.toOption.get shouldBe a[ShareParser.ParseError.InvalidAmount]

    "return error for negative amount" in:
      val result = ShareParser.parseShares("Alice:-10")
      result shouldBe a[Left[?, ?]]
      result.swap.toOption.get shouldBe a[ShareParser.ParseError.NegativeAmount]
  }

  "ShareParser.validateShareSum" should {

    "return Right when sum matches" in:
      val shares = List(Share(Person("Alice"), 10.0), Share(Person("Bob"), 20.0))
      val result = ShareParser.validateShareSum(shares, 30.0)
      result shouldBe a[Right[?, ?]]

    "return Right when sum matches within tolerance" in:
      val shares = List(Share(Person("Alice"), 10.005))
      val result = ShareParser.validateShareSum(shares, 10.0, 0.01)
      result shouldBe a[Right[?, ?]]

    "return Left when sum does not match" in:
      val shares = List(Share(Person("Alice"), 10.0))
      val result = ShareParser.validateShareSum(shares, 50.0)
      result shouldBe a[Left[?, ?]]
      result.swap.toOption.get shouldBe a[ShareParser.ParseError.ShareSumMismatch]
  }

  "ShareParser.validatePersonsInGroup" should {

    "return Right when all persons are in group" in:
      val alice  = Person("Alice")
      val bob    = Person("Bob")
      val shares = List(Share(alice, 10.0), Share(bob, 20.0))
      val result = ShareParser.validatePersonsInGroup(shares, List(alice, bob))
      result shouldBe a[Right[?, ?]]

    "return Left when person not in group" in:
      val alice   = Person("Alice")
      val bob     = Person("Bob")
      val charlie = Person("Charlie")
      val shares  = List(Share(charlie, 10.0))
      val result  = ShareParser.validatePersonsInGroup(shares, List(alice, bob))
      result shouldBe a[Left[?, ?]]
      result.swap.toOption.get shouldBe a[ShareParser.ParseError.PersonNotInGroup]
  }

  "ShareParser.parseAndValidate" should {

    "return Right for valid input" in:
      val alice  = Person("Alice")
      val bob    = Person("Bob")
      val result = ShareParser.parseAndValidate("Alice:15_Bob:15", 30.0, List(alice, bob))
      result shouldBe a[Right[?, ?]]

    "return Left for invalid sum" in:
      val alice  = Person("Alice")
      val bob    = Person("Bob")
      val result = ShareParser.parseAndValidate("Alice:10_Bob:10", 30.0, List(alice, bob))
      result shouldBe a[Left[?, ?]]

    "return Left for person not in group" in:
      val alice  = Person("Alice")
      val result = ShareParser.parseAndValidate("Alice:15_Bob:15", 30.0, List(alice))
      result shouldBe a[Left[?, ?]]
  }

  "ShareParser.validSharePatternEither" should {

    "return Right for valid pattern" in:
      val result = ShareParser.validSharePatternEither("Alice:10_Bob:20")
      result shouldBe a[Right[?, ?]]

    "return Left for invalid pattern" in:
      val result = ShareParser.validSharePatternEither("invalid pattern")
      result shouldBe a[Left[?, ?]]
  }

  "ParseError.toMessage" should {

    "format InvalidFormat error" in:
      val error = ShareParser.ParseError.InvalidFormat("bad")
      error.toMessage should include("Invalid format")

    "format InvalidAmount error" in:
      val error = ShareParser.ParseError.InvalidAmount("Alice:abc", "not a number")
      error.toMessage should include("Invalid amount")

    "format EmptyPersonName error" in:
      val error = ShareParser.ParseError.EmptyPersonName(":10")
      error.toMessage should include("Empty person name")

    "format NegativeAmount error" in:
      val error = ShareParser.ParseError.NegativeAmount("Alice", -10.0)
      error.toMessage should include("Negative amount")

    "format ShareSumMismatch error" in:
      val error = ShareParser.ParseError.ShareSumMismatch(20.0, 30.0)
      error.toMessage should include("does not match")

    "format PersonNotInGroup error" in:
      val error = ShareParser.ParseError.PersonNotInGroup(List("Unknown"))
      error.toMessage should include("not in group")
  }
