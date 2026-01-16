package de.htwg.swe.evenup.control.BaseControllerImpl

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.control.IController
import de.htwg.swe.evenup.model.AppComponent.IApp
import de.htwg.swe.evenup.model.AppComponent.BaseAppImpl.App
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.MainMenuState
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.InGroupState
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.InEmptyGroupState
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.GroupComponent.BaseGroupImpl.Group
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date
import de.htwg.swe.evenup.model.DateComponent.IDate
import de.htwg.swe.evenup.model.financial.ShareComponent.BaseShareImpl.Share
import de.htwg.swe.evenup.model.financial.ExpenseComponent.BaseExpenseImpl.Expense
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.NormalDebtStrategy
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.SimplifiedDebtStrategy
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.Debt
import de.htwg.swe.evenup.control.*
import de.htwg.swe.evenup.util.ObservableEvent

class ControllerCommandsSpec extends AnyWordSpec with Matchers:

  class TestController extends IController:
    var app: IApp                          = App(Nil, None, None, MainMenuState())
    val argsHandler                        = new ArgsHandler
    var lastEvent: Option[ObservableEvent] = None

    override def notifyObservers(e: ObservableEvent): Unit = lastEvent = Some(e)

    def undo(): Unit                            = ()
    def redo(): Unit                            = ()
    def quit: Unit                              = ()
    def load(): Unit                            = ()
    def gotoMainMenu: Unit                      = ()
    def gotoGroup(group_name: String): Unit     = ()
    def addGroup(group_name: String): Unit      = ()
    def addUserToGroup(user_name: String): Unit = ()

    def addExpenseToGroup(
      expense_name: String,
      paid_by: String,
      amount: Double,
      date: IDate,
      shares: Option[String]
    ): Unit = ()

    def setDebtStrategy(strategy: String): Unit = ()
    def calculateDebts(): Unit                  = ()

  val alice    = Person("Alice")
  val bob      = Person("Bob")
  val strategy = NormalDebtStrategy()
  val group    = Group("Trip", List(alice, bob), Nil, Nil, strategy)
  val date     = Date(15, 6, 2025)

  "AddGroupCommand" should {

    "add group to app" in:
      val controller = new TestController
      val command    = AddGroupCommand(controller, group)
      command.doStep

      controller.app.allGroups should contain(group)
      controller.app.active_group shouldBe Some(group)
      controller.app.state shouldBe a[InGroupState]

    "notify observers with AddGroup event" in:
      val controller = new TestController
      val command    = AddGroupCommand(controller, group)
      command.doStep

      controller.lastEvent shouldBe Some(EventResponse.AddGroup(AddGroupResult.Success, group))
  }

  "GotoMainMenuCommand" should {

    "clear active group and set MainMenuState" in:
      val controller = new TestController
      controller.app = controller.app.addGroup(group).updateActiveGroup(Some(group))
      val command = GotoMainMenuCommand(controller)
      command.doStep

      controller.app.active_group shouldBe None
      controller.app.state shouldBe a[MainMenuState]

    "notify observers with MainMenu event" in:
      val controller = new TestController
      val command    = GotoMainMenuCommand(controller)
      command.doStep

      controller.lastEvent shouldBe Some(EventResponse.MainMenu)
  }

  "GotoGroupCommand" should {

    "set active group" in:
      val controller = new TestController
      controller.app = controller.app.addGroup(group)
      val command = GotoGroupCommand(controller, group)
      command.doStep

      controller.app.active_group shouldBe Some(group)

    "notify observers with GotoGroup Success event" in:
      val controller = new TestController
      val command    = GotoGroupCommand(controller, group)
      command.doStep

      controller.lastEvent shouldBe Some(EventResponse.GotoGroup(GotoGroupResult.Success, group))
  }

  "GotoEmptyGroupCommand" should {

    "set active group" in:
      val controller = new TestController
      val emptyGroup = Group("Empty", List(alice), Nil, Nil, strategy)
      controller.app = controller.app.addGroup(emptyGroup)
      val command = GotoEmptyGroupCommand(controller, emptyGroup)
      command.doStep

      controller.app.active_group shouldBe Some(emptyGroup)

    "notify observers with GotoGroup SuccessEmptyGroup event" in:
      val controller = new TestController
      val emptyGroup = Group("Empty", List(alice), Nil, Nil, strategy)
      val command    = GotoEmptyGroupCommand(controller, emptyGroup)
      command.doStep

      controller.lastEvent shouldBe Some(EventResponse.GotoGroup(GotoGroupResult.SuccessEmptyGroup, emptyGroup))
  }

  "AddUserToGroupCommand" should {

    "add user to active group" in:
      val controller = new TestController
      controller.app = controller.app.addGroup(group).updateActiveGroup(Some(group))
      val charlie = Person("Charlie")
      val command = AddUserToGroupCommand(controller, charlie)
      command.doStep

      controller.app.active_group.get.members should contain(charlie)

    "notify observers with AddUserToGroup Success event" in:
      val controller = new TestController
      controller.app = controller.app.addGroup(group).updateActiveGroup(Some(group))
      val charlie = Person("Charlie")
      val command = AddUserToGroupCommand(controller, charlie)
      command.doStep

      val event = controller.lastEvent.get.asInstanceOf[EventResponse.AddUserToGroup]
      event.result shouldBe AddUserToGroupResult.Success
      event.user shouldBe charlie
  }

  "AddExpenseToGroupCommand" should {

    "add expense to active group" in:
      val controller = new TestController
      controller.app = controller.app.addGroup(group).updateActiveGroup(Some(group))
      val expense = Expense("Dinner", 30.0, date, alice, List(Share(bob, 15.0)))
      val command = AddExpenseToGroupCommand(controller, expense)
      command.doStep

      controller.app.active_group.get.expenses should contain(expense)

    "notify observers with AddExpenseToGroup Success event" in:
      val controller = new TestController
      controller.app = controller.app.addGroup(group).updateActiveGroup(Some(group))
      val expense = Expense("Dinner", 30.0, date, alice, List(Share(bob, 15.0)))
      val command = AddExpenseToGroupCommand(controller, expense)
      command.doStep

      controller.lastEvent shouldBe Some(EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.Success, expense))
  }

  "SetDebtCalculationStrategy" should {

    "update strategy on active group" in:
      val controller = new TestController
      controller.app = controller.app.addGroup(group).updateActiveGroup(Some(group))
      val newStrategy = SimplifiedDebtStrategy()
      val command     = SetDebtCalculationStrategy(controller, newStrategy)
      command.doStep

      controller.app.active_group.get.debt_strategy shouldBe a[SimplifiedDebtStrategy]

    "notify observers with SetDebtStrategy Success event" in:
      val controller = new TestController
      controller.app = controller.app.addGroup(group).updateActiveGroup(Some(group))
      val newStrategy = SimplifiedDebtStrategy()
      val command     = SetDebtCalculationStrategy(controller, newStrategy)
      command.doStep

      val event = controller.lastEvent.get.asInstanceOf[EventResponse.SetDebtStrategy]
      event.result shouldBe SetDebtStrategyResult.Success
  }

  "CalculateDebtsCommand" should {

    "notify observers with CalculateDebts Success event" in:
      val controller = new TestController
      val debts      = List(Debt(bob, alice, 15.0))
      val command    = CalculateDebtsCommand(controller, debts)
      command.doStep

      controller.lastEvent shouldBe Some(EventResponse.CalculateDebts(CalculateDebtsResult.Success, debts))
  }
