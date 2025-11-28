package de.htwg.swe.evenup.model.state

import org.scalatest.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import de.htwg.swe.evenup.control.Controller
import de.htwg.swe.evenup.model.{App, Group, Person}

class InEmptyGroupStateSpec extends AnyWordSpec with Matchers:

  "An InEmptyGroupState" should {

    "execute without errors" in:
      val state      = InEmptyGroupState()
      val app        = App(List(), None, None, state)
      val controller = Controller(app)
      noException shouldBe thrownBy(state.execute(controller))

    "have correct group permissions" in:
      val state = InEmptyGroupState()
      state.canAddGroup shouldBe false
      state.canRemoveGroup shouldBe false
      state.canGotoGroup shouldBe false

    "have correct user permissions" in:
      val state = InEmptyGroupState()
      state.canAddUser shouldBe true
      state.canRemoveUser shouldBe false

    "have correct expense permissions" in:
      val state = InEmptyGroupState()
      state.canAddExpense shouldBe true
      state.canRemoveExpense shouldBe true
      state.canEditExpense shouldBe true

    "have correct transaction permissions" in:
      val state = InEmptyGroupState()
      state.canAddTransaction shouldBe true
      state.canRemoveTransaction shouldBe true
      state.canEditTransaction shouldBe true

    "have correct debt and strategy permissions" in:
      val state = InEmptyGroupState()
      state.canCalculateDebts shouldBe true
      state.canSetStrategy shouldBe true

    "use the overridden canLogin value" in:
      val state = InEmptyGroupState()
      state.canLogin shouldBe false
  }
