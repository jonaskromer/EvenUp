package de.htwg.swe.evenup.view.tui

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.StateComponent.*

class TuiKeysSpec extends AnyWordSpec with Matchers:

  "TuiKeys enum" should {

    "contain all defined keys" in:
      val keys = TuiKeys.values.map(_.key)
      keys should contain allOf (
        ":newgroup",
        ":group",
        ":addexp",
        ":editexp",
        ":pay",
        ":editpay",
        ":adduser",
        ":h",
        ":q",
        ":m",
        ":l"
      )

    "return correct usage and description for newGroup" in:
      val newGroup = TuiKeys.newGroup
      newGroup.usage shouldBe "<group name>"
      newGroup.description shouldBe "Add a group"

    "return correct usage and description for addExpense" in:
      val addExp = TuiKeys.addExpense
      addExp.usage shouldBe "<name> <paid_by> <amount> <opt:shares as Person:Amount_Person...>  <date>"
      addExp.description shouldBe "Add an expense"

    "allow iteration over all enum values" in:
      val all = TuiKeys.values.toList
      all should contain(TuiKeys.quit)
      all should contain(TuiKeys.login)

    "be able to find a key by string" in:
      val keyMap = TuiKeys.values.map(k => k.key -> k).toMap
      keyMap(":q") shouldBe TuiKeys.quit
      keyMap(":l") shouldBe TuiKeys.login

    val mainMenuState     = MainMenuState()
    val inGroupState      = InGroupState()
    val inEmptyGroupState = InEmptyGroupState()

    "have correct keys, usage, and description" in {
      TuiKeys.newGroup.key shouldBe ":newgroup"
      TuiKeys.newGroup.usage shouldBe "<group name>"
      TuiKeys.newGroup.description shouldBe "Add a group"

      TuiKeys.addExpense.key shouldBe ":addexp"
      TuiKeys.addExpense.description shouldBe "Add an expense"
    }

    "return correct allowed permissions for MainMenuState" in {
      TuiKeys.newGroup.allowed(mainMenuState) shouldBe true
      TuiKeys.gotoGroup.allowed(mainMenuState) shouldBe true
      TuiKeys.addUserToGroup.allowed(mainMenuState) shouldBe false
      TuiKeys.calculateDebts.allowed(mainMenuState) shouldBe false
      TuiKeys.login.allowed(mainMenuState) shouldBe true
    }

    "return correct allowed permissions for InGroupState" in {
      TuiKeys.newGroup.allowed(inGroupState) shouldBe false
      TuiKeys.gotoGroup.allowed(inGroupState) shouldBe false
      TuiKeys.addUserToGroup.allowed(inGroupState) shouldBe true
      TuiKeys.calculateDebts.allowed(inGroupState) shouldBe true
      TuiKeys.login.allowed(inGroupState) shouldBe false
    }

    "return correct allowed permissions for InEmptyGroupState" in {
      TuiKeys.newGroup.allowed(inEmptyGroupState) shouldBe false
      TuiKeys.gotoGroup.allowed(inEmptyGroupState) shouldBe false
      TuiKeys.addUserToGroup.allowed(inEmptyGroupState) shouldBe true
      TuiKeys.calculateDebts.allowed(inEmptyGroupState) shouldBe true
      TuiKeys.login.allowed(inEmptyGroupState) shouldBe false
    }

    "have consistent key strings for all cases" in {
      val allKeys = TuiKeys.values.map(_.key)
      allKeys.distinct.size shouldBe TuiKeys.values.size
    }
  }
