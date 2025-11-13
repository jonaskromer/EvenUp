package de.htwg.swe.evenup.view.tui

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import java.io.{ByteArrayOutputStream, PrintStream}

class PrompterSpec extends AnyWordSpec with Matchers:

  "Prompter" should {

    "prompt for new group correctly" in:
      val outStream = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outStream)):
        val prompter = new Prompter
        prompter.promptNewGroup

      val output = outStream.toString
      output should include (TuiKeys.newGroup.key)
      output should include (TuiKeys.newGroup.usage)
      output should include (">")

    "prompt to add a user correctly" in:
      val outStream = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outStream)):
        val prompter = new Prompter
        prompter.promptAddUser

      val output = outStream.toString
      output should include (TuiKeys.addUserToGroup.key)
      output should include (TuiKeys.addUserToGroup.usage)
      output should include (">")

    "prompt for adding expense in a group correctly" in:
      val outStream = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outStream)):
        val prompter = new Prompter
        prompter.promptInGroup

      val output = outStream.toString
      output should include (TuiKeys.addExpense.key)
      output should include (TuiKeys.addExpense.usage)
      output should include (">")

    "prompt for main menu correctly" in:
      val outStream = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outStream)):
        val prompter = new Prompter
        prompter.promptMainMenu

      val output = outStream.toString
      output should include (TuiKeys.gotoGroup.key)
      output should include (TuiKeys.gotoGroup.usage)
      output should include (">")

    "prompt for initialized group correctly" in:
      val outStream = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outStream)):
        val prompter = new Prompter
        prompter.promptInitGroup

      val output = outStream.toString
      output should include ("This group is only initialized.")
      output should include (TuiKeys.addUserToGroup.key)
      output should include (TuiKeys.addExpense.key)
      output should include (">")
  }
