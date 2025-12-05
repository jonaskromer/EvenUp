package de.htwg.swe.evenup.view.tui

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.control._
import de.htwg.swe.evenup.model._
import de.htwg.swe.evenup.model.financial.{Expense, Share}
import de.htwg.swe.evenup.model.financial.debt.{Debt, NormalDebtStrategy, SimplifiedDebtStrategy}
import de.htwg.swe.evenup.model.state.{MainMenuState, InGroupState}

class TuiStringBuilderSpec extends AnyWordSpec with Matchers:

  "TuiStringBuilder" should {

    "handle AddGroup events" when {
      "group is added successfully" in {
        val app = App(Nil, None, None, MainMenuState())
        val controller = new Controller(app)
        val builder = new TuiStringBuilder(controller)
        val group = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
        
        val event = EventResponse.AddGroup(AddGroupResult.Success, group)
        
        builder.isDefined(event) shouldBe true
        val result = builder.handle(event)
        result should include("Added group TestGroup")
      }
      
      "group already exists" in {
        val app = App(Nil, None, None, MainMenuState())
        val controller = new Controller(app)
        val builder = new TuiStringBuilder(controller)
        val group = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
        
        val event = EventResponse.AddGroup(AddGroupResult.GroupExists, group)
        
        builder.isDefined(event) shouldBe true
        val result = builder.handle(event)
        result should include("already exists")
      }
    }

    "handle GotoGroup events" when {
      "goto group successfully" in {
        val group = Group("TestGroup", List(Person("Alice"), Person("Bob")), Nil, Nil, NormalDebtStrategy())
        val app = App(List(group), None, Some(group), InGroupState())
        val controller = new Controller(app)
        val builder = new TuiStringBuilder(controller)
        
        val event = EventResponse.GotoGroup(GotoGroupResult.Success, group)
        
        builder.isDefined(event) shouldBe true
        val result = builder.handle(event)
        result should include("Set active group to TestGroup")
      }
      
      "goto empty group" in {
        val group = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
        val app = App(List(group), None, Some(group), InGroupState())
        val controller = new Controller(app)
        val builder = new TuiStringBuilder(controller)
        
        val event = EventResponse.GotoGroup(GotoGroupResult.SuccessEmptyGroup, group)
        
        builder.isDefined(event) shouldBe true
        val result = builder.handle(event)
        result should include("is empty")
        result should include("Add some users")
      }
      
      "group not found" in {
        val app = App(Nil, None, None, MainMenuState())
        val controller = new Controller(app)
        val builder = new TuiStringBuilder(controller)
        val group = Group("NonExistent", Nil, Nil, Nil, NormalDebtStrategy())
        
        val event = EventResponse.GotoGroup(GotoGroupResult.GroupNotFound, group)
        
        builder.isDefined(event) shouldBe true
        val result = builder.handle(event)
        result should include("Unable to find group")
      }
    }

    "handle AddUserToGroup events" when {
      "user added successfully" in {
        val group = Group("TestGroup", List(Person("Alice")), Nil, Nil, NormalDebtStrategy())
        val app = App(List(group), None, Some(group), InGroupState())
        val controller = new Controller(app)
        val builder = new TuiStringBuilder(controller)
        val user = Person("Alice")
        
        val event = EventResponse.AddUserToGroup(AddUserToGroupResult.Success, user, group)
        
        builder.isDefined(event) shouldBe true
        val result = builder.handle(event)
        result should include("Added Alice to TestGroup")
      }
      
      "user already added" in {
        val group = Group("TestGroup", List(Person("Alice")), Nil, Nil, NormalDebtStrategy())
        val app = App(List(group), None, Some(group), InGroupState())
        val controller = new Controller(app)
        val builder = new TuiStringBuilder(controller)
        val user = Person("Alice")
        
        val event = EventResponse.AddUserToGroup(AddUserToGroupResult.UserAlreadyAdded, user, group)
        
        builder.isDefined(event) shouldBe true
        val result = builder.handle(event)
        result should include("already added")
      }
      
      "no active group" in {
        val app = App(Nil, None, None, MainMenuState())
        val controller = new Controller(app)
        val builder = new TuiStringBuilder(controller)
        val user = Person("Alice")
        val group = Group("", Nil, Nil, Nil, NormalDebtStrategy())
        
        val event = EventResponse.AddUserToGroup(AddUserToGroupResult.NoActiveGroup, user, group)
        
        builder.isDefined(event) shouldBe true
        val result = builder.handle(event)
        result should include("no active group")
      }
    }

    "handle AddExpenseToGroup events" when {
      "expense added successfully" in {
        val alice = Person("Alice")
        val bob = Person("Bob")
        val expense = Expense("Dinner", 100.0, Date(1, 1, 2000), alice, List(Share(alice, 50.0), Share(bob, 50.0)))
        val group = Group("TestGroup", List(alice, bob), List(expense), Nil, NormalDebtStrategy())
        val app = App(List(group), None, Some(group), InGroupState())
        val controller = new Controller(app)
        val builder = new TuiStringBuilder(controller)
        
        val event = EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.Success, expense)
        
        builder.isDefined(event) shouldBe true
        val result = builder.handle(event)
        result should include("Added expense")
      }
      
      "no active group" in {
        val app = App(Nil, None, None, MainMenuState())
        val controller = new Controller(app)
        val builder = new TuiStringBuilder(controller)
        val expense = Expense("Dinner", 100.0, Date(1, 1, 2000), Person("Alice"), Nil)
        
        val event = EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.NoActiveGroup, expense)
        
        builder.isDefined(event) shouldBe true
        val result = builder.handle(event)
        result should include("first goto a group")
      }
      
      "shares sum wrong" in {
        val app = App(Nil, None, None, MainMenuState())
        val controller = new Controller(app)
        val builder = new TuiStringBuilder(controller)
        val expense = Expense("Dinner", 100.0, Date(1, 1, 2000), Person("Alice"), Nil)
        
        val event = EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.SharesSumWrong, expense)
        
        builder.isDefined(event) shouldBe true
        val result = builder.handle(event)
        result should include("sum of the shares")
      }
      
      "shares person not found" in {
        val app = App(Nil, None, None, MainMenuState())
        val controller = new Controller(app)
        val builder = new TuiStringBuilder(controller)
        val expense = Expense("Dinner", 100.0, Date(1, 1, 2000), Person("Alice"), Nil)
        
        val event = EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.SharesPersonNotFound, expense)
        
        builder.isDefined(event) shouldBe true
        val result = builder.handle(event)
        result should include("Wrong user in shares")
      }
      
      "paid by not found" in {
        val app = App(Nil, None, None, MainMenuState())
        val controller = new Controller(app)
        val builder = new TuiStringBuilder(controller)
        val expense = Expense("Dinner", 100.0, Date(1, 1, 2000), Person("Charlie"), Nil)
        
        val event = EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.PaidByNotFound, expense)
        
        builder.isDefined(event) shouldBe true
        val result = builder.handle(event)
        result should include("first add Charlie")
      }
    }

    "handle CalculateDebts events" when {
      "debts calculated successfully with debts" in {
        val alice = Person("Alice")
        val bob = Person("Bob")
        val debt = Debt(bob, alice, 50.0)
        val app = App(Nil, None, None, MainMenuState())
        val controller = new Controller(app)
        val builder = new TuiStringBuilder(controller)
        
        val event = EventResponse.CalculateDebts(CalculateDebtsResult.Success, List(debt))
        
        builder.isDefined(event) shouldBe true
        val result = builder.handle(event)
        result should include("Calculated debts")
      }
      
      "debts calculated successfully with no debts" in {
        val app = App(Nil, None, None, MainMenuState())
        val controller = new Controller(app)
        val builder = new TuiStringBuilder(controller)
        
        val event = EventResponse.CalculateDebts(CalculateDebtsResult.Success, Nil)
        
        builder.isDefined(event) shouldBe true
        val result = builder.handle(event)
        result should include("No debts to settle")
        result should include("Evend Up")
      }
      
      "no active group" in {
        val app = App(Nil, None, None, MainMenuState())
        val controller = new Controller(app)
        val builder = new TuiStringBuilder(controller)
        
        val event = EventResponse.CalculateDebts(CalculateDebtsResult.NoActiveGroup, Nil)
        
        builder.isDefined(event) shouldBe true
        val result = builder.handle(event)
        result should include("no active group")
      }
    }

    "handle SetDebtStrategy events" when {
      "strategy set successfully" in {
        val app = App(Nil, None, None, MainMenuState())
        val controller = new Controller(app)
        val builder = new TuiStringBuilder(controller)
        val strategy = NormalDebtStrategy()
        
        val event = EventResponse.SetDebtStrategy(SetDebtStrategyResult.Success, strategy)
        
        builder.isDefined(event) shouldBe true
        val result = builder.handle(event)
        result should include("Switched to")
        result should include("debt calculation strategy")
      }
      
      "no active group" in {
        val app = App(Nil, None, None, MainMenuState())
        val controller = new Controller(app)
        val builder = new TuiStringBuilder(controller)
        val strategy = NormalDebtStrategy()
        
        val event = EventResponse.SetDebtStrategy(SetDebtStrategyResult.NoActiveGroup, strategy)
        
        builder.isDefined(event) shouldBe true
        val result = builder.handle(event)
        result should include("No active group")
        result should include("Cannot set calculation strategy")
      }
    }

    "handle command events" when {
      "quit command" in {
        val app = App(Nil, None, None, MainMenuState())
        val controller = new Controller(app)
        val builder = new TuiStringBuilder(controller)
        
        val event = EventResponse.Quit
        
        builder.isDefined(event) shouldBe true
        val result = builder.handle(event)
        result should include("Goodbye")
      }
      
      "main menu command" in {
        val group = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
        val app = App(List(group), None, None, MainMenuState())
        val controller = new Controller(app)
        val builder = new TuiStringBuilder(controller)
        
        val event = EventResponse.MainMenu
        
        builder.isDefined(event) shouldBe true
        val result = builder.handle(event)
        result should include("Go to a group")
      }
    }

    "handle undo/redo events" when {
      "undo successful" in {
        val app = App(Nil, None, None, MainMenuState())
        val controller = new Controller(app)
        val builder = new TuiStringBuilder(controller)
        
        val event = EventResponse.Undo(UndoResult.Success, 2)
        
        builder.isDefined(event) shouldBe true
        val result = builder.handle(event)
        result should include("Undo successfull")
        result should include("Remaining stack 2")
      }
      
      "undo empty stack" in {
        val app = App(Nil, None, None, MainMenuState())
        val controller = new Controller(app)
        val builder = new TuiStringBuilder(controller)
        
        val event = EventResponse.Undo(UndoResult.EmptyStack, 0)
        
        builder.isDefined(event) shouldBe true
        val result = builder.handle(event)
        result should include("Nothing to undo")
      }
      
      "redo successful" in {
        val app = App(Nil, None, None, MainMenuState())
        val controller = new Controller(app)
        val builder = new TuiStringBuilder(controller)
        
        val event = EventResponse.Redo(RedoResult.Success, 1)
        
        builder.isDefined(event) shouldBe true
        val result = builder.handle(event)
        result should include("Redo successfull")
        result should include("Remaining stack 1")
      }
      
      "redo empty stack" in {
        val app = App(Nil, None, None, MainMenuState())
        val controller = new Controller(app)
        val builder = new TuiStringBuilder(controller)
        
        val event = EventResponse.Redo(RedoResult.EmptyStack, 0)
        
        builder.isDefined(event) shouldBe true
        val result = builder.handle(event)
        result should include("Nothing to redo")
      }
    }

    "return empty string for active group when none is set" in {
      val app = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val builder = new TuiStringBuilder(controller)
      
      val result = builder.getActiveGroupString
      result shouldBe ""
    }

    "return formatted string for active group when set" in {
      val group = Group("TestGroup", List(Person("Alice")), Nil, Nil, NormalDebtStrategy())
      val app = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val builder = new TuiStringBuilder(controller)
      
      val result = builder.getActiveGroupString
      result should not be empty
      result should include("TestGroup")
    }

    "return available groups string" in {
      val group1 = Group("Group1", Nil, Nil, Nil, NormalDebtStrategy())
      val group2 = Group("Group2", Nil, Nil, Nil, NormalDebtStrategy())
      val app = App(List(group1, group2), None, None, MainMenuState())
      val controller = new Controller(app)
      val builder = new TuiStringBuilder(controller)
      
      val result = builder.getAvailableGroupsString
      result should include("Available Groups")
      result should include("Group1")
      result should include("Group2")
    }

    "return false for isDefined when event is not handled" in {
      val app = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val builder = new TuiStringBuilder(controller)
      
      val event = EventResponse.Success
      
      builder.isDefined(event) shouldBe false
    }
  }