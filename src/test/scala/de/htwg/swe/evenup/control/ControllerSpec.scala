package de.htwg.swe.evenup.control

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.swe.evenup.model._
import de.htwg.swe.evenup.model.financial.{Expense, ExpenseBuilder}
import de.htwg.swe.evenup.model.financial.debt.{NormalDebtStrategy, SimplifiedDebtStrategy, Debt}
import de.htwg.swe.evenup.model.state.MainMenuState

class ControllerSpec extends AnyWordSpec with Matchers:

  "A Controller" should {

    val alice = Person("Alice")
    val bob   = Person("Bob")
    val g1    = Group("Trip", List(alice), List(), List(), NormalDebtStrategy())
    val g2    = Group("Party", List(alice, bob), List(), List(), NormalDebtStrategy())
    val app   = App(List(g1, g2), None, None, MainMenuState())

    val controller = Controller(app)

    "return all groups correctly" in {
      controller.allGroups shouldBe List(g1, g2)
    }

    "return all users correctly" in {
      controller.allUsers should contain theSameElementsAs List(alice, bob)
    }

    "add a group" in {
      val g3 = Group("Travel", List(bob), List(), List(), NormalDebtStrategy())
      controller.addGroup(g3)
      controller.app.groups should contain(g3)
    }

    "add a user to an active group" in {
      controller.gotoGroup(g1)
      controller.addUserToGroup(bob)
      controller.app.active_group.get.members should contain(bob)
    }

    "handle adding an existing user without duplication" in {
      val initialCount = controller.app.active_group.get.members.size
      controller.addUserToGroup(alice)
      controller.app.active_group.get.members.size shouldBe initialCount
    }

    "goto main menu" in {
      controller.gotoMainMenu
      controller.app.active_group shouldBe None
      controller.app.state shouldBe a[MainMenuState]
    }
  }
