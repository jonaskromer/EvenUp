package de.htwg.swe.evenup.view.tui

import de.htwg.swe.evenup.util.{ObservableEvent, Observer}
import de.htwg.swe.evenup.model.Person
import de.htwg.swe.evenup.model.Group
import de.htwg.swe.evenup.model.Share
import de.htwg.swe.evenup.control.*

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
    val maxKeyLen         = TuiKeys.values.map(_.key.length).max

    for key <- TuiKeys.values do
      println(
        String.format(
          s"%-${maxDescriptionLen}s ==> %-${maxKeyLen}s %s",
          key.description,
          key.key,
          key.usage
        )
      )

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

  println(
    f"Start by adding a group with => ${TuiKeys.newGroup.key} <group name>"
  )

  print(">")

  val addGroupHandler: PartialFunction[ControllerEvent, Unit] =
    case ControllerEvent.AddGroup(AddGroupResult.Success, group) =>
      println(f"Added group ${group.name}")

  val gotoGroupHandler: PartialFunction[ControllerEvent, Unit] =
    case ControllerEvent.GotoGroup(GotoGroupResult.Success, group) =>
      println(s"Set active group to ${group.name}")
    case ControllerEvent.GotoGroup(GotoGroupResult.GroupNotFound, group) =>
      println(s"Unable to find group ${group.name}")

  val addUserToGroupHandler: PartialFunction[ControllerEvent, Unit] =
    case ControllerEvent.AddUserToGroup(AddUserToGroupResult.Success, user) =>
      println(
        f"Added ${user.name} to ${controller.app.active_group.get.name}."
      ) // IS THIS VALID OR SHOULD I RETURN ALSO A GROUP???
    case ControllerEvent.AddUserToGroup(
          AddUserToGroupResult.UserAlreadyAdded,
          user
        ) =>
      println(
        f"User ${user.name} already added to group ${controller.app.active_group.get.name}!"
      )
    case ControllerEvent.AddUserToGroup(
          AddUserToGroupResult.NoActiveGroup,
          user
        ) =>
      println(f"Cannot add ${user.name} because there is no active group!")

  val expenseHandler: PartialFunction[ControllerEvent, Unit] =
    case ControllerEvent.AddExpense(AddExpenseResult.Success, expense) =>
      println(s"Added expense ${expense}")
    case ControllerEvent.AddExpense(
          AddExpenseResult.ActiveGroupNotFound,
          expense
        ) =>
      println("Please first goto a group.")
    case ControllerEvent.AddExpense(AddExpenseResult.SharesSumWrong, expense) =>
      println("The sum of the shares do not match with the sum of the expense.")
    case ControllerEvent.AddExpense(
          AddExpenseResult.SharesPersonNotFound,
          expense
        ) =>
      println(s"Wrong user in shares.")
    case ControllerEvent.AddExpense(AddExpenseResult.PaidByNotFound, expense) =>
      println(
        s"Please first add ${expense.paid_by} to the group before using in expense."
      )

  val handler: PartialFunction[ControllerEvent, Unit] =
    addGroupHandler orElse gotoGroupHandler orElse expenseHandler orElse addUserToGroupHandler

  override def update(event: ObservableEvent): Unit =
    event match
      case e: ControllerEvent if handler.isDefinedAt(e) =>
        handler(e)
        printActiveGroup
      case ControllerEvent.MainMenu => printFullOverview
      case ControllerEvent.Quit     => println("GoodBye!")
      case _                        => println("Unhandled event...")

  def processInput(input: String): Unit =
    val in = input.split(" ").toList
    in.head match
      case TuiKeys.help.key     => printHelp
      case TuiKeys.quit.key     => controller.quit
      case TuiKeys.newGroup.key =>
        controller.addGroup(Group(in.drop(1).mkString(" "), Nil, Nil, Nil))
      case TuiKeys.addUserToGroup.key =>
        in.drop(1)
          .foreach(person_name =>
            controller.addUserToGroup(Person(person_name))
          )
      case TuiKeys.addExpense.key =>
        controller.addExpenseToGroup(
          in(1),
          Person(in(2)),
          in(3).toDouble,
          shares = parser.parseShares(in.lift(4))
        )
      case TuiKeys.MainMenu.key  => controller.gotoMainMenu
      case TuiKeys.gotoGroup.key =>
        controller.gotoGroup(Group(in.drop(1).mkString(" "), Nil, Nil, Nil))
      case _ => println("This key is not supported... yet :)")

}
