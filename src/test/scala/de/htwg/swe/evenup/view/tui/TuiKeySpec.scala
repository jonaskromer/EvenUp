package de.htwg.swe.evenup.view.tui

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

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
  }
