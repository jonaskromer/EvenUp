package de.htwg.swe.evenup.control.BaseControllerImpl

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.AppComponent.BaseAppImpl.App
import de.htwg.swe.evenup.model.AppComponent.IApp
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.MainMenuState
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.InGroupState
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.InEmptyGroupState
import de.htwg.swe.evenup.model.GroupComponent.BaseGroupImpl.Group
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date
import de.htwg.swe.evenup.model.financial.ShareComponent.BaseShareImpl.Share
import de.htwg.swe.evenup.model.financial.ExpenseComponent.BaseExpenseImpl.Expense
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.NormalDebtStrategy
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.SimplifiedDebtStrategy
import de.htwg.swe.evenup.model.FileIOComponent.IFileIO
import de.htwg.swe.evenup.control.*
import de.htwg.swe.evenup.util.Observer
import de.htwg.swe.evenup.util.ObservableEvent
import de.htwg.swe.evenup.modules.Default.given

class ControllerSpec extends AnyWordSpec with Matchers:

  val date = Date(15, 6, 2025)

  class TestObserver extends Observer:
    var lastEvent: Option[ObservableEvent] = None
    def update(event: ObservableEvent): Unit = lastEvent = Some(event)

  // Use the default givens which loads from file
  def createController(): Controller =
    new Controller

  "Controller" should {

    "add a group with unique name" in:
      val controller = createController()
      val observer = new TestObserver
      controller.add(observer)
      val uniqueName = s"TestGroup_${System.currentTimeMillis}"

      controller.addGroup(uniqueName)

      controller.app.groups.map(_.name) should contain(uniqueName)

    "fail to add duplicate group" in:
      val controller = createController()
      val observer = new TestObserver
      controller.add(observer)

      // First add a unique group
      val uniqueName = s"DuplicateTest_${System.currentTimeMillis}"
      controller.addGroup(uniqueName)
      
      // Try to add the same group again
      controller.addGroup(uniqueName)

      observer.lastEvent.get match
        case EventResponse.AddGroup(result, _) => result shouldBe AddGroupResult.GroupExists
        case _ => fail("Expected AddGroup response")

    "goto an existing group" in:
      val controller = createController()
      val observer = new TestObserver
      controller.add(observer)

      // Vacation exists in the data file
      controller.gotoGroup("Vacation")

      controller.app.active_group should not be empty

    "fail to goto non-existent group" in:
      val controller = createController()
      val observer = new TestObserver
      controller.add(observer)

      controller.gotoGroup("NonExistentGroup_12345")

      observer.lastEvent.get shouldBe a[EventResponse.GotoGroup]
      observer.lastEvent.get match
        case EventResponse.GotoGroup(result, _) => result shouldBe GotoGroupResult.GroupNotFound
        case _ => fail("Expected GotoGroup response")

    "goto main menu from group" in:
      val controller = createController()
      val observer = new TestObserver
      controller.add(observer)

      controller.gotoGroup("Vacation")
      controller.gotoMainMenu

      observer.lastEvent.get shouldBe EventResponse.MainMenu

    "add user to group" in:
      val controller = createController()
      val observer = new TestObserver
      controller.add(observer)

      controller.gotoGroup("Vacation")
      val uniqueUser = s"TestUser_${System.currentTimeMillis}"
      controller.addUserToGroup(uniqueUser)

      val members = controller.app.active_group.get.members.map(_.name)
      members should contain(uniqueUser)

    "fail to add duplicate user" in:
      val controller = createController()
      val observer = new TestObserver
      controller.add(observer)

      controller.gotoGroup("Vacation")
      // Bryan exists in Vacation group
      controller.addUserToGroup("Bryan")

      observer.lastEvent.get match
        case EventResponse.AddUserToGroup(result, _, _) => result shouldBe AddUserToGroupResult.UserAlreadyAdded
        case _ => fail("Expected AddUserToGroup response")

    "fail to add user without active group" in:
      val controller = createController()
      val observer = new TestObserver
      controller.add(observer)

      // Make sure we're in main menu
      controller.gotoMainMenu
      controller.addUserToGroup("TestUser")

      observer.lastEvent.get match
        case EventResponse.AddUserToGroup(result, _, _) => result shouldBe AddUserToGroupResult.NoActiveGroup
        case _ => fail("Expected AddUserToGroup response")

    "add expense to group" in:
      val controller = createController()
      val observer = new TestObserver
      controller.add(observer)

      controller.gotoGroup("Vacation")
      val expenseCount = controller.app.active_group.get.expenses.length
      controller.addExpenseToGroup("TestDinner", "Bryan", 50.0, date, Some("Bryan:25_Jonas:25"))

      controller.app.active_group.get.expenses.length shouldBe expenseCount + 1

    "fail to add expense without active group" in:
      val controller = createController()
      val observer = new TestObserver
      controller.add(observer)

      controller.gotoMainMenu
      controller.addExpenseToGroup("Dinner", "Alice", 30.0, date, None)

      observer.lastEvent.get match
        case EventResponse.AddExpenseToGroup(result, _) => result shouldBe AddExpenseToGroupResult.NoActiveGroup
        case _ => fail("Expected AddExpenseToGroup response")

    "set debt strategy" in:
      val controller = createController()
      val observer = new TestObserver
      controller.add(observer)

      controller.gotoGroup("Vacation")
      controller.setDebtStrategy("simplified")

      controller.app.active_group.get.debt_strategy shouldBe a[SimplifiedDebtStrategy]

    "fail to set debt strategy without active group" in:
      val controller = createController()
      val observer = new TestObserver
      controller.add(observer)

      controller.gotoMainMenu
      controller.setDebtStrategy("simplified")

      observer.lastEvent.get match
        case EventResponse.SetDebtStrategy(result, _) => result shouldBe SetDebtStrategyResult.NoActiveGroup
        case _ => fail("Expected SetDebtStrategy response")

    "calculate debts" in:
      val controller = createController()
      val observer = new TestObserver
      controller.add(observer)

      controller.gotoGroup("Vacation")
      controller.calculateDebts()

      observer.lastEvent.get shouldBe a[EventResponse.CalculateDebts]

    "fail to calculate debts without active group" in:
      val controller = createController()
      val observer = new TestObserver
      controller.add(observer)

      controller.gotoMainMenu
      controller.calculateDebts()

      observer.lastEvent.get match
        case EventResponse.CalculateDebts(result, _) => result shouldBe CalculateDebtsResult.NoActiveGroup
        case _ => fail("Expected CalculateDebts response")

    "undo with empty stack" in:
      val controller = createController()
      val observer = new TestObserver
      controller.add(observer)

      controller.undo()

      observer.lastEvent.get match
        case EventResponse.Undo(result, _) => result shouldBe UndoResult.EmptyStack
        case _ => fail("Expected Undo response")

    "redo with empty stack" in:
      val controller = createController()
      val observer = new TestObserver
      controller.add(observer)

      controller.redo()

      observer.lastEvent.get match
        case EventResponse.Redo(result, _) => result shouldBe RedoResult.EmptyStack
        case _ => fail("Expected Redo response")

    "undo after action" in:
      val controller = createController()
      val observer = new TestObserver
      controller.add(observer)

      val uniqueName = s"UndoGroup_${System.currentTimeMillis}"
      controller.addGroup(uniqueName)
      controller.app.groups.map(_.name) should contain(uniqueName)

      controller.undo()
      controller.app.groups.map(_.name) should not contain (uniqueName)

    "redo after undo" in:
      val controller = createController()
      val observer = new TestObserver
      controller.add(observer)

      val uniqueName = s"RedoGroup_${System.currentTimeMillis}"
      controller.addGroup(uniqueName)
      controller.undo()
      controller.app.groups.map(_.name) should not contain (uniqueName)

      controller.redo()
      controller.app.groups.map(_.name) should contain(uniqueName)

    "load function is called on initialization" in:
      val controller = createController()
      controller.app should not be null
      controller.app.groups should not be empty
  }

  "Controller.argsHandler" should {

    "be accessible" in:
      val controller = createController()
      controller.argsHandler shouldBe a[ArgsHandler]
  }
