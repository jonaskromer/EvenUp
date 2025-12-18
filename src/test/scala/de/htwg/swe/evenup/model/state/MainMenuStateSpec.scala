package de.htwg.swe.evenup.model.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.swe.evenup.control.BaseControllerImpl.Controller
import de.htwg.swe.evenup.model.{App, Group, Person}

class MainMenuStateSpec extends AnyWordSpec with Matchers:

  "A MainMenuState" should {

    val state = MainMenuState()

    "execute without errors" in:
      val state      = MainMenuState()
      val app        = App(List(), None, None, state)
      val controller = Controller(app)
      noException shouldBe thrownBy(state.execute(controller))

    "deny group navigation restrictions correctly" in {
      state.canAddGroup shouldBe true
      state.canRemoveGroup shouldBe true
      state.canGotoGroup shouldBe true
    }

    "deny all user operations" in {
      state.canAddUser shouldBe false
      state.canRemoveUser shouldBe false
    }

    "deny all expense operations" in {
      state.canAddExpense shouldBe false
      state.canRemoveExpense shouldBe false
      state.canEditExpense shouldBe false
    }

    "deny all transaction operations" in {
      state.canAddTransaction shouldBe false
      state.canRemoveTransaction shouldBe false
      state.canEditTransaction shouldBe false
    }

    "deny strategic operations" in {
      state.canCalculateDebts shouldBe false
      state.canSetStrategy shouldBe false
    }

    "allow login and deny going to main menu" in {
      state.canLogin shouldBe true
      state.canGoToMainMenu shouldBe false
    }
  }
