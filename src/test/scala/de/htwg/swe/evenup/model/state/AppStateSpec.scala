package de.htwg.swe.evenup.model.state

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import de.htwg.swe.evenup.control.Controller
import de.htwg.swe.evenup.model.{App, Group, Person}

class AppStateSpec extends AnyWordSpec with Matchers:

  class TestState extends AppState:
    def execute(controller: Controller): Unit = ()
    def canAddGroup: Boolean                  = true
    def canRemoveGroup: Boolean               = false
    def canGotoGroup: Boolean                 = true

    def canAddUser: Boolean    = false
    def canRemoveUser: Boolean = false

    def canAddExpense: Boolean    = true
    def canRemoveExpense: Boolean = false
    def canEditExpense: Boolean   = true

    def canAddTransaction: Boolean    = false
    def canRemoveTransaction: Boolean = false
    def canEditTransaction: Boolean   = false

    def canCalculateDebts: Boolean = true
    def canSetStrategy: Boolean    = false

  "An AppState implementation" should {

    "call its execute method without errors" in:
      val initialState = new TestState()
      val app          = App(List(), None, None, initialState)
      val controller   = Controller(app)
      val state        = new TestState()
      noException shouldBe thrownBy(state.execute(controller))

    "use the default values for canLogin and canLogout" in:
      val state = new TestState()
      state.canLogin shouldBe false
      state.canLogout shouldBe true

    "use the default values for canShowHelp and canQuit" in:
      val state = new TestState()
      state.canShowHelp shouldBe true
      state.canQuit shouldBe true

    "use the default value for canGoToMainMenu" in:
      val state = new TestState()
      state.canGoToMainMenu shouldBe true

    "return the overridden values for group permissions" in:
      val state = new TestState()
      state.canAddGroup shouldBe true
      state.canRemoveGroup shouldBe false
      state.canGotoGroup shouldBe true

    "return the overridden values for user permissions" in:
      val state = new TestState()
      state.canAddUser shouldBe false
      state.canRemoveUser shouldBe false

    "return the overridden values for expense permissions" in:
      val state = new TestState()
      state.canAddExpense shouldBe true
      state.canRemoveExpense shouldBe false
      state.canEditExpense shouldBe true

    "return the overridden values for transaction permissions" in:
      val state = new TestState()
      state.canAddTransaction shouldBe false
      state.canRemoveTransaction shouldBe false
      state.canEditTransaction shouldBe false

    "return the overridden values for calculating debts and strategies" in:
      val state = new TestState()
      state.canCalculateDebts shouldBe true
      state.canSetStrategy shouldBe false
  }
