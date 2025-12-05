package de.htwg.swe.evenup.control

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model._
import de.htwg.swe.evenup.model.financial.debt.{NormalDebtStrategy, SimplifiedDebtStrategy}
import de.htwg.swe.evenup.model.financial.{Expense, Share}
import de.htwg.swe.evenup.model.state.{InEmptyGroupState, InGroupState, MainMenuState}
import de.htwg.swe.evenup.util.Observer

class ControllerSpec extends AnyWordSpec with Matchers:

  class TestObserver extends Observer:
    var lastEvent: Option[EventResponse] = None

    def update(e: de.htwg.swe.evenup.util.ObservableEvent): Unit =
      e match
        case event: EventResponse => lastEvent = Some(event)
        case _                    =>

  "Controller" should {

    "add a group successfully" in {
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.addGroup("TestGroup")

      controller.app.containsGroup("TestGroup") shouldBe true
      observer.lastEvent shouldBe defined
      observer.lastEvent.get match
        case EventResponse.AddGroup(AddGroupResult.Success, group) => group.name shouldBe "TestGroup"
        case _                                                     => fail("Expected AddGroup Success event")
    }

    "not add duplicate group" in {
      val existingGroup = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val app           = App(List(existingGroup), None, None, MainMenuState())
      val controller    = new Controller(app)
      val observer      = new TestObserver
      controller.add(observer)

      controller.addGroup("TestGroup")

      controller.app.allGroups should have size 1
      observer.lastEvent shouldBe defined
      observer.lastEvent.get match
        case EventResponse.AddGroup(AddGroupResult.GroupExists, _) => succeed
        case _                                                     => fail("Expected AddGroup GroupExists event")
    }

    "goto existing group with members" in {
      val group      = Group("TestGroup", List(Person("Alice"), Person("Bob")), Nil, Nil, NormalDebtStrategy())
      val app        = App(List(group), None, None, MainMenuState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.gotoGroup("TestGroup")

      controller.app.active_group shouldBe defined
      controller.app.active_group.get.name shouldBe "TestGroup"
      observer.lastEvent shouldBe defined
      observer.lastEvent.get match
        case EventResponse.GotoGroup(GotoGroupResult.Success, g) => g.name shouldBe "TestGroup"
        case _                                                   => fail("Expected GotoGroup Success event")
    }

    "goto empty group" in {
      val group      = Group("TestGroup", List(Person("Alice")), Nil, Nil, NormalDebtStrategy())
      val app        = App(List(group), None, None, MainMenuState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.gotoGroup("TestGroup")

      controller.app.active_group shouldBe defined
      observer.lastEvent shouldBe defined
      observer.lastEvent.get match
        case EventResponse.GotoGroup(GotoGroupResult.SuccessEmptyGroup, _) => succeed
        case _ => fail("Expected GotoGroup SuccessEmptyGroup event")
    }

    "fail to goto non-existent group" in {
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.gotoGroup("NonExistent")

      controller.app.active_group shouldBe None
      observer.lastEvent shouldBe defined
      observer.lastEvent.get match
        case EventResponse.GotoGroup(GotoGroupResult.GroupNotFound, _) => succeed
        case _                                                         => fail("Expected GotoGroup GroupNotFound event")
    }

    "goto main menu" in {
      val group      = Group("TestGroup", List(Person("Alice")), Nil, Nil, NormalDebtStrategy())
      val app        = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.gotoMainMenu

      controller.app.active_group shouldBe None
      controller.app.state shouldBe a[MainMenuState]
      observer.lastEvent shouldBe defined
      observer.lastEvent.get match
        case EventResponse.MainMenu => succeed
        case _                      => fail("Expected MainMenu event")
    }

    "add user to group successfully" in {
      val group      = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val app        = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.addUserToGroup("Alice")

      val updatedGroup = controller.app.getGroup("TestGroup").get
      updatedGroup.members should contain(Person("Alice"))
      observer.lastEvent shouldBe defined
      observer.lastEvent.get match
        case EventResponse.AddUserToGroup(AddUserToGroupResult.Success, user, _) => user.name shouldBe "Alice"
        case _ => fail("Expected AddUserToGroup Success event")
    }

    "not add duplicate user to group" in {
      val group      = Group("TestGroup", List(Person("Alice")), Nil, Nil, NormalDebtStrategy())
      val app        = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.addUserToGroup("Alice")

      val updatedGroup = controller.app.getGroup("TestGroup").get
      updatedGroup.members should have size 1
      observer.lastEvent shouldBe defined
      observer.lastEvent.get match
        case EventResponse.AddUserToGroup(AddUserToGroupResult.UserAlreadyAdded, _, _) => succeed
        case _ => fail("Expected AddUserToGroup UserAlreadyAdded event")
    }

    "fail to add user when no active group" in {
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.addUserToGroup("Alice")

      observer.lastEvent shouldBe defined
      observer.lastEvent.get match
        case EventResponse.AddUserToGroup(AddUserToGroupResult.NoActiveGroup, _, _) => succeed
        case _ => fail("Expected AddUserToGroup NoActiveGroup event")
    }

    "add expense to group successfully" in {
      val group      = Group("TestGroup", List(Person("Alice"), Person("Bob")), Nil, Nil, NormalDebtStrategy())
      val app        = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.addExpenseToGroup("Dinner", "Alice", 100.0, Date(1, 1, 2000))

      val updatedGroup = controller.app.getGroup("TestGroup").get
      updatedGroup.expenses should have size 1
      updatedGroup.expenses.head.name shouldBe "Dinner"
      observer.lastEvent shouldBe defined
      observer.lastEvent.get match
        case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.Success, expense) =>
          expense.name shouldBe "Dinner"
          expense.amount shouldBe 100.0
        case _ => fail("Expected AddExpenseToGroup Success event")
    }

    "add expense with custom shares" in {
      val group      = Group("TestGroup", List(Person("Alice"), Person("Bob")), Nil, Nil, NormalDebtStrategy())
      val app        = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.addExpenseToGroup("Dinner", "Alice", 100.0, Date(1, 1, 2000), Some("Alice:60.0_Bob:40.0"))

      val updatedGroup = controller.app.getGroup("TestGroup").get
      updatedGroup.expenses should have size 1
      val expense = updatedGroup.expenses.head
      expense.shares should have size 2
      expense.shares.find(_.person.name == "Alice").get.amount shouldBe 60.0
      expense.shares.find(_.person.name == "Bob").get.amount shouldBe 40.0
    }

    "fail to add expense when no active group" in {
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.addExpenseToGroup("Dinner", "Alice", 100.0, Date(1, 1, 2000))

      observer.lastEvent shouldBe defined
      observer.lastEvent.get match
        case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.NoActiveGroup, _) => succeed
        case _ => fail("Expected AddExpenseToGroup NoActiveGroup event")
    }

    "fail to add expense when paid_by not in group" in {
      val group      = Group("TestGroup", List(Person("Alice")), Nil, Nil, NormalDebtStrategy())
      val app        = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.addExpenseToGroup("Dinner", "Charlie", 100.0, Date(1, 1, 2000))

      observer.lastEvent shouldBe defined
      observer.lastEvent.get match
        case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.PaidByNotFound, _) => succeed
        case _ => fail("Expected AddExpenseToGroup PaidByNotFound event")
    }

    "fail to add expense with invalid amount" in {
      val group      = Group("TestGroup", List(Person("Alice")), Nil, Nil, NormalDebtStrategy())
      val app        = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.addExpenseToGroup("Dinner", "Alice", -50.0, Date(1, 1, 2000))

      observer.lastEvent shouldBe defined
      observer.lastEvent.get match
        case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.InvalidAmount, expense) =>
          expense.amount shouldBe -50.0
        case _ => fail("Expected AddExpenseToGroup InvalidAmount event")
    }

    "fail to add expense with shares person not found" in {
      val group      = Group("TestGroup", List(Person("Alice"), Person("Bob")), Nil, Nil, NormalDebtStrategy())
      val app        = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.addExpenseToGroup("Dinner", "Alice", 100.0, Date(1, 1, 2000), Some("Alice:60.0_Charlie:40.0"))

      observer.lastEvent shouldBe defined
      observer.lastEvent.get match
        case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.SharesPersonNotFound, _) => succeed
        case _ => fail("Expected AddExpenseToGroup SharesPersonNotFound event")
    }

    "set debt calculation strategy to normal" in {
      val group      = Group("TestGroup", List(Person("Alice")), Nil, Nil, SimplifiedDebtStrategy())
      val app        = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.setDebtStrategy("normal")

      val updatedGroup = controller.app.getGroup("TestGroup").get
      updatedGroup.debt_strategy shouldBe a[NormalDebtStrategy]
      observer.lastEvent shouldBe defined
      observer.lastEvent.get match
        case EventResponse.SetDebtStrategy(SetDebtStrategyResult.Success, strategy) =>
          strategy shouldBe a[NormalDebtStrategy]
        case _ => fail("Expected SetDebtStrategy Success event")
    }

    "set debt calculation strategy to simplified" in {
      val group      = Group("TestGroup", List(Person("Alice")), Nil, Nil, NormalDebtStrategy())
      val app        = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.setDebtStrategy("simplified")

      val updatedGroup = controller.app.getGroup("TestGroup").get
      updatedGroup.debt_strategy shouldBe a[SimplifiedDebtStrategy]
      observer.lastEvent shouldBe defined
      observer.lastEvent.get match
        case EventResponse.SetDebtStrategy(SetDebtStrategyResult.Success, strategy) =>
          strategy shouldBe a[SimplifiedDebtStrategy]
        case _ => fail("Expected SetDebtStrategy Success event")
    }

    "fail to set strategy when no active group" in {
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.setDebtStrategy("normal")

      observer.lastEvent shouldBe defined
      observer.lastEvent.get match
        case EventResponse.SetDebtStrategy(SetDebtStrategyResult.NoActiveGroup, _) => succeed
        case _ => fail("Expected SetDebtStrategy NoActiveGroup event")
    }

    "calculate debts successfully" in {
      val alice      = Person("Alice")
      val bob        = Person("Bob")
      val expense    = Expense("Dinner", 100.0, Date(1, 1, 2000), alice, List(Share(alice, 50.0), Share(bob, 50.0)))
      val group      = Group("TestGroup", List(alice, bob), List(expense), Nil, NormalDebtStrategy())
      val app        = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.calculateDebts()

      observer.lastEvent shouldBe defined
      observer.lastEvent.get match
        case EventResponse.CalculateDebts(CalculateDebtsResult.Success, debts) =>
          debts should have size 1
          debts.head.from shouldBe bob
          debts.head.to shouldBe alice
          debts.head.amount shouldBe 50.0
        case _ => fail("Expected CalculateDebts Success event")
    }

    "fail to calculate debts when no active group" in {
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.calculateDebts()

      observer.lastEvent shouldBe defined
      observer.lastEvent.get match
        case EventResponse.CalculateDebts(CalculateDebtsResult.NoActiveGroup, _) => succeed
        case _ => fail("Expected CalculateDebts NoActiveGroup event")
    }

    "undo successfully" in {
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.addGroup("TestGroup")
      controller.app.containsGroup("TestGroup") shouldBe true

      controller.undo()

      controller.app.containsGroup("TestGroup") shouldBe false
      observer.lastEvent shouldBe defined
      observer.lastEvent.get match
        case EventResponse.Undo(UndoResult.Success, _) => succeed
        case _                                         => fail("Expected Undo Success event")
    }

    "fail to undo when stack is empty" in {
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.undo()

      observer.lastEvent shouldBe defined
      observer.lastEvent.get match
        case EventResponse.Undo(UndoResult.EmptyStack, _) => succeed
        case _                                            => fail("Expected Undo EmptyStack event")
    }

    "redo successfully" in {
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.addGroup("TestGroup")
      controller.undo()
      controller.app.containsGroup("TestGroup") shouldBe false

      controller.redo()

      controller.app.containsGroup("TestGroup") shouldBe true
      observer.lastEvent shouldBe defined
      observer.lastEvent.get match
        case EventResponse.Redo(RedoResult.Success, _) => succeed
        case _                                         => fail("Expected Redo Success event")
    }

    "fail to redo when stack is empty" in {
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.redo()

      observer.lastEvent shouldBe defined
      observer.lastEvent.get match
        case EventResponse.Redo(RedoResult.EmptyStack, _) => succeed
        case _                                            => fail("Expected Redo EmptyStack event")
    }

    "handle undo when argsHandler returns unexpected response" in {
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)

      controller.addGroup("TestGroup")
      val sizeBefore = controller.undoManager.getUndoStackSize

      controller.undo()

      controller.undoManager.getUndoStackSize shouldBe <(sizeBefore)
    }

    "handle redo when argsHandler returns unexpected response" in {
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)

      controller.addGroup("TestGroup")
      controller.undo()
      val sizeBefore = controller.undoManager.getRedoStackSize

      controller.redo()

      controller.undoManager.getRedoStackSize shouldBe <(sizeBefore)
    }

    "handle gotoGroup edge cases" in {
      val group      = Group("TestGroup", List(Person("Alice"), Person("Bob")), Nil, Nil, NormalDebtStrategy())
      val app        = App(List(group), None, None, MainMenuState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.gotoGroup("TestGroup")

      observer.lastEvent shouldBe defined
      controller.app.active_group shouldBe defined
    }

    "handle addGroup edge cases" in {
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.addGroup("TestGroup")

      observer.lastEvent shouldBe defined
      controller.app.containsGroup("TestGroup") shouldBe true
    }

    "handle addUserToGroup edge cases" in {
      val group      = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val app        = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.addUserToGroup("Alice")

      observer.lastEvent shouldBe defined
      val updatedGroup = controller.app.getGroup("TestGroup").get
      updatedGroup.members should contain(Person("Alice"))
    }

    "handle addExpenseToGroup edge cases" in {
      val group      = Group("TestGroup", List(Person("Alice"), Person("Bob")), Nil, Nil, NormalDebtStrategy())
      val app        = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.addExpenseToGroup("Dinner", "Alice", 100.0, Date(1, 1, 2000))

      observer.lastEvent shouldBe defined
      val updatedGroup = controller.app.getGroup("TestGroup").get
      updatedGroup.expenses should have size 1
    }

    "handle setDebtStrategy edge cases" in {
      val group      = Group("TestGroup", List(Person("Alice")), Nil, Nil, NormalDebtStrategy())
      val app        = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.setDebtStrategy("simplified")

      observer.lastEvent shouldBe defined
      val updatedGroup = controller.app.getGroup("TestGroup").get
      updatedGroup.debt_strategy shouldBe a[SimplifiedDebtStrategy]
    }

    "handle calculateDebts edge cases" in {
      val alice      = Person("Alice")
      val bob        = Person("Bob")
      val expense    = Expense("Dinner", 100.0, Date(1, 1, 2000), alice, List(Share(alice, 50.0), Share(bob, 50.0)))
      val group      = Group("TestGroup", List(alice, bob), List(expense), Nil, NormalDebtStrategy())
      val app        = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val observer   = new TestObserver
      controller.add(observer)

      controller.calculateDebts()

      observer.lastEvent shouldBe defined
      observer.lastEvent.get match
        case EventResponse.CalculateDebts(CalculateDebtsResult.Success, debts) => debts should not be empty
        case _ => fail("Expected CalculateDebts Success event")
    }

    "handle complex operation sequence" in {
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)

      controller.addGroup("Group1")
      controller.addGroup("Group2")
      controller.app.allGroups should have size 2

      controller.gotoGroup("Group1")
      controller.addUserToGroup("Alice")
      controller.addUserToGroup("Bob")

      val group1 = controller.app.getGroup("Group1").get
      group1.members should have size 2

      controller.undo()
      val group1AfterUndo = controller.app.getGroup("Group1").get
      group1AfterUndo.members should have size 1

      controller.redo()
      val group1AfterRedo = controller.app.getGroup("Group1").get
      group1AfterRedo.members should have size 2

      controller.gotoMainMenu
      controller.app.active_group shouldBe None
    }

    "maintain state consistency through undo/redo" in {
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)

      controller.addGroup("TestGroup")
      controller.gotoGroup("TestGroup")
      controller.addUserToGroup("Alice")
      controller.addExpenseToGroup("Dinner", "Alice", 50.0, Date(1, 1, 2000))

      val groupBeforeUndo = controller.app.getGroup("TestGroup").get
      groupBeforeUndo.expenses should have size 1

      controller.undo()
      val groupAfterUndoExpense = controller.app.getGroup("TestGroup").get
      groupAfterUndoExpense.expenses shouldBe empty

      controller.undo()
      val groupAfterUndoUser = controller.app.getGroup("TestGroup").get
      groupAfterUndoUser.members shouldBe empty

      controller.redo()
      controller.redo()
      val groupAfterRedo = controller.app.getGroup("TestGroup").get
      groupAfterRedo.members should have size 1
      groupAfterRedo.expenses should have size 1
    }
  }
