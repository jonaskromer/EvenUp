package de.htwg.swe.evenup.model.state

import org.scalatest.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import de.htwg.swe.evenup.control.Controller
import de.htwg.swe.evenup.model.{App, Group, Person}

class InGroupStateSpec extends AnyWordSpec with Matchers:

  "An InGroupState" should {

    "execute without errors" in:
      val state      = InGroupState()
      val app        = App(List(), None, None, state)
      val controller = Controller(app)
      noException shouldBe thrownBy(state.execute(controller))

    "have correct group permissions" in:
      val state = InGroupState()
      state.canAddGroup shouldBe false
      state.canRemoveGroup shouldBe false
      state.canGotoGroup shouldBe false

    "have correct user permissions" in:
      val state = InGroupState()
      state.canAddUser shouldBe true
      state.canRemoveUser shouldBe true

    "have correct expense permissions" in:
      val state = InGroupState()
      state.canAddExpense shouldBe true
      state.canRemoveExpense shouldBe true
      state.canEditExpense shouldBe true

    "have correct transaction permissions" in:
      val state = InGroupState()
      state.canAddTransaction shouldBe true
      state.canRemoveTransaction shouldBe true
      state.canEditTransaction shouldBe true

    "have correct debt and strategy permissions" in:
      val state = InGroupState()
      state.canCalculateDebts shouldBe true
      state.canSetStrategy shouldBe true

    "use the overridden canLogin value" in:
      val state = InGroupState()
      state.canLogin shouldBe false
  }
