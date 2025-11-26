package de.htwg.swe.evenup.view.tui

import de.htwg.swe.evenup.util.{ObservableEvent, Observer}
import de.htwg.swe.evenup.model.Person
import de.htwg.swe.evenup.model.Group
import de.htwg.swe.evenup.model.Share
import de.htwg.swe.evenup.control.*
import de.htwg.swe.evenup.model.debt.NormalDebtStrategy

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

  def spacer = "_" * 40

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

  def buildFullOverviewString: String = Seq(
    spacer,
    controller.app.allGroups.map(_.toString).mkString("\n"),
    spacer
  ).mkString("\n")

  def getAvailableGroupsString: String = Seq(
    spacer,
    "Available Groups:",
    spacer,
    controller.app.allGroups.map(_.name).mkString("\n"),
    spacer
  ).mkString("\n")

  def getActiveGroupString: String =
    controller.app.active_group match
      case Some(group) =>
        Seq(
          spacer,
          group.toString,
          spacer
        ).mkString("\n")
      case None => ""

  println("Welcome to EvenUp!")

  println(
    s"Start by adding a group with => ${TuiKeys.newGroup.key} <group name>"
  )

  print(">")

  val addGroupHandler: PartialFunction[ControllerEvent, String] =
    case ControllerEvent.AddGroup(AddGroupResult.Success, group) => s"Added group ${group.name}"

  val gotoGroupHandler: PartialFunction[ControllerEvent, String] =
    case ControllerEvent.GotoGroup(GotoGroupResult.Success, group)       => s"Set active group to ${group.name}"
    case ControllerEvent.GotoGroup(GotoGroupResult.GroupNotFound, group) => s"Unable to find group ${group.name}"

  val addUserToGroupHandler: PartialFunction[ControllerEvent, String] =
    case ControllerEvent.AddUserToGroup(AddUserToGroupResult.Success, user) =>
      s"Added ${user.name} to ${controller.app.active_group.get.name}." // IS THIS VALID OR SHOULD I RETURN ALSO A GROUP???
    case ControllerEvent.AddUserToGroup(
          AddUserToGroupResult.UserAlreadyAdded,
          user
        ) =>
      s"User ${user.name} already added to group ${controller.app.active_group.get.name}!"

    case ControllerEvent.AddUserToGroup(
          AddUserToGroupResult.NoActiveGroup,
          user
        ) =>
      s"Cannot add ${user.name} because there is no active group!"

  val expenseHandler: PartialFunction[ControllerEvent, String] =
    case ControllerEvent.AddExpense(AddExpenseResult.Success, expense) => s"Added expense ${expense}"
    case ControllerEvent.AddExpense(
          AddExpenseResult.ActiveGroupNotFound,
          expense
        ) =>
      "Please first goto a group."
    case ControllerEvent.AddExpense(AddExpenseResult.SharesSumWrong, expense) =>
      "The sum of the shares do not match with the sum of the expense."
    case ControllerEvent.AddExpense(
          AddExpenseResult.SharesPersonNotFound,
          expense
        ) =>
      s"Wrong user in shares."
    case ControllerEvent.AddExpense(AddExpenseResult.PaidByNotFound, expense) =>
      s"Please first add ${expense.paid_by} to the group before using in expense."

  val debtHandler: PartialFunction[ControllerEvent, String] =
    case ControllerEvent.CalculateDebts(transactions) =>
      if transactions.isEmpty then
        "No debts to settle. Group is Evend Up!"
      else
        val transactionStrings = transactions.map(_.toString).mkString("\n ")
        s"Calculated debts:\n ${transactionStrings}"

    case ControllerEvent.SwitchStrategy(strategyName) =>
      s"Switched to ${strategyName} debt calculation strategy."

  val commandHandler: PartialFunction[ControllerEvent, String] =
    case ControllerEvent.Quit     => s"Goodbye!"
    case ControllerEvent.MainMenu =>
      s"Go to a group by using ${TuiKeys.gotoGroup.key} ${TuiKeys.gotoGroup.usage}\n${getAvailableGroupsString}"

  val handler: PartialFunction[ControllerEvent, String] =
    addGroupHandler orElse gotoGroupHandler orElse expenseHandler orElse addUserToGroupHandler orElse debtHandler orElse commandHandler

  override def update(event: ObservableEvent): Unit =
    event match
      case e: ControllerEvent if handler.isDefinedAt(e) =>
        print(
          Seq(
            ".\n.\n",
            handler(e),
            getActiveGroupString,
            ">"
          ).mkString("\n")
        )
      case _ => println("Unhandled event...")

  def processInput(input: String): Unit =
    val in = input.split(" ").toList
    in.head match
      case TuiKeys.help.key           => printHelp
      case TuiKeys.quit.key           => controller.quit
      case TuiKeys.newGroup.key       => controller.addGroup(Group(in.drop(1).mkString(" "), Nil, Nil, Nil, NormalDebtStrategy()))
      case TuiKeys.addUserToGroup.key =>
        in.drop(1)
          .foreach(person_name => controller.addUserToGroup(Person(person_name)))
      case TuiKeys.addExpense.key =>
        controller.addExpenseToGroup(
          in(1),
          Person(in(2)),
          in(3).toDouble,
          shares = parser.parseShares(in.lift(4))
        )
      case TuiKeys.MainMenu.key  => controller.gotoMainMenu
      case TuiKeys.gotoGroup.key => controller.gotoGroup(Group(in.drop(1).mkString(" "), Nil, Nil, Nil, NormalDebtStrategy()))
      case TuiKeys.calculateDebts.key => controller.calculateDebts()
      case TuiKeys.setStrategy.key => controller.setDebtStrategy(in(1))
      case _                     =>
        println("This key is not supported... yet :)")
        print(">")

}
