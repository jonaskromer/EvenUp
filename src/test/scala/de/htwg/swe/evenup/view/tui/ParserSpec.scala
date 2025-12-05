package de.htwg.swe.evenup.view.tui

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.{Success, Failure}

class ParserSpec extends AnyWordSpec with Matchers:

  "Parser" should {

    val parser = new Parser

    "validate valid share patterns" in {
      parser.validSharePattern("Alice:30") shouldBe true
      parser.validSharePattern("Alice:30.5") shouldBe true
      parser.validSharePattern("Alice:30_Bob:20") shouldBe true
      parser.validSharePattern("Alice:30.5_Bob:20.75") shouldBe true
      parser.validSharePattern("Alice:30_Bob:20_Charlie:10") shouldBe true
    }

    "reject invalid share patterns" in {
      parser.validSharePattern("Alice30") shouldBe false
      parser.validSharePattern("Alice:") shouldBe false
      parser.validSharePattern(":30") shouldBe false
      parser.validSharePattern("Alice:30,Bob:20") shouldBe false // comma instead of underscore
      parser.validSharePattern("Alice:30_") shouldBe false // trailing underscore
      parser.validSharePattern("_Alice:30") shouldBe false // leading underscore
      parser.validSharePattern("Alice:30__Bob:20") shouldBe false // double underscore
      parser.validSharePattern("") shouldBe false
    }

    "parse help command successfully" in {
      val result = parser.parseInput(TuiKeys.help.key)
      result shouldBe a[Success[?]]
      result.get should contain(TuiKeys.help.key)
    }

    "parse quit command successfully" in {
      val result = parser.parseInput(TuiKeys.quit.key)
      result shouldBe a[Success[?]]
      result.get should contain(TuiKeys.quit.key)
    }

    "parse undo command successfully" in {
      val result = parser.parseInput(TuiKeys.undo.key)
      result shouldBe a[Success[?]]
      result.get should contain(TuiKeys.undo.key)
    }

    "parse redo command successfully" in {
      val result = parser.parseInput(TuiKeys.redo.key)
      result shouldBe a[Success[?]]
      result.get should contain(TuiKeys.redo.key)
    }

    "reject addExpense command with non-numeric amount and shares" in {
      val result = parser.parseInput(s"${TuiKeys.addExpense.key} Dinner Alice fifty Alice:30_Bob:20")
      result shouldBe a[Failure[?]]
    }
    
    "parse MainMenu command successfully" in {
      val result = parser.parseInput(TuiKeys.MainMenu.key)
      result shouldBe a[Success[?]]
      result.get should contain(TuiKeys.MainMenu.key)
    }

    "parse calculateDebts command successfully" in {
      val result = parser.parseInput(TuiKeys.calculateDebts.key)
      result shouldBe a[Success[?]]
      result.get should contain(TuiKeys.calculateDebts.key)
    }

    "parse newGroup command with valid arguments" in {
      val result = parser.parseInput(s"${TuiKeys.newGroup.key} TestGroup")
      result shouldBe a[Success[?]]
      result.get shouldBe List(TuiKeys.newGroup.key, "TestGroup")
    }

    "reject newGroup command with no arguments" in {
      val result = parser.parseInput(TuiKeys.newGroup.key)
      result shouldBe a[Failure[?]]
      result.failed.get.getMessage should include(TuiKeys.newGroup.key)
    }

    "reject newGroup command with too many arguments" in {
      val result = parser.parseInput(s"${TuiKeys.newGroup.key} Test Group")
      result shouldBe a[Failure[?]]
    }

    "parse addUserToGroup command with single user" in {
      val result = parser.parseInput(s"${TuiKeys.addUserToGroup.key} Alice")
      result shouldBe a[Success[?]]
      result.get shouldBe List(TuiKeys.addUserToGroup.key, "Alice")
    }

    "parse addUserToGroup command with multiple users" in {
      val result = parser.parseInput(s"${TuiKeys.addUserToGroup.key} Alice Bob Charlie")
      result shouldBe a[Success[?]]
      result.get shouldBe List(TuiKeys.addUserToGroup.key, "Alice", "Bob", "Charlie")
    }

    "reject addUserToGroup command with no arguments" in {
      val result = parser.parseInput(TuiKeys.addUserToGroup.key)
      result shouldBe a[Failure[?]]
    }

    "parse gotoGroup command with valid arguments" in {
      val result = parser.parseInput(s"${TuiKeys.gotoGroup.key} TestGroup")
      result shouldBe a[Success[?]]
      result.get shouldBe List(TuiKeys.gotoGroup.key, "TestGroup")
    }

    "reject gotoGroup command with no arguments" in {
      val result = parser.parseInput(TuiKeys.gotoGroup.key)
      result shouldBe a[Failure[?]]
    }

    "reject gotoGroup command with too many arguments" in {
      val result = parser.parseInput(s"${TuiKeys.gotoGroup.key} Test Group")
      result shouldBe a[Failure[?]]
    }

    "parse setStrategy command with valid arguments" in {
      val result = parser.parseInput(s"${TuiKeys.setStrategy.key} Simplified")
      result shouldBe a[Success[?]]
      result.get shouldBe List(TuiKeys.setStrategy.key, "Simplified")
    }

    "reject setStrategy command with no arguments" in {
      val result = parser.parseInput(TuiKeys.setStrategy.key)
      result shouldBe a[Failure[?]]
    }

    "reject setStrategy command with too many arguments" in {
      val result = parser.parseInput(s"${TuiKeys.setStrategy.key} Normal Strategy")
      result shouldBe a[Failure[?]]
    }

    "parse addExpense command with valid arguments (no shares)" in {
      val result = parser.parseInput(s"${TuiKeys.addExpense.key} Dinner Alice 50.0")
      result shouldBe a[Success[?]]
      result.get shouldBe List(TuiKeys.addExpense.key, "Dinner", "Alice", "50.0")
    }

    "parse addExpense command with valid arguments and shares" in {
      val result = parser.parseInput(s"${TuiKeys.addExpense.key} Dinner Alice 50.0 Alice:30_Bob:20")
      result shouldBe a[Success[?]]
      result.get shouldBe List(TuiKeys.addExpense.key, "Dinner", "Alice", "50.0", "Alice:30_Bob:20")
    }

    "parse addExpense command with decimal amounts in shares" in {
      val result = parser.parseInput(s"${TuiKeys.addExpense.key} Dinner Alice 50.5 Alice:30.5_Bob:20.0")
      result shouldBe a[Success[?]]
      result.get shouldBe List(TuiKeys.addExpense.key, "Dinner", "Alice", "50.5", "Alice:30.5_Bob:20.0")
    }

    "reject addExpense command with non-numeric amount" in {
      val result = parser.parseInput(s"${TuiKeys.addExpense.key} Dinner Alice fifty")
      result shouldBe a[Failure[?]]
    }

    "reject addExpense command with invalid share pattern" in {
      val result = parser.parseInput(s"${TuiKeys.addExpense.key} Dinner Alice 50.0 Alice30Bob20")
      result shouldBe a[Failure[?]]
    }

    "reject addExpense command with too few arguments" in {
      val result = parser.parseInput(s"${TuiKeys.addExpense.key} Dinner Alice")
      result shouldBe a[Failure[?]]
    }

    "reject addExpense command with too many arguments" in {
      val result = parser.parseInput(s"${TuiKeys.addExpense.key} Dinner Alice 50.0 shares date extra")
      result shouldBe a[Failure[?]]
    }

    "reject unsupported command" in {
      val result = parser.parseInput(":unsupported command")
      result shouldBe a[Failure[?]]
      result.failed.get.getMessage should include("This key is not supported... yet :)")
    }

    "reject empty input" in {
      val result = parser.parseInput("")
      result shouldBe a[Failure[?]]
    }

    "include error decoration in failure messages" in {
      val result = parser.parseInput(TuiKeys.newGroup.key)
      result shouldBe a[Failure[?]]
      val errorMessage = result.failed.get.getMessage
      errorMessage should include(TuiKeys.newGroup.key)
      errorMessage should include(TuiKeys.newGroup.usage)
    }

    "handle spaces in input correctly" in {
      val result = parser.parseInput(s"${TuiKeys.newGroup.key}   TestGroup")
      result shouldBe a[Failure[?]]
    }
  }

  "Parser.decorateErrorMessage" should {

    val parser = new Parser

    "create decorated error message for supported keys" in {
      val message = parser.decorateErrorMessage(TuiKeys.newGroup)
      message should include(TuiKeys.newGroup.key)
      message should include(TuiKeys.newGroup.usage)
      message should include("Wrong usage")
    }

    "create decorated error message for unsupported key" in {
      val message = parser.decorateErrorMessage(TuiKeys.unsupportedKey)
      message should include(TuiKeys.unsupportedKey.key)
      message should include(TuiKeys.unsupportedKey.usage)
      message should not include "Wrong usage"
    }

    "include color and border decorations" in {
      val message = parser.decorateErrorMessage(TuiKeys.addExpense)
      message should not be empty
    }
  }