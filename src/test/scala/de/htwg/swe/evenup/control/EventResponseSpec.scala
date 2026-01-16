package de.htwg.swe.evenup.control

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.GroupComponent.BaseGroupImpl.Group
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.NormalDebtStrategy
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.SimplifiedDebtStrategy
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.Debt
import de.htwg.swe.evenup.model.financial.ExpenseComponent.BaseExpenseImpl.Expense
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date
import de.htwg.swe.evenup.model.financial.ShareComponent.BaseShareImpl.Share

class EventResponseSpec extends AnyWordSpec with Matchers:

  val alice    = Person("Alice")
  val bob      = Person("Bob")
  val group    = Group("Trip", List(alice, bob), Nil, Nil, NormalDebtStrategy())
  val date     = Date(15, 6, 2025)
  val expense  = Expense("Dinner", 30.0, date, alice, List(Share(bob, 15.0)))
  val debt     = Debt(bob, alice, 15.0)
  val strategy = NormalDebtStrategy()

  "EventResponse" should {

    "have Success case" in:
      EventResponse.Success shouldBe EventResponse.Success

    "have Quit case" in:
      EventResponse.Quit shouldBe EventResponse.Quit

    "have MainMenu case" in:
      EventResponse.MainMenu shouldBe EventResponse.MainMenu

    "have NextHandler case" in:
      EventResponse.NextHandler shouldBe EventResponse.NextHandler

    "have UncoveredFailure case with error text" in:
      val response = EventResponse.UncoveredFailure("test error")
      response match
        case EventResponse.UncoveredFailure(text) => text shouldBe "test error"
        case _                                    => fail("Expected UncoveredFailure")

    "have Undo case with result and stack size" in:
      val response = EventResponse.Undo(UndoResult.Success, 5)
      response match
        case EventResponse.Undo(result, stackSize) =>
          result shouldBe UndoResult.Success
          stackSize shouldBe 5
        case _ => fail("Expected Undo")

    "have Redo case with result and stack size" in:
      val response = EventResponse.Redo(RedoResult.EmptyStack, 0)
      response match
        case EventResponse.Redo(result, stackSize) =>
          result shouldBe RedoResult.EmptyStack
          stackSize shouldBe 0
        case _ => fail("Expected Redo")

    "have AddGroup case with result and group" in:
      val response = EventResponse.AddGroup(AddGroupResult.Success, group)
      response match
        case EventResponse.AddGroup(result, g) =>
          result shouldBe AddGroupResult.Success
          g shouldBe group
        case _ => fail("Expected AddGroup")

    "have GotoGroup case with result and group" in:
      val response = EventResponse.GotoGroup(GotoGroupResult.GroupNotFound, group)
      response match
        case EventResponse.GotoGroup(result, g) =>
          result shouldBe GotoGroupResult.GroupNotFound
          g shouldBe group
        case _ => fail("Expected GotoGroup")

    "have AddUserToGroup case with result, user and group" in:
      val response = EventResponse.AddUserToGroup(AddUserToGroupResult.Success, alice, group)
      response match
        case EventResponse.AddUserToGroup(result, u, g) =>
          result shouldBe AddUserToGroupResult.Success
          u shouldBe alice
          g shouldBe group
        case _ => fail("Expected AddUserToGroup")

    "have AddExpenseToGroup case with result and expense" in:
      val response = EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.Success, expense)
      response match
        case EventResponse.AddExpenseToGroup(result, e) =>
          result shouldBe AddExpenseToGroupResult.Success
          e shouldBe expense
        case _ => fail("Expected AddExpenseToGroup")

    "have CalculateDebts case with result and debts" in:
      val response = EventResponse.CalculateDebts(CalculateDebtsResult.Success, List(debt))
      response match
        case EventResponse.CalculateDebts(result, d) =>
          result shouldBe CalculateDebtsResult.Success
          d shouldBe List(debt)
        case _ => fail("Expected CalculateDebts")

    "have SetDebtStrategy case with result and strategy" in:
      val response = EventResponse.SetDebtStrategy(SetDebtStrategyResult.Success, strategy)
      response match
        case EventResponse.SetDebtStrategy(result, s) =>
          result shouldBe SetDebtStrategyResult.Success
          s shouldBe strategy
        case _ => fail("Expected SetDebtStrategy")
  }

  "UndoResult" should {

    "have Success case" in:
      UndoResult.Success shouldBe UndoResult.Success

    "have EmptyStack case" in:
      UndoResult.EmptyStack shouldBe UndoResult.EmptyStack
  }

  "RedoResult" should {

    "have Success case" in:
      RedoResult.Success shouldBe RedoResult.Success

    "have EmptyStack case" in:
      RedoResult.EmptyStack shouldBe RedoResult.EmptyStack
  }

  "AddGroupResult" should {

    "have Success case" in:
      AddGroupResult.Success shouldBe AddGroupResult.Success

    "have GroupExists case" in:
      AddGroupResult.GroupExists shouldBe AddGroupResult.GroupExists
  }

  "GotoGroupResult" should {

    "have Success case" in:
      GotoGroupResult.Success shouldBe GotoGroupResult.Success

    "have SuccessEmptyGroup case" in:
      GotoGroupResult.SuccessEmptyGroup shouldBe GotoGroupResult.SuccessEmptyGroup

    "have GroupNotFound case" in:
      GotoGroupResult.GroupNotFound shouldBe GotoGroupResult.GroupNotFound
  }

  "AddUserToGroupResult" should {

    "have Success case" in:
      AddUserToGroupResult.Success shouldBe AddUserToGroupResult.Success

    "have UserAlreadyAdded case" in:
      AddUserToGroupResult.UserAlreadyAdded shouldBe AddUserToGroupResult.UserAlreadyAdded

    "have NoActiveGroup case" in:
      AddUserToGroupResult.NoActiveGroup shouldBe AddUserToGroupResult.NoActiveGroup
  }

  "AddExpenseToGroupResult" should {

    "have all cases" in:
      AddExpenseToGroupResult.Success shouldBe AddExpenseToGroupResult.Success
      AddExpenseToGroupResult.NoActiveGroup shouldBe AddExpenseToGroupResult.NoActiveGroup
      AddExpenseToGroupResult.SharesSumWrong shouldBe AddExpenseToGroupResult.SharesSumWrong
      AddExpenseToGroupResult.SharesPersonNotFound shouldBe AddExpenseToGroupResult.SharesPersonNotFound
      AddExpenseToGroupResult.PaidByNotFound shouldBe AddExpenseToGroupResult.PaidByNotFound
      AddExpenseToGroupResult.InvalidAmount shouldBe AddExpenseToGroupResult.InvalidAmount
      AddExpenseToGroupResult.InvalidShares shouldBe AddExpenseToGroupResult.InvalidShares
  }

  "SetDebtStrategyResult" should {

    "have Success case" in:
      SetDebtStrategyResult.Success shouldBe SetDebtStrategyResult.Success

    "have NoActiveGroup case" in:
      SetDebtStrategyResult.NoActiveGroup shouldBe SetDebtStrategyResult.NoActiveGroup
  }

  "CalculateDebtsResult" should {

    "have Success case" in:
      CalculateDebtsResult.Success shouldBe CalculateDebtsResult.Success

    "have NoActiveGroup case" in:
      CalculateDebtsResult.NoActiveGroup shouldBe CalculateDebtsResult.NoActiveGroup
  }
