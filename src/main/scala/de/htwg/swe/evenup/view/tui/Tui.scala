package de.htwg.swe.evenup.view.tui

import de.htwg.swe.evenup.control.{Controller, ControllerEvent}
import de.htwg.swe.evenup.util.{ObservableEvent, Observer}

import scala.io.StdIn.readLine

enum PromptState {
  case None
  case GetOrCreateGroup
  case SetupGroupName
  case SetupGroupUsers
  case InGroup
  case SetupExpense
  case MainMenu
}

class Tui(controller: Controller) extends Observer {
  var promptState: PromptState = PromptState.None

  val parser   = new Parser
  val prompter = new Prompter

  controller.add(this)

  def printHelp: Unit =
    for key <- TuiKeys.values do println(s"${key.productPrefix} ==> ${key.key}")

  def printFullOverview: Unit =
    println("_" * 40)
    controller.app.allGroups.foreach(group => println(group))
    println("_" * 40)

  def printAvailableGroups: Unit =
    println("_" * 40)
    println("The following groups are available:")
    controller.app.groups.foreach(group => println(group.name))
    println("_" * 40)

  def printActiveGroup: Unit =
    println("_" * 40)
    println(controller.app.findGroupByName(controller.app.active_group.get).get)
    println("_" * 40)

  println("Welcome to EvenUp!")
  val user = readLine("Please enter your name: ")
  println(f"Logged in as $user.")

  println(
    f"Start by adding a group with => ${TuiKeys.newGroup.key} <group name>"
  )

  override def update(event: ObservableEvent): Unit =
    event match
      case ControllerEvent.Quit =>
        println("Goodbye!")
        System.exit(0)

      case ControllerEvent.NewGroupCreated =>
        promptState = PromptState.SetupGroupName
        printFullOverview
        prompter.promptAddUser

      case ControllerEvent.PersonAddedToGroup =>
        promptState = PromptState.SetupGroupUsers
        printFullOverview
        prompter.promptAddUserOrContinue

      case ControllerEvent.GroupCreationFinished =>
        promptState = PromptState.InGroup
        printActiveGroup
        prompter.promptInGroup

      case ControllerEvent.MainMenu =>
        promptState = PromptState.MainMenu
        printAvailableGroups
        prompter.promptMainMenu

  def processInput(input: String): Unit =
    val in = input.split(" ").toList
    in.head match
      case TuiKeys.help.key    => printHelp
      case TuiKeys.quit.key    => controller.quit
      case TuiKeys.newGroup.key => controller.addGroup(in.drop(1).mkString(" "))
      case TuiKeys.addUserToGroup.key =>
        in.drop(2).foreach(person => controller.addPersonToGroup(in(1), person))
      case TuiKeys.MainMenu.key => controller.gotoMainMenu
      case TuiKeys.proceed.key =>
        promptState match
          case PromptState.SetupGroupName =>
            println(
              f"Please first add a user to this group by using => ${TuiKeys.addUserToGroup.key} <user name>"
            )
          case PromptState.SetupGroupUsers =>
            controller.finishGroupSetup
            // TODO: Controller go into group / set active group
          case _ => println("Not supported yet.")
      case _ => println("This key is not supported!")

}
