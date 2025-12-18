package de.htwg.swe.evenup.control

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model._
import de.htwg.swe.evenup.model.financial.debt.{NormalDebtStrategy, SimplifiedDebtStrategy}
import de.htwg.swe.evenup.model.financial.{Expense, ExpenseBuilder, Share}
import de.htwg.swe.evenup.model.state.{InGroupState, MainMenuState}
import de.htwg.swe.evenup.BaseArgsHandlerImpl.ArgsHandler

class ArgsHandlerSpec extends AnyWordSpec with Matchers:

  "ArgsHandler" should {

    "handle addGroup operation" when {
      "group does not exist" in {
        val argsHandler = new ArgsHandler
        val app         = App(Nil, None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "addGroup", "group_name" -> "TestGroup"),
          app
        )

        response match
          case EventResponse.AddGroup(AddGroupResult.Success, group) =>
            group.name shouldBe "TestGroup"
            group.members shouldBe empty
          case _ => fail("Expected AddGroup Success response")
      }

      "group already exists" in {
        val argsHandler   = new ArgsHandler
        val existingGroup = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
        val app           = App(List(existingGroup), None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "addGroup", "group_name" -> "TestGroup"),
          app
        )

        response match
          case EventResponse.AddGroup(AddGroupResult.GroupExists, group) => group.name shouldBe "TestGroup"
          case _ => fail("Expected AddGroup GroupExists response")
      }

      "missing group_name parameter" in {
        val argsHandler = new ArgsHandler
        val app         = App(Nil, None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "addGroup"),
          app
        )

        response shouldBe EventResponse.UncoveredFailure("AddGroupHandler")
      }

      "wrong type for group_name parameter" in {
        val argsHandler = new ArgsHandler
        val app         = App(Nil, None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "addGroup", "group_name" -> 123),
          app
        )

        response shouldBe EventResponse.UncoveredFailure("AddGroupHandler")
      }
    }

    "handle gotoGroup operation" when {
      "group exists and is empty" in {
        val argsHandler = new ArgsHandler
        val group       = Group("TestGroup", List(Person("Alice")), Nil, Nil, NormalDebtStrategy())
        val app         = App(List(group), None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "gotoGroup", "group_name" -> "TestGroup"),
          app
        )

        response match
          case EventResponse.GotoGroup(GotoGroupResult.SuccessEmptyGroup, g) => g.name shouldBe "TestGroup"
          case _ => fail("Expected GotoGroup SuccessEmptyGroup response")
      }

      "group exists and has members" in {
        val argsHandler = new ArgsHandler
        val group       = Group("TestGroup", List(Person("Alice"), Person("Bob")), Nil, Nil, NormalDebtStrategy())
        val app         = App(List(group), None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "gotoGroup", "group_name" -> "TestGroup"),
          app
        )

        response match
          case EventResponse.GotoGroup(GotoGroupResult.Success, g) => g.name shouldBe "TestGroup"
          case _                                                   => fail("Expected GotoGroup Success response")
      }

      "group does not exist" in {
        val argsHandler = new ArgsHandler
        val app         = App(Nil, None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "gotoGroup", "group_name" -> "NonExistent"),
          app
        )

        response match
          case EventResponse.GotoGroup(GotoGroupResult.GroupNotFound, g) => g.name shouldBe "NonExistent"
          case _ => fail("Expected GotoGroup GroupNotFound response")
      }

      "missing group_name parameter" in {
        val argsHandler = new ArgsHandler
        val app         = App(Nil, None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "gotoGroup"),
          app
        )

        response shouldBe EventResponse.UncoveredFailure("GotoGroupHandler")
      }

      "wrong type for group_name parameter" in {
        val argsHandler = new ArgsHandler
        val app         = App(Nil, None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "gotoGroup", "group_name" -> 123),
          app
        )

        response shouldBe EventResponse.UncoveredFailure("GotoGroupHandler")
      }
    }

    "handle addUserToGroup operation" when {
      "user is added successfully" in {
        val argsHandler = new ArgsHandler
        val group       = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
        val app         = App(List(group), None, Some(group), InGroupState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "addUserToGroup", "user_name" -> "Alice"),
          app
        )

        response match
          case EventResponse.AddUserToGroup(AddUserToGroupResult.Success, user, _) => user.name shouldBe "Alice"
          case _ => fail("Expected AddUserToGroup Success response")
      }

      "user already exists in group" in {
        val argsHandler = new ArgsHandler
        val group       = Group("TestGroup", List(Person("Alice")), Nil, Nil, NormalDebtStrategy())
        val app         = App(List(group), None, Some(group), InGroupState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "addUserToGroup", "user_name" -> "Alice"),
          app
        )

        response match
          case EventResponse.AddUserToGroup(AddUserToGroupResult.UserAlreadyAdded, user, g) =>
            user.name shouldBe "Alice"
            g.name shouldBe "TestGroup"
          case _ => fail("Expected AddUserToGroup UserAlreadyAdded response")
      }

      "no active group is set" in {
        val argsHandler = new ArgsHandler
        val app         = App(Nil, None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "addUserToGroup", "user_name" -> "Alice"),
          app
        )

        response match
          case EventResponse.AddUserToGroup(AddUserToGroupResult.NoActiveGroup, user, _) => user.name shouldBe "Alice"
          case _ => fail("Expected AddUserToGroup NoActiveGroup response")
      }

      "missing user_name parameter" in {
        val argsHandler = new ArgsHandler
        val app         = App(Nil, None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "addUserToGroup"),
          app
        )

        response shouldBe EventResponse.UncoveredFailure("AddUserToGroupHandler")
      }

      "wrong type for user_name parameter" in {
        val argsHandler = new ArgsHandler
        val app         = App(Nil, None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "addUserToGroup", "user_name" -> 123),
          app
        )

        response shouldBe EventResponse.UncoveredFailure("AddUserToGroupHandler")
      }
    }

    "handle undo operation" when {
      "undo stack is not empty" in {
        val argsHandler = new ArgsHandler
        val app         = App(Nil, None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "undo", "undo_stack_size" -> 3),
          app
        )

        response match
          case EventResponse.Undo(UndoResult.Success, size) => size shouldBe 2
          case _                                            => fail("Expected Undo Success response")
      }

      "undo stack is empty" in {
        val argsHandler = new ArgsHandler
        val app         = App(Nil, None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "undo", "undo_stack_size" -> 0),
          app
        )

        response match
          case EventResponse.Undo(UndoResult.EmptyStack, size) => size shouldBe 0
          case _                                               => fail("Expected Undo EmptyStack response")
      }

      "missing undo_stack_size parameter" in {
        val argsHandler = new ArgsHandler
        val app         = App(Nil, None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "undo"),
          app
        )

        response shouldBe EventResponse.UncoveredFailure("undoHandler")
      }

      "wrong type for undo_stack_size parameter" in {
        val argsHandler = new ArgsHandler
        val app         = App(Nil, None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "undo", "undo_stack_size" -> "invalid"),
          app
        )

        response shouldBe EventResponse.UncoveredFailure("undoHandler")
      }
    }

    "handle redo operation" when {
      "redo stack is not empty" in {
        val argsHandler = new ArgsHandler
        val app         = App(Nil, None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "redo", "redo_stack_size" -> 2),
          app
        )

        response match
          case EventResponse.Redo(RedoResult.Success, size) => size shouldBe 1
          case _                                            => fail("Expected Redo Success response")
      }

      "redo stack is empty" in {
        val argsHandler = new ArgsHandler
        val app         = App(Nil, None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "redo", "redo_stack_size" -> 0),
          app
        )

        response match
          case EventResponse.Redo(RedoResult.EmptyStack, size) => size shouldBe 0
          case _                                               => fail("Expected Redo EmptyStack response")
      }

      "missing redo_stack_size parameter" in {
        val argsHandler = new ArgsHandler
        val app         = App(Nil, None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "redo"),
          app
        )

        response shouldBe EventResponse.UncoveredFailure("redoHandler")
      }

      "wrong type for redo_stack_size parameter" in {
        val argsHandler = new ArgsHandler
        val app         = App(Nil, None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "redo", "redo_stack_size" -> "invalid"),
          app
        )

        response shouldBe EventResponse.UncoveredFailure("redoHandler")
      }
    }

    "handle addExpenseToGroup operation" when {
      "expense is added successfully with even shares" in {
        val argsHandler = new ArgsHandler
        val group       = Group("TestGroup", List(Person("Alice"), Person("Bob")), Nil, Nil, NormalDebtStrategy())
        val app         = App(List(group), None, Some(group), InGroupState())

        val response = argsHandler.checkOrDelegate(
          Map(
            "operation"    -> "addExpenseToGroup",
            "expense_name" -> "Dinner",
            "paid_by"      -> "Alice",
            "amount"       -> 100.0,
            "date"         -> Date(1, 1, 2000),
            "shares"       -> None
          ),
          app
        )

        response match
          case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.Success, expense) =>
            expense.name shouldBe "Dinner"
            expense.amount shouldBe 100.0
            expense.paid_by shouldBe Person("Alice")
            expense.shares should have size 2
          case _ => fail("Expected AddExpenseToGroup Success response")
      }

      "expense is added with custom shares" in {
        val argsHandler = new ArgsHandler
        val group       = Group("TestGroup", List(Person("Alice"), Person("Bob")), Nil, Nil, NormalDebtStrategy())
        val app         = App(List(group), None, Some(group), InGroupState())

        val response = argsHandler.checkOrDelegate(
          Map(
            "operation"    -> "addExpenseToGroup",
            "expense_name" -> "Dinner",
            "paid_by"      -> "Alice",
            "amount"       -> 100.0,
            "date"         -> Date(1, 1, 2000),
            "shares"       -> Some("Alice:60.0_Bob:40.0")
          ),
          app
        )

        response match
          case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.Success, expense) =>
            expense.shares should have size 2
            expense.shares.find(_.person.name == "Alice").get.amount shouldBe 60.0
            expense.shares.find(_.person.name == "Bob").get.amount shouldBe 40.0
          case _ => fail("Expected AddExpenseToGroup Success response")
      }

      "no active group" in {
        val argsHandler = new ArgsHandler
        val app         = App(Nil, None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map(
            "operation"    -> "addExpenseToGroup",
            "expense_name" -> "Dinner",
            "paid_by"      -> "Alice",
            "amount"       -> 100.0,
            "date"         -> Date(1, 1, 2000),
            "shares"       -> None
          ),
          app
        )

        response match
          case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.NoActiveGroup, _) => succeed
          case _ => fail("Expected AddExpenseToGroup NoActiveGroup response")
      }

      "paid_by user not in group" in {
        val argsHandler = new ArgsHandler
        val group       = Group("TestGroup", List(Person("Alice")), Nil, Nil, NormalDebtStrategy())
        val app         = App(List(group), None, Some(group), InGroupState())

        val response = argsHandler.checkOrDelegate(
          Map(
            "operation"    -> "addExpenseToGroup",
            "expense_name" -> "Dinner",
            "paid_by"      -> "Charlie",
            "amount"       -> 100.0,
            "date"         -> Date(1, 1, 2000),
            "shares"       -> None
          ),
          app
        )

        response match
          case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.PaidByNotFound, expense) =>
            expense.paid_by.name shouldBe "Charlie"
          case _ => fail("Expected AddExpenseToGroup PaidByNotFound response")
      }

      "shares person not in group" in {
        val argsHandler = new ArgsHandler
        val group       = Group("TestGroup", List(Person("Alice"), Person("Bob")), Nil, Nil, NormalDebtStrategy())
        val app         = App(List(group), None, Some(group), InGroupState())

        val response = argsHandler.checkOrDelegate(
          Map(
            "operation"    -> "addExpenseToGroup",
            "expense_name" -> "Dinner",
            "paid_by"      -> "Alice",
            "amount"       -> 100.0,
            "date"         -> Date(1, 1, 2000),
            "shares"       -> Some("Alice:60.0_Charlie:40.0")
          ),
          app
        )

        response match
          case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.SharesPersonNotFound, _) => succeed
          case _ => fail("Expected AddExpenseToGroup SharesPersonNotFound response")
      }

      "invalid amount (zero)" in {
        val argsHandler = new ArgsHandler
        val group       = Group("TestGroup", List(Person("Alice"), Person("Bob")), Nil, Nil, NormalDebtStrategy())
        val app         = App(List(group), None, Some(group), InGroupState())

        val response = argsHandler.checkOrDelegate(
          Map(
            "operation"    -> "addExpenseToGroup",
            "expense_name" -> "Dinner",
            "paid_by"      -> "Alice",
            "amount"       -> 0.0,
            "date"         -> Date(1, 1, 2000),
            "shares"       -> None
          ),
          app
        )

        response match
          case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.InvalidAmount, expense) =>
            expense.amount shouldBe 0.0
          case _ => fail("Expected AddExpenseToGroup InvalidAmount response")
      }

      "invalid amount (negative)" in {
        val argsHandler = new ArgsHandler
        val group       = Group("TestGroup", List(Person("Alice"), Person("Bob")), Nil, Nil, NormalDebtStrategy())
        val app         = App(List(group), None, Some(group), InGroupState())

        val response = argsHandler.checkOrDelegate(
          Map(
            "operation"    -> "addExpenseToGroup",
            "expense_name" -> "Dinner",
            "paid_by"      -> "Alice",
            "amount"       -> -50.0,
            "date"         -> Date(1, 1, 2000),
            "shares"       -> None
          ),
          app
        )

        response match
          case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.InvalidAmount, expense) =>
            expense.amount shouldBe -50.0
          case _ => fail("Expected AddExpenseToGroup InvalidAmount response")
      }

      "expense with remainder distribution" in {
        val argsHandler = new ArgsHandler
        val group       = Group(
          "TestGroup",
          List(Person("Alice"), Person("Bob"), Person("Charlie")),
          Nil,
          Nil,
          NormalDebtStrategy()
        )
        val app = App(List(group), None, Some(group), InGroupState())

        val response = argsHandler.checkOrDelegate(
          Map(
            "operation"    -> "addExpenseToGroup",
            "expense_name" -> "Dinner",
            "paid_by"      -> "Alice",
            "amount"       -> 100.0,
            "date"         -> Date(1, 1, 2000),
            "shares"       -> None
          ),
          app
        )

        response match
          case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.Success, expense) =>
            val totalShares = expense.shares.map(_.amount).sum
            totalShares shouldBe 100.0
            expense.shares should have size 3
          case _ => fail("Expected AddExpenseToGroup Success response")
      }

      "invalid share format (missing colon)" in {
        val argsHandler = new ArgsHandler
        val group       = Group("TestGroup", List(Person("Alice"), Person("Bob")), Nil, Nil, NormalDebtStrategy())
        val app         = App(List(group), None, Some(group), InGroupState())

        assertThrows[IllegalArgumentException] {
          argsHandler.checkOrDelegate(
            Map(
              "operation"    -> "addExpenseToGroup",
              "expense_name" -> "Dinner",
              "paid_by"      -> "Alice",
              "amount"       -> 100.0,
              "date"         -> Date(1, 1, 2000),
              "shares"       -> Some("AliceInvalid_Bob:40.0")
            ),
            app
          )
        }
      }

      "invalid share format (non-numeric amount)" in {
        val argsHandler = new ArgsHandler
        val group       = Group("TestGroup", List(Person("Alice"), Person("Bob")), Nil, Nil, NormalDebtStrategy())
        val app         = App(List(group), None, Some(group), InGroupState())

        assertThrows[NumberFormatException] {
          argsHandler.checkOrDelegate(
            Map(
              "operation"    -> "addExpenseToGroup",
              "expense_name" -> "Dinner",
              "paid_by"      -> "Alice",
              "amount"       -> 100.0,
              "date"         -> Date(1, 1, 2000),
              "shares"       -> Some("Alice:invalid_Bob:40.0")
            ),
            app
          )
        }
      }

      "missing required parameters" in {
        val argsHandler = new ArgsHandler
        val app         = App(Nil, None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map(
            "operation"    -> "addExpenseToGroup",
            "expense_name" -> "Dinner"
          ),
          app
        )

        response shouldBe EventResponse.UncoveredFailure("AddExpenseToGroupHandler")
      }

      "wrong type for parameters" in {
        val argsHandler = new ArgsHandler
        val app         = App(Nil, None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map(
            "operation"    -> "addExpenseToGroup",
            "expense_name" -> 123,
            "paid_by"      -> "Alice",
            "amount"       -> 100.0,
            "date"         -> Date(1, 1, 2000),
            "shares"       -> None
          ),
          app
        )

        response shouldBe EventResponse.UncoveredFailure("AddExpenseToGroupHandler")
      }
    }

    "handle setDebtStrategy operation" when {
      "setting normal strategy successfully" in {
        val argsHandler = new ArgsHandler
        val group       = Group("TestGroup", List(Person("Alice")), Nil, Nil, NormalDebtStrategy())
        val app         = App(List(group), None, Some(group), InGroupState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "setDebtStrategy", "strategy" -> "normal"),
          app
        )

        response match
          case EventResponse.SetDebtStrategy(SetDebtStrategyResult.Success, strategy) =>
            strategy shouldBe a[NormalDebtStrategy]
          case _ => fail("Expected SetDebtStrategy Success response")
      }

      "setting simplified strategy successfully" in {
        val argsHandler = new ArgsHandler
        val group       = Group("TestGroup", List(Person("Alice")), Nil, Nil, NormalDebtStrategy())
        val app         = App(List(group), None, Some(group), InGroupState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "setDebtStrategy", "strategy" -> "simplified"),
          app
        )

        response match
          case EventResponse.SetDebtStrategy(SetDebtStrategyResult.Success, strategy) =>
            strategy shouldBe a[SimplifiedDebtStrategy]
          case _ => fail("Expected SetDebtStrategy Success response")
      }

      "no active group" in {
        val argsHandler = new ArgsHandler
        val app         = App(Nil, None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "setDebtStrategy", "strategy" -> "normal"),
          app
        )

        response match
          case EventResponse.SetDebtStrategy(SetDebtStrategyResult.NoActiveGroup, _) => succeed
          case _ => fail("Expected SetDebtStrategy NoActiveGroup response")
      }

      "invalid strategy" in {
        val argsHandler = new ArgsHandler
        val group       = Group("TestGroup", List(Person("Alice")), Nil, Nil, NormalDebtStrategy())
        val app         = App(List(group), None, Some(group), InGroupState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "setDebtStrategy", "strategy" -> "invalid"),
          app
        )

        response shouldBe EventResponse.UncoveredFailure("SetDebtStrategyHandler")
      }

      "missing strategy parameter" in {
        val argsHandler = new ArgsHandler
        val group       = Group("TestGroup", List(Person("Alice")), Nil, Nil, NormalDebtStrategy())
        val app         = App(List(group), None, Some(group), InGroupState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "setDebtStrategy"),
          app
        )

        response shouldBe EventResponse.UncoveredFailure("SetDebtStrategyHandler")
      }
    }

    "handle calculateDebts operation" when {
      "debts calculated successfully" in {
        val argsHandler = new ArgsHandler
        val alice       = Person("Alice")
        val bob         = Person("Bob")
        val expense     = Expense("Dinner", 100.0, Date(1, 1, 2000), alice, List(Share(alice, 50.0), Share(bob, 50.0)))
        val group       = Group("TestGroup", List(alice, bob), List(expense), Nil, NormalDebtStrategy())
        val app         = App(List(group), None, Some(group), InGroupState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "calculateDebts"),
          app
        )

        response match
          case EventResponse.CalculateDebts(CalculateDebtsResult.Success, debts) => debts should have size 1
          case _ => fail("Expected CalculateDebts Success response")
      }

      "no active group" in {
        val argsHandler = new ArgsHandler
        val app         = App(Nil, None, None, MainMenuState())

        val response = argsHandler.checkOrDelegate(
          Map("operation" -> "calculateDebts"),
          app
        )

        response match
          case EventResponse.CalculateDebts(CalculateDebtsResult.NoActiveGroup, debts) => debts shouldBe empty
          case _ => fail("Expected CalculateDebts NoActiveGroup response")
      }
    }

    "delegate to next handler when operation doesn't match" in {
      val argsHandler = new ArgsHandler
      val app         = App(Nil, None, None, MainMenuState())

      val response = argsHandler.checkOrDelegate(
        Map("someOtherKey" -> "someValue"),
        app
      )

      response shouldBe EventResponse.Success
    }

  }
