package de.htwg.swe.evenup.view.tui

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.control.IController
import de.htwg.swe.evenup.control.*
import de.htwg.swe.evenup.model.AppComponent.IApp
import de.htwg.swe.evenup.model.AppComponent.BaseAppImpl.App
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.MainMenuState
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.InGroupState
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.GroupComponent.BaseGroupImpl.Group
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date
import de.htwg.swe.evenup.model.DateComponent.IDate
import de.htwg.swe.evenup.model.financial.ShareComponent.BaseShareImpl.Share
import de.htwg.swe.evenup.model.financial.ExpenseComponent.BaseExpenseImpl.Expense
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.NormalDebtStrategy
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.SimplifiedDebtStrategy
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.Debt
import de.htwg.swe.evenup.control.BaseControllerImpl.ArgsHandler

class TuiStringBuilderSpec extends AnyWordSpec with Matchers:

  class TestController extends IController:
    var app: IApp   = App(Nil, None, None, MainMenuState())
    val argsHandler = new ArgsHandler

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
  val expense  = Expense("Dinner", 30.0, date, alice, List(Share(bob, 15.0)))
  val debt     = Debt(bob, alice, 15.0)

  "TuiStringBuilder" should {

    "handle AddGroup Success" in:
      val controller = new TestController
      val builder    = new TuiStringBuilder(controller)
      val result     = builder.handle(EventResponse.AddGroup(AddGroupResult.Success, group))
      result should include("Added group")
      result should include("Trip")

    "handle AddGroup GroupExists" in:
      val controller = new TestController
      val builder    = new TuiStringBuilder(controller)
      val result     = builder.handle(EventResponse.AddGroup(AddGroupResult.GroupExists, group))
      result should include("already exists")

    "handle GotoGroup Success" in:
      val controller = new TestController
      controller.app = controller.app.addGroup(group).updateActiveGroup(Some(group))
      val builder = new TuiStringBuilder(controller)
      val result  = builder.handle(EventResponse.GotoGroup(GotoGroupResult.Success, group))
      result should include("Set active group")

    "handle GotoGroup SuccessEmptyGroup" in:
      val controller = new TestController
      val builder    = new TuiStringBuilder(controller)
      val result     = builder.handle(EventResponse.GotoGroup(GotoGroupResult.SuccessEmptyGroup, group))
      result should include("empty")

    "handle GotoGroup GroupNotFound" in:
      val controller = new TestController
      val builder    = new TuiStringBuilder(controller)
      val result     = builder.handle(EventResponse.GotoGroup(GotoGroupResult.GroupNotFound, group))
      result should include("Unable to find")

    "handle AddUserToGroup Success" in:
      val controller = new TestController
      controller.app = controller.app.addGroup(group).updateActiveGroup(Some(group))
      val builder = new TuiStringBuilder(controller)
      val result  = builder.handle(EventResponse.AddUserToGroup(AddUserToGroupResult.Success, alice, group))
      result should include("Added")

    "handle AddUserToGroup UserAlreadyAdded" in:
      val controller = new TestController
      val builder    = new TuiStringBuilder(controller)
      val result     = builder.handle(EventResponse.AddUserToGroup(AddUserToGroupResult.UserAlreadyAdded, alice, group))
      result should include("already added")

    "handle AddUserToGroup NoActiveGroup" in:
      val controller = new TestController
      val builder    = new TuiStringBuilder(controller)
      val result     = builder.handle(EventResponse.AddUserToGroup(AddUserToGroupResult.NoActiveGroup, alice, group))
      result should include("no active group")

    "handle AddExpenseToGroup Success" in:
      val controller = new TestController
      controller.app = controller.app.addGroup(group).updateActiveGroup(Some(group))
      val builder = new TuiStringBuilder(controller)
      val result  = builder.handle(EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.Success, expense))
      result should include("Added expense")

    "handle AddExpenseToGroup NoActiveGroup" in:
      val controller = new TestController
      val builder    = new TuiStringBuilder(controller)
      val result     = builder.handle(EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.NoActiveGroup, expense))
      result should include("goto a group")

    "handle AddExpenseToGroup SharesSumWrong" in:
      val controller = new TestController
      val builder    = new TuiStringBuilder(controller)
      val result     = builder.handle(EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.SharesSumWrong, expense))
      result should include("do not match")

    "handle AddExpenseToGroup SharesPersonNotFound" in:
      val controller = new TestController
      val builder    = new TuiStringBuilder(controller)
      val result     = builder.handle(
        EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.SharesPersonNotFound, expense)
      )
      result should include("Wrong user")

    "handle AddExpenseToGroup PaidByNotFound" in:
      val controller = new TestController
      val builder    = new TuiStringBuilder(controller)
      val result     = builder.handle(EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.PaidByNotFound, expense))
      result should include("add")

    "handle AddExpenseToGroup InvalidShares" in:
      val controller = new TestController
      val builder    = new TuiStringBuilder(controller)
      val result     = builder.handle(EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.InvalidShares, expense))
      result should include("Invalid shares")

    "handle CalculateDebts Success with debts" in:
      val controller = new TestController
      val builder    = new TuiStringBuilder(controller)
      val result     = builder.handle(EventResponse.CalculateDebts(CalculateDebtsResult.Success, List(debt)))
      result should include("Calculated debts")

    "handle CalculateDebts Success with no debts" in:
      val controller = new TestController
      val builder    = new TuiStringBuilder(controller)
      val result     = builder.handle(EventResponse.CalculateDebts(CalculateDebtsResult.Success, Nil))
      result should include("Evend Up")

    "handle CalculateDebts NoActiveGroup" in:
      val controller = new TestController
      val builder    = new TuiStringBuilder(controller)
      val result     = builder.handle(EventResponse.CalculateDebts(CalculateDebtsResult.NoActiveGroup, Nil))
      result should include("no active group")

    "handle SetDebtStrategy Success" in:
      val controller = new TestController
      val builder    = new TuiStringBuilder(controller)
      val result     = builder.handle(EventResponse.SetDebtStrategy(SetDebtStrategyResult.Success, strategy))
      result should include("Switched")

    "handle SetDebtStrategy NoActiveGroup" in:
      val controller = new TestController
      val builder    = new TuiStringBuilder(controller)
      val result     = builder.handle(EventResponse.SetDebtStrategy(SetDebtStrategyResult.NoActiveGroup, strategy))
      result should include("No active group")

    "handle Quit" in:
      val controller = new TestController
      val builder    = new TuiStringBuilder(controller)
      val result     = builder.handle(EventResponse.Quit)
      result should include("Goodbye")

    "handle MainMenu" in:
      val controller = new TestController
      controller.app = controller.app.addGroup(group)
      val builder = new TuiStringBuilder(controller)
      val result  = builder.handle(EventResponse.MainMenu)
      result should include("Go to a group")

    "handle Undo Success" in:
      val controller = new TestController
      controller.app = controller.app.addGroup(group).updateActiveGroup(Some(group))
      val builder = new TuiStringBuilder(controller)
      val result  = builder.handle(EventResponse.Undo(UndoResult.Success, 2))
      result should include("Undo success")

    "handle Undo EmptyStack" in:
      val controller = new TestController
      val builder    = new TuiStringBuilder(controller)
      val result     = builder.handle(EventResponse.Undo(UndoResult.EmptyStack, 0))
      result should include("Nothing to undo")

    "handle Redo Success" in:
      val controller = new TestController
      controller.app = controller.app.addGroup(group).updateActiveGroup(Some(group))
      val builder = new TuiStringBuilder(controller)
      val result  = builder.handle(EventResponse.Redo(RedoResult.Success, 1))
      result should include("Redo success")

    "handle Redo EmptyStack" in:
      val controller = new TestController
      val builder    = new TuiStringBuilder(controller)
      val result     = builder.handle(EventResponse.Redo(RedoResult.EmptyStack, 0))
      result should include("Nothing to redo")

    "check if event is defined" in:
      val controller = new TestController
      val builder    = new TuiStringBuilder(controller)
      builder.isDefined(EventResponse.Quit) shouldBe true
      builder.isDefined(EventResponse.Success) shouldBe false
  }
