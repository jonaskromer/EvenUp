package de.htwg.swe.evenup.control.BaseControllerImpl

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.AppComponent.BaseAppImpl.App
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.MainMenuState
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.InGroupState
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.GroupComponent.BaseGroupImpl.Group
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date
import de.htwg.swe.evenup.model.financial.ShareComponent.BaseShareImpl.Share
import de.htwg.swe.evenup.model.financial.ExpenseComponent.BaseExpenseImpl.Expense
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.NormalDebtStrategy
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.SimplifiedDebtStrategy
import de.htwg.swe.evenup.control.*

class ArgsHandlerSpec extends AnyWordSpec with Matchers:

  val alice      = Person("Alice")
  val bob        = Person("Bob")
  val date       = Date(15, 6, 2025)
  val strategy   = NormalDebtStrategy()
  val group      = Group("Trip", List(alice, bob), Nil, Nil, strategy)
  val emptyGroup = Group("Empty", List(alice), Nil, Nil, strategy)

  def appWithGroup       = App(List(group), None, None, MainMenuState())
  def appWithActiveGroup = App(List(group), None, Some(group), InGroupState())
  def appWithEmptyGroup  = App(List(emptyGroup), None, Some(emptyGroup), InGroupState())

  "ArgsHandler" should {

    "delegate to chain of handlers" in:
      val handler = new ArgsHandler
      val result  = handler.checkOrDelegate(Map("operation" -> "unknown"), appWithGroup)
      result shouldBe EventResponse.Success
  }

  "AddGroupHandler" should {

    "return Success for new group" in:
      val handler = AddGroupHandler(None)
      val result  = handler.check(Map("operation" -> "addGroup", "group_name" -> "NewGroup"), appWithGroup)
      result shouldBe a[EventResponse.AddGroup]
      result.asInstanceOf[EventResponse.AddGroup].result shouldBe AddGroupResult.Success

    "return GroupExists for existing group" in:
      val handler = AddGroupHandler(None)
      val result  = handler.check(Map("operation" -> "addGroup", "group_name" -> "Trip"), appWithGroup)
      result.asInstanceOf[EventResponse.AddGroup].result shouldBe AddGroupResult.GroupExists

    "delegate to next handler for other operations" in:
      val handler = AddGroupHandler(None)
      val result  = handler.check(Map("operation" -> "other"), appWithGroup)
      result shouldBe EventResponse.NextHandler
  }

  "GotoGroupHandler" should {

    "return Success for existing group with multiple members" in:
      val handler = GotoGroupHandler(None)
      val result  = handler.check(Map("operation" -> "gotoGroup", "group_name" -> "Trip"), appWithGroup)
      result.asInstanceOf[EventResponse.GotoGroup].result shouldBe GotoGroupResult.Success

    "return SuccessEmptyGroup for group with single member" in:
      val handler = GotoGroupHandler(None)
      val app     = App(List(emptyGroup), None, None, MainMenuState())
      val result  = handler.check(Map("operation" -> "gotoGroup", "group_name" -> "Empty"), app)
      result.asInstanceOf[EventResponse.GotoGroup].result shouldBe GotoGroupResult.SuccessEmptyGroup

    "return GroupNotFound for non-existent group" in:
      val handler = GotoGroupHandler(None)
      val result  = handler.check(Map("operation" -> "gotoGroup", "group_name" -> "Unknown"), appWithGroup)
      result.asInstanceOf[EventResponse.GotoGroup].result shouldBe GotoGroupResult.GroupNotFound

    "delegate to next handler for other operations" in:
      val handler = GotoGroupHandler(None)
      val result  = handler.check(Map("operation" -> "other"), appWithGroup)
      result shouldBe EventResponse.NextHandler
  }

  "AddUserToGroupHandler" should {

    "return Success for new user" in:
      val handler = AddUserToGroupHandler(None)
      val result  = handler.check(Map("operation" -> "addUserToGroup", "user_name" -> "Charlie"), appWithActiveGroup)
      result.asInstanceOf[EventResponse.AddUserToGroup].result shouldBe AddUserToGroupResult.Success

    "return UserAlreadyAdded for existing user" in:
      val handler = AddUserToGroupHandler(None)
      val result  = handler.check(Map("operation" -> "addUserToGroup", "user_name" -> "Alice"), appWithActiveGroup)
      result.asInstanceOf[EventResponse.AddUserToGroup].result shouldBe AddUserToGroupResult.UserAlreadyAdded

    "return NoActiveGroup when no active group" in:
      val handler = AddUserToGroupHandler(None)
      val result  = handler.check(Map("operation" -> "addUserToGroup", "user_name" -> "Charlie"), appWithGroup)
      result.asInstanceOf[EventResponse.AddUserToGroup].result shouldBe AddUserToGroupResult.NoActiveGroup

    "delegate to next handler for other operations" in:
      val handler = AddUserToGroupHandler(None)
      val result  = handler.check(Map("operation" -> "other"), appWithGroup)
      result shouldBe EventResponse.NextHandler
  }

  "UndoRedoHandler" should {

    "return Undo Success when stack not empty" in:
      val handler = UndoRedoHandler(None)
      val result  = handler.check(Map("operation" -> "undo", "undo_stack_size" -> 3), appWithGroup)
      result.asInstanceOf[EventResponse.Undo].result shouldBe UndoResult.Success
      result.asInstanceOf[EventResponse.Undo].stack_size shouldBe 2

    "return Undo EmptyStack when stack empty" in:
      val handler = UndoRedoHandler(None)
      val result  = handler.check(Map("operation" -> "undo", "undo_stack_size" -> 0), appWithGroup)
      result.asInstanceOf[EventResponse.Undo].result shouldBe UndoResult.EmptyStack

    "return Redo Success when stack not empty" in:
      val handler = UndoRedoHandler(None)
      val result  = handler.check(Map("operation" -> "redo", "redo_stack_size" -> 2), appWithGroup)
      result.asInstanceOf[EventResponse.Redo].result shouldBe RedoResult.Success
      result.asInstanceOf[EventResponse.Redo].stack_size shouldBe 1

    "return Redo EmptyStack when stack empty" in:
      val handler = UndoRedoHandler(None)
      val result  = handler.check(Map("operation" -> "redo", "redo_stack_size" -> 0), appWithGroup)
      result.asInstanceOf[EventResponse.Redo].result shouldBe RedoResult.EmptyStack

    "delegate to next handler for other operations" in:
      val handler = UndoRedoHandler(None)
      val result  = handler.check(Map("operation" -> "other"), appWithGroup)
      result shouldBe EventResponse.NextHandler
  }

  "AddExpenseToGroupHandler" should {

    "return NoActiveGroup when no active group" in:
      val handler = AddExpenseToGroupHandler(None)
      val result  = handler.check(
        Map("operation" -> "addExpenseToGroup", "expense_name" -> "Test", "paid_by" -> "Alice", "amount" -> 10.0),
        appWithGroup
      )
      result.asInstanceOf[EventResponse.AddExpenseToGroup].result shouldBe AddExpenseToGroupResult.NoActiveGroup

    "return PaidByNotFound when payer not in group" in:
      val handler = AddExpenseToGroupHandler(None)
      val result  = handler.check(
        Map("operation" -> "addExpenseToGroup", "expense_name" -> "Test", "paid_by" -> "Unknown", "amount" -> 10.0),
        appWithActiveGroup
      )
      result.asInstanceOf[EventResponse.AddExpenseToGroup].result shouldBe AddExpenseToGroupResult.PaidByNotFound

    "return InvalidAmount for zero or negative amount" in:
      val handler = AddExpenseToGroupHandler(None)
      val result  = handler.check(
        Map("operation" -> "addExpenseToGroup", "expense_name" -> "Test", "paid_by" -> "Alice", "amount" -> 0.0),
        appWithActiveGroup
      )
      result.asInstanceOf[EventResponse.AddExpenseToGroup].result shouldBe AddExpenseToGroupResult.InvalidAmount

    "return Success for valid expense without shares" in:
      val handler = AddExpenseToGroupHandler(None)
      val result  = handler.check(
        Map(
          "operation"    -> "addExpenseToGroup",
          "expense_name" -> "Dinner",
          "paid_by"      -> "Alice",
          "amount"       -> 30.0,
          "shares"       -> None
        ),
        appWithActiveGroup
      )
      result.asInstanceOf[EventResponse.AddExpenseToGroup].result shouldBe AddExpenseToGroupResult.Success

    "return Success for valid expense with valid shares" in:
      val handler = AddExpenseToGroupHandler(None)
      val result  = handler.check(
        Map(
          "operation"    -> "addExpenseToGroup",
          "expense_name" -> "Dinner",
          "paid_by"      -> "Alice",
          "amount"       -> 30.0,
          "shares"       -> Some("Alice:15_Bob:15")
        ),
        appWithActiveGroup
      )
      result.asInstanceOf[EventResponse.AddExpenseToGroup].result shouldBe AddExpenseToGroupResult.Success

    "return SharesPersonNotFound for invalid person in shares" in:
      val handler = AddExpenseToGroupHandler(None)
      val result  = handler.check(
        Map(
          "operation"    -> "addExpenseToGroup",
          "expense_name" -> "Dinner",
          "paid_by"      -> "Alice",
          "amount"       -> 30.0,
          "shares"       -> Some("Unknown:30")
        ),
        appWithActiveGroup
      )
      result.asInstanceOf[EventResponse.AddExpenseToGroup].result shouldBe AddExpenseToGroupResult.SharesPersonNotFound

    "delegate to next handler for other operations" in:
      val handler = AddExpenseToGroupHandler(None)
      val result  = handler.check(Map("operation" -> "other"), appWithGroup)
      result shouldBe EventResponse.NextHandler
  }

  "SetDebtStrategyHandler" should {

    "return Success for normal strategy" in:
      val handler = SetDebtStrategyHandler(None)
      val result  = handler.check(Map("operation" -> "setDebtStrategy", "strategy" -> "normal"), appWithActiveGroup)
      result.asInstanceOf[EventResponse.SetDebtStrategy].result shouldBe SetDebtStrategyResult.Success
      result.asInstanceOf[EventResponse.SetDebtStrategy].strategy shouldBe a[NormalDebtStrategy]

    "return Success for simplified strategy" in:
      val handler = SetDebtStrategyHandler(None)
      val result  = handler.check(Map("operation" -> "setDebtStrategy", "strategy" -> "simplified"), appWithActiveGroup)
      result.asInstanceOf[EventResponse.SetDebtStrategy].result shouldBe SetDebtStrategyResult.Success
      result.asInstanceOf[EventResponse.SetDebtStrategy].strategy shouldBe a[SimplifiedDebtStrategy]

    "return NoActiveGroup when no active group" in:
      val handler = SetDebtStrategyHandler(None)
      val result  = handler.check(Map("operation" -> "setDebtStrategy", "strategy" -> "normal"), appWithGroup)
      result.asInstanceOf[EventResponse.SetDebtStrategy].result shouldBe SetDebtStrategyResult.NoActiveGroup

    "delegate to next handler for other operations" in:
      val handler = SetDebtStrategyHandler(None)
      val result  = handler.check(Map("operation" -> "other"), appWithGroup)
      result shouldBe EventResponse.NextHandler
  }

  "CalculateDebtsHandler" should {

    "return Success with debts when active group" in:
      val handler          = CalculateDebtsHandler(None)
      val expense          = Expense("Dinner", 30.0, date, alice, List(Share(bob, 30.0)))
      val groupWithExpense = group.addExpense(expense)
      val app    = App(List(groupWithExpense.asInstanceOf[Group]), None, Some(groupWithExpense), InGroupState())
      val result = handler.check(Map("operation" -> "calculateDebts"), app)
      result.asInstanceOf[EventResponse.CalculateDebts].result shouldBe CalculateDebtsResult.Success

    "return NoActiveGroup when no active group" in:
      val handler = CalculateDebtsHandler(None)
      val result  = handler.check(Map("operation" -> "calculateDebts"), appWithGroup)
      result.asInstanceOf[EventResponse.CalculateDebts].result shouldBe CalculateDebtsResult.NoActiveGroup

    "delegate to next handler for other operations" in:
      val handler = CalculateDebtsHandler(None)
      val result  = handler.check(Map("operation" -> "other"), appWithGroup)
      result shouldBe EventResponse.NextHandler
  }

  "HandlerTemplate" should {

    "chain handlers correctly" in:
      val handler3 = CalculateDebtsHandler(None)
      val handler2 = SetDebtStrategyHandler(Some(handler3))
      val handler1 = AddGroupHandler(Some(handler2))

      // Test delegation
      val result = handler1.checkOrDelegate(Map("operation" -> "calculateDebts"), appWithActiveGroup)
      result.asInstanceOf[EventResponse.CalculateDebts].result shouldBe CalculateDebtsResult.Success
  }
