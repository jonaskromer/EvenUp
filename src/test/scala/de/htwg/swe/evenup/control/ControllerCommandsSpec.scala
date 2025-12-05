package de.htwg.swe.evenup.control

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import de.htwg.swe.evenup.model._
import de.htwg.swe.evenup.model.financial.Expense
import de.htwg.swe.evenup.model.financial.debt.{NormalDebtStrategy, SimplifiedDebtStrategy, Debt}
import de.htwg.swe.evenup.model.state.{MainMenuState, InGroupState}
import de.htwg.swe.evenup.util.ObservableEvent

class ControllerCommandsSpec extends AnyWordSpec with Matchers:

  "AddGroupCommand" should {

    "add group to app and set it as active group" in {
      val app = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val group = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val command = AddGroupCommand(controller, group)

      command.doStep

      controller.app.allGroups should contain(group)
      controller.app.active_group shouldBe Some(group)
    }

    "update app state to InGroupState" in {
      val app = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val group = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val command = AddGroupCommand(controller, group)

      command.doStep

      controller.app.state shouldBe a[InGroupState]
    }

    "notify observers with success event" in {
      val app = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val group = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val command = AddGroupCommand(controller, group)

      var notifiedEvent: Option[ObservableEvent] = None
      controller.add((event: ObservableEvent) => notifiedEvent = Some(event))

      command.doStep

      notifiedEvent shouldBe Some(EventResponse.AddGroup(AddGroupResult.Success, group))
    }

    "be undoable" in {
      val app = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val group = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val command = AddGroupCommand(controller, group)

      command.doStep
      controller.app.allGroups should contain(group)

      command.undoStep
      controller.app.allGroups should not contain group
      controller.app.active_group shouldBe None
    }

    "be redoable" in {
      val app = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val group = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val command = AddGroupCommand(controller, group)

      command.doStep
      command.undoStep
      command.redoStep

      controller.app.allGroups should contain(group)
      controller.app.active_group shouldBe Some(group)
    }
  }

  "GotoMainMenuCommand" should {

    "clear active group" in {
      val group = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val app = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val command = GotoMainMenuCommand(controller)

      command.doStep

      controller.app.active_group shouldBe None
    }

    "update app state to MainMenuState" in {
      val group = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val app = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val command = GotoMainMenuCommand(controller)

      command.doStep

      controller.app.state shouldBe a[MainMenuState]
    }

    "notify observers with MainMenu event" in {
      val group = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val app = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val command = GotoMainMenuCommand(controller)

      var notifiedEvent: Option[ObservableEvent] = None
      controller.add((event: ObservableEvent) => notifiedEvent = Some(event))

      command.doStep

      notifiedEvent shouldBe Some(EventResponse.MainMenu)
    }

    "be undoable" in {
      val group = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val app = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val command = GotoMainMenuCommand(controller)

      command.doStep
      controller.app.active_group shouldBe None

      command.undoStep
      controller.app.active_group shouldBe Some(group)
    }
  }

  "GotoGroupCommand" should {

    "set active group" in {
      val group = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val app = App(List(group), None, None, MainMenuState())
      val controller = new Controller(app)
      val command = GotoGroupCommand(controller, group)

      command.doStep

      controller.app.active_group shouldBe Some(group)
    }

    "notify observers with success event" in {
      val group = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val app = App(List(group), None, None, MainMenuState())
      val controller = new Controller(app)
      val command = GotoGroupCommand(controller, group)

      var notifiedEvent: Option[ObservableEvent] = None
      controller.add((event: ObservableEvent) => notifiedEvent = Some(event))

      command.doStep

      notifiedEvent shouldBe Some(EventResponse.GotoGroup(GotoGroupResult.Success, group))
    }

    "be undoable" in {
      val group = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val app = App(List(group), None, None, MainMenuState())
      val controller = new Controller(app)
      val command = GotoGroupCommand(controller, group)

      command.doStep
      controller.app.active_group shouldBe Some(group)

      command.undoStep
      controller.app.active_group shouldBe None
    }
  }

  "GotoEmptyGroupCommand" should {

    "set active group" in {
      val group = Group("EmptyGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val app = App(List(group), None, None, MainMenuState())
      val controller = new Controller(app)
      val command = GotoEmptyGroupCommand(controller, group)

      command.doStep

      controller.app.active_group shouldBe Some(group)
    }

    "notify observers with SuccessEmptyGroup event" in {
      val group = Group("EmptyGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val app = App(List(group), None, None, MainMenuState())
      val controller = new Controller(app)
      val command = GotoEmptyGroupCommand(controller, group)

      var notifiedEvent: Option[ObservableEvent] = None
      controller.add((event: ObservableEvent) => notifiedEvent = Some(event))

      command.doStep

      notifiedEvent shouldBe Some(EventResponse.GotoGroup(GotoGroupResult.SuccessEmptyGroup, group))
    }

    "be undoable" in {
      val group = Group("EmptyGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val app = App(List(group), None, None, MainMenuState())
      val controller = new Controller(app)
      val command = GotoEmptyGroupCommand(controller, group)

      command.doStep
      controller.app.active_group shouldBe Some(group)

      command.undoStep
      controller.app.active_group shouldBe None
    }
  }

  "AddUserToGroupCommand" should {

    "add user to active group" in {
      val alice = Person("Alice")
      val group = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val app = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val command = AddUserToGroupCommand(controller, alice)

      command.doStep

      controller.app.active_group.get.members should contain(alice)
    }

    "update the group in app's group list" in {
      val alice = Person("Alice")
      val group = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val app = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val command = AddUserToGroupCommand(controller, alice)

      command.doStep

      val updatedGroup = controller.app.allGroups.find(_.name == "TestGroup").get
      updatedGroup.members should contain(alice)
    }

    "update active group reference" in {
      val alice = Person("Alice")
      val group = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val app = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val command = AddUserToGroupCommand(controller, alice)

      command.doStep

      controller.app.active_group.get.members should contain(alice)
    }

    "notify observers with success event" in {
      val alice = Person("Alice")
      val group = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val app = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val command = AddUserToGroupCommand(controller, alice)

      var notifiedEvent: Option[ObservableEvent] = None
      controller.add((event: ObservableEvent) => notifiedEvent = Some(event))

      command.doStep

      notifiedEvent match
        case Some(EventResponse.AddUserToGroup(AddUserToGroupResult.Success, user, updatedGroup)) =>
          user shouldBe alice
          updatedGroup.members should contain(alice)
        case _ => fail("Expected AddUserToGroup success event")
    }

    "be undoable" in {
      val alice = Person("Alice")
      val group = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val app = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val command = AddUserToGroupCommand(controller, alice)

      command.doStep
      controller.app.active_group.get.members should contain(alice)

      command.undoStep
      controller.app.active_group.get.members should not contain alice
    }
  }

  "AddExpenseToGroupCommand" should {

    "add expense to active group" in {
      val alice = Person("Alice")
      val group = Group("TestGroup", List(alice), Nil, Nil, NormalDebtStrategy())
      val expense = Expense("Dinner", 50.0, Date(1, 1, 2025), alice, Nil)
      val app = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val command = AddExpenseToGroupCommand(controller, expense)

      command.doStep

      controller.app.active_group.get.expenses should contain(expense)
    }

    "update the group in app's group list" in {
      val alice = Person("Alice")
      val group = Group("TestGroup", List(alice), Nil, Nil, NormalDebtStrategy())
      val expense = Expense("Dinner", 50.0, Date(1, 1, 2025), alice, Nil)
      val app = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val command = AddExpenseToGroupCommand(controller, expense)

      command.doStep

      val updatedGroup = controller.app.allGroups.find(_.name == "TestGroup").get
      updatedGroup.expenses should contain(expense)
    }

    "notify observers with success event" in {
      val alice = Person("Alice")
      val group = Group("TestGroup", List(alice), Nil, Nil, NormalDebtStrategy())
      val expense = Expense("Dinner", 50.0, Date(1, 1, 2025), alice, Nil)
      val app = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val command = AddExpenseToGroupCommand(controller, expense)

      var notifiedEvent: Option[ObservableEvent] = None
      controller.add((event: ObservableEvent) => notifiedEvent = Some(event))

      command.doStep

      notifiedEvent shouldBe Some(EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.Success, expense))
    }

    "be undoable" in {
      val alice = Person("Alice")
      val group = Group("TestGroup", List(alice), Nil, Nil, NormalDebtStrategy())
      val expense = Expense("Dinner", 50.0, Date(1, 1, 2025), alice, Nil)
      val app = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val command = AddExpenseToGroupCommand(controller, expense)

      command.doStep
      controller.app.active_group.get.expenses should contain(expense)

      command.undoStep
      controller.app.active_group.get.expenses should not contain expense
    }
  }

  "SetDebtCalculationStrategy" should {

    "update strategy in active group" in {
      val alice = Person("Alice")
      val group = Group("TestGroup", List(alice), Nil, Nil, NormalDebtStrategy())
      val app = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val newStrategy = SimplifiedDebtStrategy()
      val command = SetDebtCalculationStrategy(controller, newStrategy)

      command.doStep

      controller.app.active_group.get.debt_strategy shouldBe newStrategy
    }

    "update the group in app's group list" in {
      val alice = Person("Alice")
      val group = Group("TestGroup", List(alice), Nil, Nil, NormalDebtStrategy())
      val app = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val newStrategy = SimplifiedDebtStrategy()
      val command = SetDebtCalculationStrategy(controller, newStrategy)

      command.doStep

      val updatedGroup = controller.app.allGroups.find(_.name == "TestGroup").get
      updatedGroup.debt_strategy shouldBe newStrategy
    }

    "notify observers with success event" in {
      val alice = Person("Alice")
      val group = Group("TestGroup", List(alice), Nil, Nil, NormalDebtStrategy())
      val app = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val newStrategy = SimplifiedDebtStrategy()
      val command = SetDebtCalculationStrategy(controller, newStrategy)

      var notifiedEvent: Option[ObservableEvent] = None
      controller.add((event: ObservableEvent) => notifiedEvent = Some(event))

      command.doStep

      notifiedEvent shouldBe Some(EventResponse.SetDebtStrategy(SetDebtStrategyResult.Success, newStrategy))
    }

    "be undoable" in {
      val alice = Person("Alice")
      val group = Group("TestGroup", List(alice), Nil, Nil, NormalDebtStrategy())
      val app = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val oldStrategy = group.debt_strategy
      val newStrategy = SimplifiedDebtStrategy()
      val command = SetDebtCalculationStrategy(controller, newStrategy)

      command.doStep
      controller.app.active_group.get.debt_strategy shouldBe newStrategy

      command.undoStep
      controller.app.active_group.get.debt_strategy shouldBe oldStrategy
    }
  }

  "CalculateDebtsCommand" should {

    "notify observers with debts" in {
      val alice = Person("Alice")
      val bob = Person("Bob")
      val group = Group("TestGroup", List(alice, bob), Nil, Nil, NormalDebtStrategy())
      val app = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val debts = List(Debt(bob, alice, 25.0))
      val command = CalculateDebtsCommand(controller, debts)

      var notifiedEvent: Option[ObservableEvent] = None
      controller.add((event: ObservableEvent) => notifiedEvent = Some(event))

      command.doStep

      notifiedEvent shouldBe Some(EventResponse.CalculateDebts(CalculateDebtsResult.Success, debts))
    }

    "notify observers with empty debts list" in {
      val alice = Person("Alice")
      val group = Group("TestGroup", List(alice), Nil, Nil, NormalDebtStrategy())
      val app = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val debts = Nil
      val command = CalculateDebtsCommand(controller, debts)

      var notifiedEvent: Option[ObservableEvent] = None
      controller.add((event: ObservableEvent) => notifiedEvent = Some(event))

      command.doStep

      notifiedEvent shouldBe Some(EventResponse.CalculateDebts(CalculateDebtsResult.Success, Nil))
    }

    "be undoable (no-op for calculation)" in {
      val alice = Person("Alice")
      val bob = Person("Bob")
      val group = Group("TestGroup", List(alice, bob), Nil, Nil, NormalDebtStrategy())
      val app = App(List(group), None, Some(group), InGroupState())
      val controller = new Controller(app)
      val debts = List(Debt(bob, alice, 25.0))
      val command = CalculateDebtsCommand(controller, debts)

      val initialState = controller.app
      command.doStep
      command.undoStep

      controller.app shouldBe initialState
    }
  }

  "Command undo/redo chain" should {

    "support multiple undo operations in sequence" in {
      var app = App(Nil, None, None, MainMenuState())
      var controller = new Controller(app)
      
      val group1 = Group("Group1", Nil, Nil, Nil, NormalDebtStrategy())
      val group2 = Group("Group2", Nil, Nil, Nil, NormalDebtStrategy())
      
      val command1 = AddGroupCommand(controller, group1)
      val command2 = AddGroupCommand(controller, group2)

      command1.doStep
      command2.doStep
      
      controller.app.allGroups should have size 2
    }

    "support redo after undo" in {
      val app = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      
      val group = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val command = AddGroupCommand(controller, group)

      command.doStep
      controller.app.allGroups should contain(group)

      command.undoStep
      controller.app.allGroups should not contain group

      command.redoStep
      controller.app.allGroups should contain(group)
    }
  }