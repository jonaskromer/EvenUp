package de.htwg.swe.evenup

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import java.io.ByteArrayOutputStream
import java.io.PrintStream

import de.htwg.swe.evenup._

class TUISpec extends AnyWordSpec with Matchers:

  def captureOutput(block: => Unit): String =
    val outputStream = new ByteArrayOutputStream()
    val printStream = new PrintStream(outputStream)
    Console.withOut(printStream) {
      block
    }
    outputStream.toString()

  "The quit command should return false to stop the program" in:
    val tui = TUI()
    val result = tui.processInput("quit")
    result shouldBe false

  "The help command should return true and display help text" in:
    val tui = TUI()
    val output = captureOutput {
      tui.processInput("help")
    }
    output should include("Commands:")
    output should include("quit")
    output should include("help")

  "The users command should display message when no users exist" in:
    val tui = TUI()
    val output = captureOutput {
      tui.processInput("users")
    }
    output should include("No users have been created.")

  "The users command should display all users when users exist" in:
    val p_1 = Person("John")
    val p_2 = Person("Peter")
    val state = AppState(Nil, List(p_1, p_2))
    val tui = TUI(state)
    val output = captureOutput {
      tui.processInput("users")
    }
    output should include("Users:")
    output should include("John")
    output should include("Peter")

  "The groups command should display message when no groups exist" in:
    val tui = TUI()
    val output = captureOutput {
      tui.processInput("groups")
    }
    output should include("No groups have been created.")

  "The groups command should display all groups when groups exist" in:
    val p_1 = Person("John")
    val g_1 = Group("Trip", List(p_1), List())
    val g_2 = Group("Party", List(p_1), List())
    val state = AppState(List(g_1, g_2))
    val tui = TUI(state)
    val output = captureOutput {
      tui.processInput("groups")
    }
    output should include("Groups:")
    output should include("Trip")
    output should include("Party")

  "When creating a new user it should be added to the state and display confirmation" in:
    val tui = TUI()
    val output = captureOutput {
      tui.processInput("createuser John")
    }
    tui.state.findUserByName("John") shouldBe Some(Person("John"))
    output should include("The user John has been created.")

  "When creating a user that already exists it should display error message" in:
    val p_1 = Person("John")
    val state = AppState(Nil, List(p_1))
    val tui = TUI(state)
    val output = captureOutput {
      tui.processInput("createuser John")
    }
    output should include("User already exists.")
    tui.state.allUsers.count(_.name == "John") shouldBe 1

  "When creating a new group it should be added to the state and display confirmation" in:
    val tui = TUI()
    val output = captureOutput {
      tui.processInput("creategroup Trip")
    }
    tui.state.findGroupByName("Trip") shouldBe Some(Group("Trip", Nil, Nil))
    output should include("The group Trip has been created.")

  "When creating a group that already exists it should display error message" in:
    val g_1 = Group("Trip", Nil, Nil)
    val state = AppState(List(g_1))
    val tui = TUI(state)
    val output = captureOutput {
      tui.processInput("creategroup Trip")
    }
    output should include("Group already exists.")
    tui.state.allGroups.count(_.name == "Trip") shouldBe 1

  "The showgroup command should return true for an existing group" in:
    val p_1 = Person("John")
    val g_1 = Group("Trip", List(p_1), List())
    val state = AppState(List(g_1))
    val tui = TUI(state)
    val result = tui.processInput("showgroup Trip")
    result shouldBe true

  "The showgroup command should display error for a non-existing group" in:
    val tui = TUI()
    val output = captureOutput {
      tui.processInput("showgroup Holiday")
    }
    output should include("Group Holiday does not exist.")

  "When adding an existing user to an existing group the group should be updated" in:
    val p_1 = Person("John")
    val g_1 = Group("Trip", Nil, Nil)
    val state = AppState(List(g_1), List(p_1))
    val tui = TUI(state)
    tui.processInput("adduser John Trip")
    val updatedGroup = tui.state.findGroupByName("Trip")
    updatedGroup.isDefined shouldBe true
    updatedGroup.get.members should contain(p_1)

  "When adding a non-existing user to a group it should display error message" in:
    val g_1 = Group("Trip", Nil, Nil)
    val state = AppState(List(g_1))
    val tui = TUI(state)
    val output = captureOutput {
      tui.processInput("adduser John Trip")
    }
    output should include("User John does not exist.")
    val group = tui.state.findGroupByName("Trip")
    group.get.members shouldBe Nil

  "When adding a user to a non-existing group it should display error message" in:
    val p_1 = Person("John")
    val state = AppState(Nil, List(p_1))
    val tui = TUI(state)
    val output = captureOutput {
      tui.processInput("adduser John Holiday")
    }
    output should include("Group Holiday does not exist.")

  "An unknown command should display error message and return true" in:
    val tui = TUI()
    val output = captureOutput {
      val result = tui.processInput("unknown command")
      result shouldBe true
    }
    output should include("Unknown command")
    output should include("help")

  "The addexpense command should display not implemented message" in:
    val tui = TUI()
    val output = captureOutput {
      tui.processInput("addexpense Groceries Trip John 25.00 01.01.2000 Peter 50")
    }
    output should include("Not yet implemented.")

  "Empty input should display unknown command message" in:
    val tui = TUI()
    val output = captureOutput {
      tui.processInput("")
    }
    output should include("Unknown command")

  "Input with extra whitespace should be parsed correctly and create user" in:
    val tui = TUI()
    val output = captureOutput {
      tui.processInput("  createuser   John  ")
    }
    tui.state.findUserByName("John") shouldBe Some(Person("John"))
    output should include("The user John has been created.")