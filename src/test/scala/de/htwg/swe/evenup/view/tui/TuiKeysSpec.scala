package de.htwg.swe.evenup.view.tui

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.MainMenuState
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.InGroupState
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.InEmptyGroupState

class TuiKeysSpec extends AnyWordSpec with Matchers:

  "TuiKeys" should {

    "have newGroup key" in:
      TuiKeys.newGroup.key shouldBe ":newgroup"
      TuiKeys.newGroup.description shouldBe "Add a group"

    "have gotoGroup key" in:
      TuiKeys.gotoGroup.key shouldBe ":group"
      TuiKeys.gotoGroup.description shouldBe "Open a group"

    "have addExpense key" in:
      TuiKeys.addExpense.key shouldBe ":addexp"
      TuiKeys.addExpense.description shouldBe "Add an expense"

    "have addUserToGroup key" in:
      TuiKeys.addUserToGroup.key shouldBe ":adduser"
      TuiKeys.addUserToGroup.description shouldBe "Add a user to a group"

    "have calculateDebts key" in:
      TuiKeys.calculateDebts.key shouldBe ":debts"

    "have setStrategy key" in:
      TuiKeys.setStrategy.key shouldBe ":strategy"

    "have load key" in:
      TuiKeys.load.key shouldBe ":load"

    "have help key" in:
      TuiKeys.help.key shouldBe ":h"

    "have quit key" in:
      TuiKeys.quit.key shouldBe ":q"

    "have undo key" in:
      TuiKeys.undo.key shouldBe ":undo"

    "have redo key" in:
      TuiKeys.redo.key shouldBe ":redo"

    "have MainMenu key" in:
      TuiKeys.MainMenu.key shouldBe ":m"

    "have login key" in:
      TuiKeys.login.key shouldBe ":l"

    "have unsupportedKey" in:
      TuiKeys.unsupportedKey.key shouldBe ""
  }

  "TuiKeys allowed functions" should {

    val mainMenuState = MainMenuState()
    val inGroupState = InGroupState()
    val inEmptyGroupState = InEmptyGroupState()

    "allow newGroup in MainMenu" in:
      TuiKeys.newGroup.allowed(mainMenuState) shouldBe true
      TuiKeys.newGroup.allowed(inGroupState) shouldBe false

    "allow gotoGroup in MainMenu" in:
      TuiKeys.gotoGroup.allowed(mainMenuState) shouldBe true
      TuiKeys.gotoGroup.allowed(inGroupState) shouldBe false

    "allow addExpense in Group states" in:
      TuiKeys.addExpense.allowed(mainMenuState) shouldBe false
      TuiKeys.addExpense.allowed(inGroupState) shouldBe true
      TuiKeys.addExpense.allowed(inEmptyGroupState) shouldBe true

    "allow addUserToGroup in Group states" in:
      TuiKeys.addUserToGroup.allowed(mainMenuState) shouldBe false
      TuiKeys.addUserToGroup.allowed(inGroupState) shouldBe true
      TuiKeys.addUserToGroup.allowed(inEmptyGroupState) shouldBe true

    "allow calculateDebts in Group states" in:
      TuiKeys.calculateDebts.allowed(mainMenuState) shouldBe false
      TuiKeys.calculateDebts.allowed(inGroupState) shouldBe true

    "allow setStrategy in Group states" in:
      TuiKeys.setStrategy.allowed(mainMenuState) shouldBe false
      TuiKeys.setStrategy.allowed(inGroupState) shouldBe true

    "allow help in all states" in:
      TuiKeys.help.allowed(mainMenuState) shouldBe true
      TuiKeys.help.allowed(inGroupState) shouldBe true
      TuiKeys.help.allowed(inEmptyGroupState) shouldBe true

    "allow quit in all states" in:
      TuiKeys.quit.allowed(mainMenuState) shouldBe true
      TuiKeys.quit.allowed(inGroupState) shouldBe true

    "allow undo in all states" in:
      TuiKeys.undo.allowed(mainMenuState) shouldBe true
      TuiKeys.undo.allowed(inGroupState) shouldBe true

    "allow redo in all states" in:
      TuiKeys.redo.allowed(mainMenuState) shouldBe true
      TuiKeys.redo.allowed(inGroupState) shouldBe true

    "allow MainMenu in Group state" in:
      TuiKeys.MainMenu.allowed(mainMenuState) shouldBe false
      TuiKeys.MainMenu.allowed(inGroupState) shouldBe true

    "allow login in MainMenu" in:
      TuiKeys.login.allowed(mainMenuState) shouldBe true
      TuiKeys.login.allowed(inGroupState) shouldBe false

    "never allow unsupportedKey" in:
      TuiKeys.unsupportedKey.allowed(mainMenuState) shouldBe false
      TuiKeys.unsupportedKey.allowed(inGroupState) shouldBe false
  }
