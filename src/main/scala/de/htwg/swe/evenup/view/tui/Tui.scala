package de.htwg.swe.evenup.view.tui

import de.htwg.swe.evenup.control.{Controller, ControllerEvent}
import de.htwg.swe.evenup.util.{ObservableEvent, Observer}

import scala.io.StdIn.readLine
import de.htwg.swe.evenup.model.Person
import de.htwg.swe.evenup.model.Group

enum PromptState {
  case None
  case GetOrCreateGroup
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
    val maxDescriptionLen = TuiKeys.values.map(_.description.length).max
    val maxKeyLen  = TuiKeys.values.map(_.key.length).max

    for key <- TuiKeys.values do
      println(String.format(s"%-${maxDescriptionLen}s ==> %-${maxKeyLen}s %s",
        key.description, key.key, key.usage))

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
    println(controller.app.findGroup(controller.app.active_group.get).get)
    println("_" * 40)

  println("Welcome to EvenUp!")

  /*
  val user = readLine("Please enter your name: ")
  println(f"Logged in as $user.")
   */
  println(
    f"Start by adding a group with => ${TuiKeys.newGroup.key} <group name>"
  )

  override def update(event: ObservableEvent): Unit =
    event match
      case ControllerEvent.Quit =>
        println("Goodbye!")
        System.exit(0)

      case ControllerEvent.MainMenu =>
        promptState = PromptState.MainMenu
        printAvailableGroups
        prompter.promptMainMenu

      case ControllerEvent.GroupInitialized =>
        promptState = PromptState.InGroup
        printActiveGroup
        prompter.promptInitGroup

      case ControllerEvent.InGroup =>
        promptState = PromptState.InGroup
        printActiveGroup
        prompter.promptInGroup

      case ControllerEvent.PersonAddedToGroup =>
        // no real use
        promptState = PromptState.InGroup
        printFullOverview
        prompter.promptInGroup

      case ControllerEvent.ExpenseAddedToGroup =>
        promptState = PromptState.InGroup
        printFullOverview
        prompter.promptInGroup

  def processInput(input: String): Unit =
    val in = input.split(" ").toList
    in.head match
      case TuiKeys.help.key     => printHelp
      case TuiKeys.quit.key     => controller.quit
      case TuiKeys.newGroup.key => controller.addGroup(Group(in.drop(1).mkString(" "), Nil, Nil))
      case TuiKeys.addUserToGroup.key => in.drop(1).foreach(person_name => controller.addPersonToGroup(Person(person_name)))
      case TuiKeys.addExpense.key => controller.addExpenseToGroup(in(1), in(2), in(3).toDouble)
      case TuiKeys.MainMenu.key  => controller.gotoMainMenu
      case TuiKeys.gotoGroup.key => controller.gotoGroup(Group(in.drop(1).mkString(" "), Nil, Nil))
      case _ => println("This key is not supported... yet :)")

}
