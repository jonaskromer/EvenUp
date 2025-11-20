package de.htwg.swe.evenup.view.tui

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import java.io.{ByteArrayOutputStream, PrintStream}

import de.htwg.swe.evenup.control._
import de.htwg.swe.evenup.model._
import de.htwg.swe.evenup.util.ObservableEvent

class TuiSpec extends AnyWordSpec with Matchers:

  "Tui" should {

    "print welcome message on init" in {
      val controller = new Controller(App(Nil, None, None))
      val tui        = new Tui(controller)
      tui.spacer.length should be > 0
    }

    "process new group input" in {
      val controller = new Controller(App(Nil, None, None))
      val tui        = new Tui(controller)
      val group      = Group("TestGroup", Nil, Nil, Nil)
      val msg        = tui.addGroupHandler(
        ControllerEvent.AddGroup(AddGroupResult.Success, group)
      )
      msg should include("Added group TestGroup")
    }

    "process add user input" in {
      val group      = Group("TestGroup", Nil, Nil, Nil)
      val controller = new Controller(App(List(group), None, Some(group)))
      val tui        = new Tui(controller)
      val user       = Person("Alice")
      val msg        = tui.addUserToGroupHandler(
        ControllerEvent.AddUserToGroup(AddUserToGroupResult.Success, user)
      )
      msg should include("Added Alice to TestGroup")
    }

    "handle add expense with shares" in {
      val alice      = Person("Alice")
      val bob        = Person("Bob")
      val group      = Group("Trip", List(alice, bob), Nil, Nil)
      val controller = new Controller(App(List(group), None, Some(group)))
      val tui        = new Tui(controller)
      val shares     = List(Share(alice, 20.0), Share(bob, 10.0))
      val expense    = Expense("Dinner", 30.0, Date(1, 1, 2025), alice, shares)
      val msg        = tui.expenseHandler(
        ControllerEvent.AddExpense(AddExpenseResult.Success, expense)
      )
      msg should include("Added expense")
    }

    "handle unknown command" in {
      val controller = new Controller(App(Nil, None, None))
      val tui        = new Tui(controller)
      noException should be thrownBy tui.processInput(":unknown")
    }

    "print help correctly" in {
      val app        = App(Nil, None, None)
      val controller = new Controller(app)
      val tui        = new Tui(controller)

      val outputStream = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outputStream)) {
        tui.printHelp
      }

      val output = outputStream.toString
      TuiKeys.values.foreach { key =>
        output should include(key.key)
      }
    }

    "print full overview correctly" in {
      val alice      = Person("Alice")
      val bob        = Person("Bob")
      val group1     = Group("Trip", List(alice, bob), Nil, Nil)
      val group2     = Group("Party", List(alice), Nil, Nil)
      val app        = App(List(group1, group2), None, None)
      val controller = new Controller(app)
      val tui        = new Tui(controller)

      val output = tui.buildFullOverviewString
      output should include("Trip")
      output should include("Party")
      output should include("Alice")
      output should include("Bob")
      output should include("_" * 40)
    }

    "print available groups correctly" in {
      val group1     = Group("Trip", Nil, Nil, Nil)
      val group2     = Group("Party", Nil, Nil, Nil)
      val app        = App(List(group1, group2), None, None)
      val controller = new Controller(app)
      val tui        = new Tui(controller)

      val output = tui.getAvailableGroupsString
      output should include("Available Groups:")
      output should include("Trip")
      output should include("Party")
      output should include("_" * 40)
    }

    "print message when user is already added to group" in {
      val user       = Person("Alice")
      val group      = Group("Trip", List(user), Nil, Nil)
      val app        = App(List(group), None, Some(group))
      val controller = new Controller(app)
      val tui        = new Tui(controller)

      val msg = tui.addUserToGroupHandler(
        ControllerEvent.AddUserToGroup(
          AddUserToGroupResult.UserAlreadyAdded,
          user
        )
      )

      msg should include("User Alice already added to group Trip")
    }

    "print message when no active group exists" in {
      val user       = Person("Alice")
      val app        = App(Nil, None, None)
      val controller = new Controller(app)
      val tui        = new Tui(controller)

      val msg = tui.addUserToGroupHandler(
        ControllerEvent.AddUserToGroup(
          AddUserToGroupResult.NoActiveGroup,
          user
        )
      )

      msg should include("Cannot add Alice because there is no active group")
    }

    "print message when active group not found for expense" in {
      val expense = Expense(
        "Dinner",
        100,
        Date(1, 1, 2025),
        Person("Alice"),
        Nil
      )
      val app        = App(Nil, None, None)
      val controller = new Controller(app)
      val tui        = new Tui(controller)

      val msg = tui.expenseHandler(
        ControllerEvent.AddExpense(
          AddExpenseResult.ActiveGroupNotFound,
          expense
        )
      )

      msg should include("Please first goto a group")
    }

    "print message when shares sum is wrong" in {
      val expense = Expense(
        "Dinner",
        100,
        Date(1, 1, 2025),
        Person("Alice"),
        Nil
      )
      val app        = App(Nil, None, None)
      val controller = new Controller(app)
      val tui        = new Tui(controller)

      val msg = tui.expenseHandler(
        ControllerEvent.AddExpense(AddExpenseResult.SharesSumWrong, expense)
      )

      msg should include(
        "The sum of the shares do not match with the sum of the expense"
      )
    }

    "print message when share references wrong user" in {
      val expense = Expense(
        "Dinner",
        100,
        Date(1, 1, 2025),
        Person("Alice"),
        Nil
      )
      val app        = App(Nil, None, None)
      val controller = new Controller(app)
      val tui        = new Tui(controller)

      val msg = tui.expenseHandler(
        ControllerEvent.AddExpense(
          AddExpenseResult.SharesPersonNotFound,
          expense
        )
      )

      msg should include("Wrong user in shares")
    }

    "print message when paidBy user not in group" in {
      val expense = Expense(
        "Dinner",
        100,
        Date(1, 1, 2025),
        Person("Alice"),
        Nil
      )
      val app        = App(Nil, None, None)
      val controller = new Controller(app)
      val tui        = new Tui(controller)

      val msg = tui.expenseHandler(
        ControllerEvent.AddExpense(AddExpenseResult.PaidByNotFound, expense)
      )

      msg should include(
        "Please first add Alice to the group before using in expense"
      )
    }
  }

  "gotoGroupHandler" should {

    "print message when group is successfully set" in {
      val group      = Group("Trip", Nil, Nil, Nil)
      val app        = App(List(group), None, Some(group))
      val controller = new Controller(app)
      val tui        = new Tui(controller)

      val msg = tui.gotoGroupHandler(
        ControllerEvent.GotoGroup(GotoGroupResult.Success, group)
      )

      msg should include("Set active group to Trip")
    }

    "print message when group is not found" in {
      val group      = Group("Holiday", Nil, Nil, Nil)
      val app        = App(Nil, None, None)
      val controller = new Controller(app)
      val tui        = new Tui(controller)

      val msg = tui.gotoGroupHandler(
        ControllerEvent.GotoGroup(GotoGroupResult.GroupNotFound, group)
      )

      msg should include("Unable to find group Holiday")
    }
  }

  "Tui.processInput" should {

    "call printHelp when input is help key" in {
      val app        = App(Nil, None, None)
      val controller = new Controller(app)
      val tui        = new Tui(controller)

      val outputStream = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outputStream)) {
        tui.processInput(TuiKeys.help.key)
      }

      val output = outputStream.toString
      output should include("==>")
    }

    "call controller.quit when input is quit key" in {
      val app        = App(Nil, None, None)
      var quitCalled = 0

      val controllerStub =
        new Controller(app) {
          override def quit: Unit = {
            quitCalled += 1
          }
        }

      val tui = new Tui(controllerStub)
      tui.processInput(TuiKeys.quit.key)

      quitCalled shouldBe 1
    }

    "call controller.gotoMainMenu when input is MainMenu key" in {
      val app            = App(Nil, None, None)
      var mainMenuCalled = false

      val controllerStub =
        new Controller(app) {
          override def gotoMainMenu: Unit = mainMenuCalled = true
        }

      val tui = new Tui(controllerStub)
      tui.processInput(TuiKeys.MainMenu.key)

      mainMenuCalled shouldBe true
    }

    "call controller.gotoGroup when input is gotoGroup key" in {
      val app                            = App(Nil, None, None)
      var gotoGroupCalled: Option[Group] = None

      val controllerStub =
        new Controller(app) {
          override def gotoGroup(group: Group): Unit =
            gotoGroupCalled = Some(group)
        }

      val tui       = new Tui(controllerStub)
      val groupName = "TestGroup"
      tui.processInput(s"${TuiKeys.gotoGroup.key} $groupName")

      gotoGroupCalled.map(_.name) shouldBe Some(groupName)
    }
  }

  "Tui.update" should {

    "handle MainMenu event and print available groups" in {
      val app        = App(Nil, None, None)
      val controller = new Controller(app)
      val tui        = new Tui(controller)

      val out = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(out)) {
        tui.update(ControllerEvent.MainMenu)
      }

      out.toString should include("Available Groups")
    }

    "handle Quit event and print Goodbye!" in {
      val app        = App(Nil, None, None)
      val controller = new Controller(app)
      val tui        = new Tui(controller)

      val out = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(out)) {
        tui.update(ControllerEvent.Quit)
      }

      out.toString should include("Goodbye!")
    }

    "handle unhandled event" in {
      val app        = App(Nil, None, None)
      val controller = new Controller(app)
      val tui        = new Tui(controller)

      val dummyEvent = new ObservableEvent {}
      val out        = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(out)) {
        tui.update(dummyEvent)
      }

      out.toString should include("Unhandled event")
    }
  }
