package de.htwg.swe.evenup.view.tui

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import java.io.{ByteArrayOutputStream, PrintStream}

import de.htwg.swe.evenup.control._
import de.htwg.swe.evenup.model._
import de.htwg.swe.evenup.model.financial.Expense
import de.htwg.swe.evenup.model.state.MainMenuState
import de.htwg.swe.evenup.model.financial.debt.NormalDebtStrategy
import de.htwg.swe.evenup.util.ObservableEvent
import de.htwg.swe.evenup.model.financial.debt.Debt
import de.htwg.swe.evenup.model.financial.debt.SimplifiedDebtStrategy
import de.htwg.swe.evenup.model.financial.Share

import scala.util.{Try, Success, Failure}

class TuiSpec extends AnyWordSpec with Matchers:

  "Tui" should {

    "print welcome message on init" in {
      val controller = new Controller(App(Nil, None, None, MainMenuState()))
      val outputStream = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream)) {
        new Tui(controller)
      }
      
      val output = outputStream.toString
      output should include("Welcome to EvenUp!")
      output should include(s"Start by adding a group with => ${TuiKeys.newGroup.key}")
    }

    "print help correctly" in {
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val outputStream1 = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream1)) {
        new Tui(controller)
      }

      val outputStream2 = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outputStream2)) {
        tui.printHelp(app.state)
      }

      val output = outputStream2.toString

      val allowedKeys = TuiKeys.values.filter(_.allowed(app.state))
      allowedKeys.foreach { key =>
        output should include(key.key)
      }

      val disallowedKeys = TuiKeys.values.filterNot(_.allowed(app.state))
      disallowedKeys.foreach { key =>
        output should not include key.key
      }
    }
  }

  "TuiStringBuilder handlers" should {

    val alice      = Person("Alice")
    val bob        = Person("Bob")
    val group      = Group("TestGroup", List(alice, bob), Nil, Nil, NormalDebtStrategy())
    val app        = App(List(group), None, Some(group), MainMenuState())
    val controller = new Controller(app)
    val builder    = new TuiStringBuilder(controller)

    "handle add group success" in {
      val msg = builder.addGroupHandler(
        EventResponse.AddGroup(AddGroupResult.Success, group)
      )
      msg should include("Added group TestGroup")
    }

    "handle add group exists" in {
      val msg = builder.addGroupHandler(
        EventResponse.AddGroup(AddGroupResult.GroupExists, group)
      )
      msg should include("The group TestGroup already exists")
    }

    "handle add user to group success" in {
      val msg = builder.addUserToGroupHandler(
        EventResponse.AddUserToGroup(AddUserToGroupResult.Success, alice, group)
      )
      msg should include("Added Alice to TestGroup")
    }

    "handle user already added" in {
      val msg = builder.addUserToGroupHandler(
        EventResponse.AddUserToGroup(AddUserToGroupResult.UserAlreadyAdded, alice, group)
      )
      msg should include("User Alice already added to group TestGroup")
    }

    "handle no active group when adding user" in {
      val emptyGroup = Group("", Nil, Nil, Nil, NormalDebtStrategy())
      val msg = builder.addUserToGroupHandler(
        EventResponse.AddUserToGroup(AddUserToGroupResult.NoActiveGroup, alice, emptyGroup)
      )
      msg should include("Cannot add Alice because there is no active group")
    }

    "handle add expense success" in {
      val shares  = List(Share(alice, 20.0), Share(bob, 10.0))
      val expense = Expense("Dinner", 30.0, Date(1, 1, 2025), alice, shares)
      val msg = builder.expenseHandler(
        EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.Success, expense)
      )
      msg should include("Added expense")
    }

    "handle no active group when adding expense" in {
      val expense = Expense("Dinner", 100, Date(1, 1, 2025), alice, Nil)
      val msg = builder.expenseHandler(
        EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.NoActiveGroup, expense)
      )
      msg should include("Please first goto a group")
    }

    "handle shares sum wrong" in {
      val expense = Expense("Dinner", 100, Date(1, 1, 2025), alice, Nil)
      val msg = builder.expenseHandler(
        EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.SharesSumWrong, expense)
      )
      msg should include("The sum of the shares do not match with the sum of the expense")
    }

    "handle share references wrong user" in {
      val expense = Expense("Dinner", 100, Date(1, 1, 2025), alice, Nil)
      val msg = builder.expenseHandler(
        EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.SharesPersonNotFound, expense)
      )
      msg should include("Wrong user in shares")
    }

    "handle paidBy user not in group" in {
      val expense = Expense("Dinner", 100, Date(1, 1, 2025), alice, Nil)
      val msg = builder.expenseHandler(
        EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.PaidByNotFound, expense)
      )
      msg should include("Please first add Alice to the group before using in expense")
    }

    "handle goto group success" in {
      val msg = builder.gotoGroupHandler(
        EventResponse.GotoGroup(GotoGroupResult.Success, group)
      )
      msg should include("Set active group to TestGroup")
    }

    "handle goto group not found" in {
      val msg = builder.gotoGroupHandler(
        EventResponse.GotoGroup(GotoGroupResult.GroupNotFound, group)
      )
      msg should include("Unable to find group TestGroup")
    }

    "handle goto empty group" in {
      val emptyGroup = Group("EmptyGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val msg = builder.gotoGroupHandler(
        EventResponse.GotoGroup(GotoGroupResult.SuccessEmptyGroup, emptyGroup)
      )
      msg should include("The group EmptyGroup is empty")
    }

    "handle empty debts" in {
      val msg = builder.debtHandler(
        EventResponse.CalculateDebts(CalculateDebtsResult.Success, Nil)
      )
      msg should include("No debts to settle. Group is Evend Up!")
    }

    "handle calculated debts" in {
      val debt = Debt(from = bob, to = alice, amount = 20.0)
      val msg = builder.debtHandler(
        EventResponse.CalculateDebts(CalculateDebtsResult.Success, List(debt))
      )
      msg should include("Calculated debts:")
      msg should include(debt.toString)
    }

    "handle switch to simplified strategy" in {
      val msg = builder.debtHandler(
        EventResponse.SetDebtStrategy(SetDebtStrategyResult.Success, SimplifiedDebtStrategy())
      )
      msg should include("Switched to simplified debt calculation strategy")
    }

    "handle switch to normal strategy" in {
      val msg = builder.debtHandler(
        EventResponse.SetDebtStrategy(SetDebtStrategyResult.Success, NormalDebtStrategy())
      )
      msg should include("Switched to normal debt calculation strategy")
    }

    "handle quit command" in {
      val msg = builder.commandHandler(EventResponse.Quit)
      msg should include("Goodbye!")
    }

    "handle main menu command" in {
      val msg = builder.commandHandler(EventResponse.MainMenu)
      msg should include("Go to a group")
      msg should include("Available Groups")
    }

    "handle undo success" in {
      val msg = builder.undoRedoHandler(
        EventResponse.Undo(UndoResult.Success, 5)
      )
      msg should include("Undo successfull")
      msg should include("5")
    }

    "handle undo empty stack" in {
      val msg = builder.undoRedoHandler(
        EventResponse.Undo(UndoResult.EmptyStack, 0)
      )
      msg should include("Nothing to undo")
    }

    "handle redo success" in {
      val msg = builder.undoRedoHandler(
        EventResponse.Redo(RedoResult.Success, 3)
      )
      msg should include("Redo successfull")
      msg should include("3")
    }

    "handle redo empty stack" in {
      val msg = builder.undoRedoHandler(
        EventResponse.Redo(RedoResult.EmptyStack, 0)
      )
      msg should include("Nothing to redo")
    }
  }

  "Tui.processInput" should {

    "call printHelp when input is help key" in {
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val outputStream1 = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream1)) {
        new Tui(controller)
      }

      val outputStream2 = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outputStream2)) {
        tui.processInput(TuiKeys.help.key)
      }

      val output = outputStream2.toString
      output should include("==>")
    }

    "call controller.quit when input is quit key" in {
      val app        = App(Nil, None, None, MainMenuState())
      var quitCalled = 0

      val controllerStub = new Controller(app) {
        override def quit: Unit = {
          quitCalled += 1
        }
      }

      val outputStream = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream)) {
        new Tui(controllerStub)
      }
      
      tui.processInput(TuiKeys.quit.key)
      quitCalled shouldBe 1
    }

    "call controller.gotoMainMenu when input is MainMenu key" in {
      val app            = App(Nil, None, None, MainMenuState())
      var mainMenuCalled = false

      val controllerStub = new Controller(app) {
        override def gotoMainMenu: Unit = mainMenuCalled = true
      }

      val outputStream = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream)) {
        new Tui(controllerStub)
      }
      
      tui.processInput(TuiKeys.MainMenu.key)
      mainMenuCalled shouldBe true
    }

    "call controller.gotoGroup when input is gotoGroup key" in {
      val app                       = App(Nil, None, None, MainMenuState())
      var gotoGroupCalled: Option[String] = None

      val controllerStub = new Controller(app) {
        override def gotoGroup(groupName: String): Unit = gotoGroupCalled = Some(groupName)
      }

      val outputStream = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream)) {
        new Tui(controllerStub)
      }
      
      val groupName = "TestGroup"
      tui.processInput(s"${TuiKeys.gotoGroup.key} $groupName")
      gotoGroupCalled shouldBe Some(groupName)
    }

    "call controller.addGroup when input is newGroup key" in {
      val app                        = App(Nil, None, None, MainMenuState())
      var addGroupCalled: Option[String] = None

      val controllerStub = new Controller(app) {
        override def addGroup(groupName: String): Unit = addGroupCalled = Some(groupName)
      }

      val outputStream = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream)) {
        new Tui(controllerStub)
      }
      
      val groupName = "MyTestGroup"
      tui.processInput(s"${TuiKeys.newGroup.key} $groupName")
      addGroupCalled shouldBe Some(groupName)
    }

    "call controller.addUserToGroup for each person when input is addUserToGroup key" in {
      val app                   = App(Nil, None, None, MainMenuState())
      var addedUsers: List[String] = Nil

      val controllerStub = new Controller(app) {
        override def addUserToGroup(userName: String): Unit = addedUsers = addedUsers :+ userName
      }

      val outputStream = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream)) {
        new Tui(controllerStub)
      }
      
      tui.processInput(s"${TuiKeys.addUserToGroup.key} Alice Bob Charlie")
      addedUsers shouldBe List("Alice", "Bob", "Charlie")
    }

    "call controller.addExpenseToGroup when input is addExpense key" in {
      val app                          = App(Nil, None, None, MainMenuState())
      var capturedDescription: String  = ""
      var capturedPaidBy: String       = ""
      var capturedAmount: Double       = 0.0
      var capturedShares: Option[String] = None

      val controllerStub = new Controller(app) {
        override def addExpenseToGroup(
          description: String,
          paid_by: String,
          sum: Double,
          date: Date = Date(1, 1, 2000),
          shares: Option[String] = None
        ): Unit = {
          capturedDescription = description
          capturedPaidBy = paid_by
          capturedAmount = sum
          capturedShares = shares
        }
      }

      val outputStream = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream)) {
        new Tui(controllerStub)
      }
      
      tui.processInput(s"${TuiKeys.addExpense.key} Dinner Alice 50.0")

      capturedDescription shouldBe "Dinner"
      capturedPaidBy shouldBe "Alice"
      capturedAmount shouldBe 50.0
      capturedShares shouldBe None
    }

    "call controller.addExpenseToGroup with shares when provided" in {
      val app                          = App(Nil, None, None, MainMenuState())
      var capturedShares: Option[String] = None

      val controllerStub = new Controller(app) {
        override def addExpenseToGroup(
          description: String,
          paid_by: String,
          sum: Double,
          date: Date = Date(1, 1, 2000),
          shares: Option[String] = None
        ): Unit = {
          capturedShares = shares
        }
      }

      val outputStream = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream)) {
        new Tui(controllerStub)
      }
      
      tui.processInput(s"${TuiKeys.addExpense.key} Dinner Alice 50.0 Alice:30,Bob:20")
      capturedShares shouldBe Some("Alice:30,Bob:20")
    }

    "call controller.undo when input is undo key" in {
      val app        = App(Nil, None, None, MainMenuState())
      var undoCalled = false

      val controllerStub = new Controller(app) {
        override def undo(): Unit = undoCalled = true
      }

      val outputStream = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream)) {
        new Tui(controllerStub)
      }
      
      tui.processInput(TuiKeys.undo.key)
      undoCalled shouldBe true
    }

    "call controller.redo when input is redo key" in {
      val app        = App(Nil, None, None, MainMenuState())
      var redoCalled = false

      val controllerStub = new Controller(app) {
        override def redo(): Unit = redoCalled = true
      }

      val outputStream = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream)) {
        new Tui(controllerStub)
      }
      
      tui.processInput(TuiKeys.redo.key)
      redoCalled shouldBe true
    }

    "call controller.calculateDebts when input is calculateDebts key" in {
      val app               = App(Nil, None, None, MainMenuState())
      var calculateCalled   = false

      val controllerStub = new Controller(app) {
        override def calculateDebts(): Unit = calculateCalled = true
      }

      val outputStream = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream)) {
        new Tui(controllerStub)
      }
      
      tui.processInput(TuiKeys.calculateDebts.key)
      calculateCalled shouldBe true
    }

    "call controller.setDebtStrategy when input is setStrategy key" in {
      val app                         = App(Nil, None, None, MainMenuState())
      var capturedStrategy: Option[String] = None

      val controllerStub = new Controller(app) {
        override def setDebtStrategy(strategy: String): Unit = 
          capturedStrategy = Some(strategy)
      }

      val outputStream = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream)) {
        new Tui(controllerStub)
      }
      
      tui.processInput(s"${TuiKeys.setStrategy.key} Simplified")
      capturedStrategy shouldBe Some("Simplified")
    }

    "handle parse failure gracefully" in {
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val outputStream1 = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream1)) {
        new Tui(controller)
      }

      val outputStream2 = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outputStream2)) {
        // This will trigger a parse failure if Parser validates input
        tui.processInput("")
      }

      // The output should contain an error message or prompt
      val output = outputStream2.toString
      // Check that something was printed (either error or prompt)
      output should not be empty
    }
  }

  "Tui.update" should {

    "handle MainMenu event and print available groups" in {
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val outputStream1 = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream1)) {
        new Tui(controller)
      }

      val outputStream2 = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outputStream2)) {
        tui.update(EventResponse.MainMenu)
      }

      val output = outputStream2.toString
      output should include("Available Groups")
    }

    "handle Quit event and print Goodbye!" in {
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val outputStream1 = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream1)) {
        new Tui(controller)
      }

      val outputStream2 = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outputStream2)) {
        tui.update(EventResponse.Quit)
      }

      val output = outputStream2.toString
      output should include("Goodbye!")
    }

    "handle AddGroup success event" in {
      val group      = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val outputStream1 = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream1)) {
        new Tui(controller)
      }

      val outputStream2 = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outputStream2)) {
        tui.update(EventResponse.AddGroup(AddGroupResult.Success, group))
      }

      val output = outputStream2.toString
      output should include("Added group TestGroup")
    }

    "handle CalculateDebts with empty list" in {
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val outputStream1 = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream1)) {
        new Tui(controller)
      }

      val outputStream2 = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outputStream2)) {
        tui.update(EventResponse.CalculateDebts(CalculateDebtsResult.Success, Nil))
      }

      val output = outputStream2.toString
      output should include("No debts to settle. Group is Evend Up!")
    }

    "handle CalculateDebts with debts" in {
      val alice = Person("Alice")
      val bob   = Person("Bob")
      val debt  = Debt(from = bob, to = alice, amount = 20.0)
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val outputStream1 = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream1)) {
        new Tui(controller)
      }

      val outputStream2 = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outputStream2)) {
        tui.update(EventResponse.CalculateDebts(CalculateDebtsResult.Success, List(debt)))
      }

      val output = outputStream2.toString
      output should include("Calculated debts:")
      output should include(debt.toString)
    }

    "handle UncoveredFailure event" in {
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val outputStream1 = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream1)) {
        new Tui(controller)
      }

      val outputStream2 = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outputStream2)) {
        tui.update(EventResponse.UncoveredFailure("test error"))
      }

      val output = outputStream2.toString
      output should include("##ERROR##")
      output should include("test error")
    }

    "handle unhandled event" in {
      val app        = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val outputStream1 = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream1)) {
        new Tui(controller)
      }

      val dummyEvent = new ObservableEvent {}
      val outputStream2 = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outputStream2)) {
        tui.update(dummyEvent)
      }

      val output = outputStream2.toString
      output should include("Unhandled event")
    }

    "handle AddUserToGroup success event" in {
      val alice = Person("Alice")
      val group = Group("TestGroup", List(alice), Nil, Nil, NormalDebtStrategy())
      val app   = App(List(group), None, Some(group), MainMenuState())
      val controller = new Controller(app)
      val outputStream1 = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream1)) {
        new Tui(controller)
      }

      val outputStream2 = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outputStream2)) {
        tui.update(EventResponse.AddUserToGroup(AddUserToGroupResult.Success, alice, group))
      }

      val output = outputStream2.toString
      output should include("Added Alice to TestGroup")
    }

    "handle GotoGroup success event" in {
      val group = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val app   = App(List(group), None, None, MainMenuState())
      val controller = new Controller(app)
      val outputStream1 = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream1)) {
        new Tui(controller)
      }

      val outputStream2 = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outputStream2)) {
        tui.update(EventResponse.GotoGroup(GotoGroupResult.Success, group))
      }

      val output = outputStream2.toString
      output should include("Set active group to TestGroup")
    }

    "handle AddExpenseToGroup success event" in {
      val alice = Person("Alice")
      val expense = Expense("Dinner", 50.0, Date(1, 1, 2025), alice, Nil)
      val app   = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val outputStream1 = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream1)) {
        new Tui(controller)
      }

      val outputStream2 = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outputStream2)) {
        tui.update(EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.Success, expense))
      }

      val output = outputStream2.toString
      output should include("Added expense")
    }

    "handle Undo success event" in {
      val app = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val outputStream1 = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream1)) {
        new Tui(controller)
      }

      val outputStream2 = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outputStream2)) {
        tui.update(EventResponse.Undo(UndoResult.Success, 3))
      }

      val output = outputStream2.toString
      output should include("Undo successfull")
    }

    "handle Redo success event" in {
      val app = App(Nil, None, None, MainMenuState())
      val controller = new Controller(app)
      val outputStream1 = new ByteArrayOutputStream()
      val tui = Console.withOut(new PrintStream(outputStream1)) {
        new Tui(controller)
      }

      val outputStream2 = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outputStream2)) {
        tui.update(EventResponse.Redo(RedoResult.Success, 2))
      }

      val output = outputStream2.toString
      output should include("Redo successfull")
    }
  }