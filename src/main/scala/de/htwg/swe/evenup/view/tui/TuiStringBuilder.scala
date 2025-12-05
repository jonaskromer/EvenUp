package de.htwg.swe.evenup.view.tui

import de.htwg.swe.evenup.control.EventResponse
import de.htwg.swe.evenup.control.EventResponse.*
import de.htwg.swe.evenup.control.AddGroupResult
import de.htwg.swe.evenup.control.GotoGroupResult
import de.htwg.swe.evenup.control.AddUserToGroupResult
import de.htwg.swe.evenup.control.AddExpenseToGroupResult
import de.htwg.swe.evenup.control.UndoResult
import de.htwg.swe.evenup.control.RedoResult
import de.htwg.swe.evenup.control.CalculateDebtsResult
import de.htwg.swe.evenup.control.SetDebtStrategyResult
import de.htwg.swe.evenup.model.Group
import de.htwg.swe.evenup.control.Controller

class TuiStringBuilder(controller: Controller):

  def getActiveGroupString: String =
    controller.app.active_group match
      case Some(group) =>
        new ColorDecorator(
          new BorderDecorator(
            new TextComponent(group.toString()),
            "="
          ),
          ConsoleColors.BRIGHT_CYAN
        ).render
      case None => ""

  val addGroupHandler: PartialFunction[EventResponse, String] =
    case EventResponse.AddGroup(AddGroupResult.Success, group)     => s"Added group ${group.name}"
    case EventResponse.AddGroup(AddGroupResult.GroupExists, group) => s"The group ${group.name} already exists"

  val gotoGroupHandler: PartialFunction[EventResponse, String] =
    case EventResponse.GotoGroup(GotoGroupResult.Success, group) =>
      s"Set active group to ${group.name}\n${getActiveGroupString}"
    case EventResponse.GotoGroup(GotoGroupResult.SuccessEmptyGroup, group) =>
      s"The group ${group.name} is empty. Add some users..."
    case EventResponse.GotoGroup(GotoGroupResult.GroupNotFound, group) => s"Unable to find group ${group.name}"

  val addUserToGroupHandler: PartialFunction[EventResponse, String] =
    case EventResponse.AddUserToGroup(AddUserToGroupResult.Success, user, group) =>
      s"Added ${user.name} to ${group.name}\n${getActiveGroupString}"
    case EventResponse.AddUserToGroup(
          AddUserToGroupResult.UserAlreadyAdded,
          user,
          group
        ) =>
      s"User ${user.name} already added to group ${group.name}!"

    case EventResponse.AddUserToGroup(
          AddUserToGroupResult.NoActiveGroup,
          user,
          group
        ) =>
      s"Cannot add ${user.name} because there is no active group!"

  val expenseHandler: PartialFunction[EventResponse, String] =
    case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.Success, expense) =>
      s"Added expense ${expense}\n${getActiveGroupString}"
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
    case EventResponse.CalculateDebts(CalculateDebtsResult.NoActiveGroup, debts) => "Currently no active group is set."
    case EventResponse.SetDebtStrategy(SetDebtStrategyResult.Success, strategy)  =>
      s"Switched to ${strategy} debt calculation strategy."
    case EventResponse.SetDebtStrategy(SetDebtStrategyResult.NoActiveGroup, strategy) =>
      "No active group. Cannot set calculation strategy."

  val commandHandler: PartialFunction[EventResponse, String] =
    case EventResponse.Quit     => s"Goodbye!"
    case EventResponse.MainMenu =>
      s"Go to a group by using ${TuiKeys.gotoGroup.key} ${TuiKeys.gotoGroup.usage}\n${getAvailableGroupsString}"

  val undoRedoHandler: PartialFunction[EventResponse, String] =
    case EventResponse.Undo(UndoResult.Success, stack_size) =>
      s"Undo successfull. Remaining stack $stack_size\n${getActiveGroupString}"
    case EventResponse.Undo(UndoResult.EmptyStack, stack_size) => s"Nothing to undo"
    case EventResponse.Redo(RedoResult.Success, stack_size)    =>
      s"Redo successfull. Remaining stack $stack_size\n${getActiveGroupString}"
    case EventResponse.Redo(RedoResult.EmptyStack, stack_size) => s"Nothing to redo"

  val handler: PartialFunction[EventResponse, String] =
    addGroupHandler orElse gotoGroupHandler orElse expenseHandler orElse addUserToGroupHandler orElse debtHandler orElse commandHandler orElse undoRedoHandler

  def isDefined(event: EventResponse): Boolean = handler.isDefinedAt(event)

  def handle(event: EventResponse): String = handler(event)

  def getAvailableGroupsString: String = Seq(
    spacer,
    "Available Groups:",
    spacer,
    controller.app.allGroups.map(_.name).mkString("\n"),
    spacer
  ).mkString("\n")

  def spacer = "_" * 40
