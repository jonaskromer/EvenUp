package de.htwg.swe.evenup.view.tui

import de.htwg.swe.evenup.util.{ObservableEvent, Observer}
import de.htwg.swe.evenup.model.Person
import de.htwg.swe.evenup.model.Group
import de.htwg.swe.evenup.model.financial.Share
import de.htwg.swe.evenup.control.*
import de.htwg.swe.evenup.model.financial.debt.NormalDebtStrategy
import de.htwg.swe.evenup.model.state.AppState
import de.htwg.swe.evenup.model.Date

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

  def printHelp(state: AppState): Unit =
    val keys              = TuiKeys.values.filter(_.allowed(state))
    val maxDescriptionLen = keys.map(_.description.length).max
    val maxKeyLen         = keys.map(_.key.length).max

    val helpText = keys
      .map { key =>
        String.format(
          s"%-${maxDescriptionLen}s ==> %-${maxKeyLen}s %s",
          key.description,
          key.key,
          key.usage
        )
      }
      .mkString("\n")

    val decoratedHelp =
      new HeaderFooterDecorator(
        new ColorDecorator(
          new BorderDecorator(
            new TextComponent(helpText),
            "="
          ),
          ConsoleColors.BRIGHT_YELLOW
        ),
        header = Some("Available Commands"),
        footer = Some("Type a command to get started")
      )

    println(decoratedHelp.render)

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

  val addGroupHandler: PartialFunction[EventResponse, String] =
    case EventResponse.AddGroup(AddGroupResult.Success, group)     => s"Added group ${group.name}"
    case EventResponse.AddGroup(AddGroupResult.GroupExists, group) => s"The group ${group.name} already exists"

  val gotoGroupHandler: PartialFunction[EventResponse, String] =
    case EventResponse.GotoGroup(GotoGroupResult.Success, group)           => s"Set active group to ${group.name}"
    case EventResponse.GotoGroup(GotoGroupResult.SuccessEmptyGroup, group) =>
      s"The group ${group.name} is empty. Add some users..."
    case EventResponse.GotoGroup(GotoGroupResult.GroupNotFound, group) => s"Unable to find group ${group.name}"

  val addUserToGroupHandler: PartialFunction[EventResponse, String] =
    case EventResponse.AddUserToGroup(AddUserToGroupResult.Success, user) =>
      s"Added ${user.name} to ${controller.app.active_group.get.name}." // IS THIS VALID OR SHOULD I RETURN ALSO A GROUP???
    case EventResponse.AddUserToGroup(
          AddUserToGroupResult.UserAlreadyAdded,
          user
        ) =>
      s"User ${user.name} already added to group ${controller.app.active_group.get.name}!"

    case EventResponse.AddUserToGroup(
          AddUserToGroupResult.NoActiveGroup,
          user
        ) =>
      s"Cannot add ${user.name} because there is no active group!"

  val expenseHandler: PartialFunction[EventResponse, String] =
    case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.Success, expense) => s"Added expense ${expense}"
    case EventResponse.AddExpenseToGroup(
          AddExpenseToGroupResult.NoActiveGroup,
          expense
        ) =>
      "Please first goto a group."
    case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.SharesSumWrong, expense) =>
      "The sum of the shares do not match with the sum of the expense."
    case EventResponse.AddExpenseToGroup(
          AddExpenseToGroupResult.SharesPersonNotFound,
          expense
        ) =>
      s"Wrong user in shares."
    case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.PaidByNotFound, expense) =>
      s"Please first add ${expense.paid_by} to the group before using in expense."

  val debtHandler: PartialFunction[EventResponse, String] =
    case EventResponse.CalculateDebts(CalculateDebtsResult.Success, debts) =>
      if debts.isEmpty then "No debts to settle. Group is Evend Up!"
      else
        val transactionStrings = debts.map(_.toString).mkString("\n ")
        s"Calculated debts:\n ${transactionStrings}"
    case EventResponse.CalculateDebts(CalculateDebtsResult.NoActiveGroup, debts) =>
      "Currently no active group is set."
    case EventResponse.SetDebtStrategy(SetDebtStrategyResult.Success, strategy) => s"Switched to ${strategy} debt calculation strategy."
    case EventResponse.SetDebtStrategy(SetDebtStrategyResult.NoActiveGroup, strategy) => "No active group. Cannot set calculation strategy."
  val commandHandler: PartialFunction[EventResponse, String] =
    case EventResponse.Quit     => s"Goodbye!"
    case EventResponse.MainMenu =>
      s"Go to a group by using ${TuiKeys.gotoGroup.key} ${TuiKeys.gotoGroup.usage}\n${getAvailableGroupsString}"

  val undoRedoHandler: PartialFunction[EventResponse, String] =
    case EventResponse.Undo(UndoResult.Success, stack_size)    => s"Undo successfull. Remaining stack $stack_size"
    case EventResponse.Undo(UndoResult.EmptyStack, stack_size) => s"Nothing to undo"
    case EventResponse.Redo(RedoResult.Success, stack_size)    => s"Redo successfull. Remaining stack $stack_size"
    case EventResponse.Redo(RedoResult.EmptyStack, stack_size) => s"Nothing to redo"

  val handler: PartialFunction[EventResponse, String] =
    addGroupHandler orElse gotoGroupHandler orElse expenseHandler orElse addUserToGroupHandler orElse debtHandler orElse commandHandler orElse undoRedoHandler

  override def update(event: ObservableEvent): Unit =
    event match
      case e: EventResponse if handler.isDefinedAt(e) =>
        print(
          Seq(
            ".\n.\n",
            handler(e),
            getActiveGroupString,
            ">"
          ).mkString("\n")
        )
      case e: EventResponse.UncoveredFailure => print(s"##ERROR## =${e._1}")
      case _                                 => println(s"Unhandled event...\n${event}")

  def processInput(input: String): Unit =
    val in = input.split(" ").toList
    in.head match
      case TuiKeys.help.key           => printHelp(controller.app.state)
      case TuiKeys.quit.key           => controller.quit
      case TuiKeys.undo.key           => controller.undo()
      case TuiKeys.redo.key           => controller.redo()
      case TuiKeys.newGroup.key       => controller.addGroup(in(1))
      case TuiKeys.addUserToGroup.key =>
        in.drop(1)
          .foreach(user_name => controller.addUserToGroup(user_name))
      case TuiKeys.addExpense.key =>
        controller.addExpenseToGroup(
          in(1),
          in(2),
          in(3).toDouble,
          date = Date(1, 1, 2000), // TODO: fix this
          shares = in.lift(4)
        )
      case TuiKeys.MainMenu.key       => controller.gotoMainMenu
      case TuiKeys.gotoGroup.key      => controller.gotoGroup(in(1))
      case TuiKeys.calculateDebts.key => controller.calculateDebts()
      case TuiKeys.setStrategy.key    => controller.setDebtStrategy(in.drop(1).mkString(" "))
      case _                          =>
        println("This key is not supported... yet :)")
        print(">")
}
