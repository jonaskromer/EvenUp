package de.htwg.swe.evenup.model.StateComponent

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.MainMenuState
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.InGroupState
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.InEmptyGroupState
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.AppStateFactory

class AppStateSpec extends AnyWordSpec with Matchers:

  "MainMenuState" should {

    val state = MainMenuState()

    "allow adding groups" in:
      state.canAddGroup shouldBe true

    "allow removing groups" in:
      state.canRemoveGroup shouldBe true

    "allow going to a group" in:
      state.canGotoGroup shouldBe true

    "not allow adding users" in:
      state.canAddUser shouldBe false

    "not allow removing users" in:
      state.canRemoveUser shouldBe false

    "not allow adding expenses" in:
      state.canAddExpense shouldBe false

    "not allow removing expenses" in:
      state.canRemoveExpense shouldBe false

    "not allow editing expenses" in:
      state.canEditExpense shouldBe false

    "not allow adding transactions" in:
      state.canAddTransaction shouldBe false

    "not allow removing transactions" in:
      state.canRemoveTransaction shouldBe false

    "not allow editing transactions" in:
      state.canEditTransaction shouldBe false

    "not allow calculating debts" in:
      state.canCalculateDebts shouldBe false

    "not allow setting strategy" in:
      state.canSetStrategy shouldBe false

    "allow login" in:
      state.canLogin shouldBe true

    "not allow going to main menu" in:
      state.canGoToMainMenu shouldBe false
  }

  "InGroupState" should {

    val state = InGroupState()

    "not allow adding groups" in:
      state.canAddGroup shouldBe false

    "not allow removing groups" in:
      state.canRemoveGroup shouldBe false

    "not allow going to a group" in:
      state.canGotoGroup shouldBe false

    "allow adding users" in:
      state.canAddUser shouldBe true

    "allow removing users" in:
      state.canRemoveUser shouldBe true

    "allow adding expenses" in:
      state.canAddExpense shouldBe true

    "allow removing expenses" in:
      state.canRemoveExpense shouldBe true

    "allow editing expenses" in:
      state.canEditExpense shouldBe true

    "allow adding transactions" in:
      state.canAddTransaction shouldBe true

    "allow removing transactions" in:
      state.canRemoveTransaction shouldBe true

    "allow editing transactions" in:
      state.canEditTransaction shouldBe true

    "allow calculating debts" in:
      state.canCalculateDebts shouldBe true

    "allow setting strategy" in:
      state.canSetStrategy shouldBe true

    "not allow login" in:
      state.canLogin shouldBe false
  }

  "InEmptyGroupState" should {

    val state = InEmptyGroupState()

    "not allow adding groups" in:
      state.canAddGroup shouldBe false

    "not allow removing groups" in:
      state.canRemoveGroup shouldBe false

    "not allow going to a group" in:
      state.canGotoGroup shouldBe false

    "allow adding users" in:
      state.canAddUser shouldBe true

    "not allow removing users" in:
      state.canRemoveUser shouldBe false

    "allow adding expenses" in:
      state.canAddExpense shouldBe true

    "allow removing expenses" in:
      state.canRemoveExpense shouldBe true

    "allow editing expenses" in:
      state.canEditExpense shouldBe true

    "allow adding transactions" in:
      state.canAddTransaction shouldBe true

    "allow removing transactions" in:
      state.canRemoveTransaction shouldBe true

    "allow editing transactions" in:
      state.canEditTransaction shouldBe true

    "allow calculating debts" in:
      state.canCalculateDebts shouldBe true

    "allow setting strategy" in:
      state.canSetStrategy shouldBe true

    "not allow login" in:
      state.canLogin shouldBe false
  }

  "AppStateFactory" should {

    "create MainMenuState for 'MainMenuState'" in:
      val state = AppStateFactory("MainMenuState")
      state shouldBe a[MainMenuState]

    "create InGroupState for 'InGroupState'" in:
      val state = AppStateFactory("InGroupState")
      state shouldBe a[InGroupState]

    "create InEmptyGroupState for 'InEmptyGroupState'" in:
      val state = AppStateFactory("InEmptyGroupState")
      state shouldBe a[InEmptyGroupState]

    "default to MainMenuState for unknown state names" in:
      val state = AppStateFactory("UnknownState")
      state shouldBe a[MainMenuState]
  }
