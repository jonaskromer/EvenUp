package de.htwg.swe.evenup.control

import de.htwg.swe.evenup.model.App
import de.htwg.swe.evenup.model.Group
import de.htwg.swe.evenup.model.financial.debt.NormalDebtStrategy
import de.htwg.swe.evenup.model.Person
import de.htwg.swe.evenup.util.UndoManager
import de.htwg.swe.evenup.model.Date
import de.htwg.swe.evenup.model.financial.Share
import de.htwg.swe.evenup.model.financial.Expense
import de.htwg.swe.evenup.model.financial.ExpenseBuilder
import de.htwg.swe.evenup.model.financial.debt.SimplifiedDebtStrategy

trait HandlerTemplate:
  val next: Option[HandlerTemplate]
  def check(args: Map[String, Any], app: App): EventResponse

  def checkOrDelegate(args: Map[String, Any], app: App): EventResponse =
    check(args, app) match
      case EventResponse.NextHandler =>
        next match
          case Some(handler) => handler.checkOrDelegate(args, app)
          case None          => EventResponse.Success
      case result => result

class ArgsHandler:
  val calculateDebtsHandler = CalculateDebtsHandler(None)
  val setDebtStrategyHandler = SetDebtStrategyHandler(Option(calculateDebtsHandler))
  val addExpenseToGroupHandler = AddExpenseToGroupHandler(Option(setDebtStrategyHandler))
  val undoRedoHandler       = UndoRedoHandler(Option(addExpenseToGroupHandler))
  val addUserToGroupHandler = AddUserToGroupHandler(Option(undoRedoHandler))
  val gotoGroupHandler      = GotoGroupHandler(Option(addUserToGroupHandler))
  val addGroupHandler       = AddGroupHandler(Option(gotoGroupHandler))

  def checkOrDelegate(args: Map[String, Any], app: App): EventResponse = addGroupHandler.checkOrDelegate(
    args,
    app
  )

case class AddGroupHandler(next: Option[HandlerTemplate]) extends HandlerTemplate:

  def check(args: Map[String, Any], app: App): EventResponse =
    args.get("operation") match
      case Some("addGroup") =>
        args.get("group_name") match
          case Some(group_name: String) =>
            app.getGroup(group_name) match
              case Some(existing_group) => (EventResponse.AddGroup(AddGroupResult.GroupExists, existing_group))
              case None                 => (
                EventResponse.AddGroup(AddGroupResult.Success, Group(group_name, Nil, Nil, Nil, NormalDebtStrategy()))
              )
          case _ => EventResponse.UncoveredFailure("AddGroupHandler")
      case _ => EventResponse.NextHandler

case class GotoGroupHandler(next: Option[HandlerTemplate]) extends HandlerTemplate:

  def check(args: Map[String, Any], app: App): EventResponse =
    args.get("operation") match
      case Some("gotoGroup") =>
        args.get("group_name") match
          case Some(group_name: String) =>
            app.getGroup(group_name) match
              case Some(group) =>
                if group.members.length == 1 then EventResponse.GotoGroup(GotoGroupResult.SuccessEmptyGroup, group)
                else EventResponse.GotoGroup(GotoGroupResult.Success, group)
              case None =>
                EventResponse.GotoGroup(
                  GotoGroupResult.GroupNotFound,
                  Group(group_name, Nil, Nil, Nil, NormalDebtStrategy())
                )
          case _ => EventResponse.UncoveredFailure("GotoGroupHandler")
      case _ => EventResponse.NextHandler

case class AddUserToGroupHandler(next: Option[HandlerTemplate]) extends HandlerTemplate:

  def check(args: Map[String, Any], app: App): EventResponse =
    args.get("operation") match
      case Some("addUserToGroup") =>
        args.get("user_name") match
          case Some(user_name: String) =>
            app.active_group match
              case Some(group) =>
                val user_already_in_group = group.containsUser(user_name)
                if user_already_in_group then
                  EventResponse.AddUserToGroup(AddUserToGroupResult.UserAlreadyAdded, Person(user_name))
                else EventResponse.AddUserToGroup(AddUserToGroupResult.Success, Person(user_name))
              case None => EventResponse.AddUserToGroup(AddUserToGroupResult.NoActiveGroup, Person(user_name))
          case _ => EventResponse.UncoveredFailure("AddUserToGroupHandler")
      case _ => EventResponse.NextHandler

case class UndoRedoHandler(next: Option[HandlerTemplate]) extends HandlerTemplate:

  def check(args: Map[String, Any], app: App): EventResponse =
    args.get("operation") match
      case Some("undo") =>
        args.get("undo_stack_size") match
          case Some(stack_size: Int) =>
            if stack_size == 0 then EventResponse.Undo(UndoResult.EmptyStack, 0)
            else EventResponse.Undo(UndoResult.Success, stack_size - 1)
          case _ => EventResponse.UncoveredFailure("undoHandler")
      case Some("redo") =>
        args.get("redo_stack_size") match
          case Some(stack_size: Int) =>
            if stack_size == 0 then EventResponse.Redo(RedoResult.EmptyStack, 0)
            else EventResponse.Redo(RedoResult.Success, stack_size - 1)
          case _ => EventResponse.UncoveredFailure("redoHandler")
      case _ => EventResponse.NextHandler

case class AddExpenseToGroupHandler(next: Option[HandlerTemplate]) extends HandlerTemplate:

  def check(args: Map[String, Any], app: App): EventResponse =
    args.get("operation") match
      case Some("addExpenseToGroup") =>
        val parsed =
          for
            expense_name <- args.get("expense_name").collect { case s: String => s }
            paid_by      <- args.get("paid_by").collect { case s: String => s }
            amount       <- args.get("amount").collect { case d: Double => d }
            date         <- Some(args.get("date").collect { case d: Date => d }.getOrElse(Date(1, 1, 2000)))
            shares       <- Some(args.get("shares").flatMap(_.asInstanceOf[Option[String]]))
          yield (expense_name, paid_by, amount, date, shares)

        val failed_expense = ExpenseBuilder()
          .withName("failed_expense")
          .withAmount(0)
          .onDate(Date(1, 1, 2000))
          .paidBy(Person(""))
          .withShares(List(Share(Person(""), 0)))

        parsed match
          case Some((expense_name, paid_by, amount, date, shares)) =>
            app.active_group match
              case None =>
                return EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.NoActiveGroup, failed_expense.build())

              case Some(active_group) =>
                val valid_paid_by = active_group.containsUser(paid_by)
                if !valid_paid_by then
                  return EventResponse.AddExpenseToGroup(
                    AddExpenseToGroupResult.PaidByNotFound,
                    failed_expense.paidBy(Person(paid_by)).build()
                  )

                if amount <= 0 then
                  return EventResponse.AddExpenseToGroup(
                    AddExpenseToGroupResult.InvalidAmount,
                    failed_expense.withAmount(amount).build()
                  )

                shares match
                  case Some(shares_string) =>
                    val shares_list = shares_string.split("_").toList.map { s =>
                      s.split(":") match
                        case Array(name, amount) => Share(Person(name), amount.toDouble)
                        case _                   => throw new IllegalArgumentException(s"Invalid share: $s")
                    }
                    // TODO: Validate that custom share is valid. All users in group. Sum matches
                    if shares_list.exists(share => !active_group.members.contains(share.person)) then
                      EventResponse.AddExpenseToGroup(
                        AddExpenseToGroupResult.SharesPersonNotFound,
                        failed_expense.build()
                      )
                    else
                      EventResponse.AddExpenseToGroup(
                        AddExpenseToGroupResult.Success,
                        ExpenseBuilder()
                          .withName(expense_name)
                          .withAmount(amount)
                          .onDate(date)
                          .paidBy(Person(paid_by))
                          .withShares(shares_list)
                          .build()
                      )

                  case None =>
                    val n     = active_group.members.length
                    val total = BigDecimal(amount).setScale(
                      2,
                      BigDecimal.RoundingMode.HALF_UP
                    )
                    val baseShare = (total / n).setScale(
                      2,
                      BigDecimal.RoundingMode.DOWN
                    )
                    val initialShares  = active_group.members.map(_ -> baseShare).toMap
                    val totalRounded   = initialShares.values.sum
                    val remainder      = total - totalRounded
                    val adjustedShares =
                      if remainder == 0 then initialShares
                      else
                        val firstMember = active_group.members.head
                        initialShares.updated(
                          firstMember,
                          initialShares(firstMember) + remainder
                        )
                    val evenShare: List[Share] =
                      adjustedShares.map { case (person, share) =>
                        Share(
                          person,
                          share.setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
                        )
                      }.toList

                    EventResponse.AddExpenseToGroup(
                      AddExpenseToGroupResult.Success,
                      ExpenseBuilder()
                        .withName(expense_name)
                        .withAmount(amount)
                        .onDate(date)
                        .paidBy(Person(paid_by))
                        .withShares(evenShare)
                        .build()
                    )

          case _ => return EventResponse.UncoveredFailure("AddExpenseToGroupHandler")

      case _ => EventResponse.NextHandler

case class SetDebtStrategyHandler(next: Option[HandlerTemplate]) extends HandlerTemplate:
  def check(args: Map[String, Any], app: App): EventResponse = 
    args.get("operation") match
      case Some("setDebtStrategy") =>
        app.active_group match
          case Some(group) =>
            args.get("strategy") match
              case Some("normal") =>
                EventResponse.SetDebtStrategy(SetDebtStrategyResult.Success, NormalDebtStrategy())
              case Some("simplified") => 
                EventResponse.SetDebtStrategy(SetDebtStrategyResult.Success, SimplifiedDebtStrategy())
              case _ => return EventResponse.UncoveredFailure("SetDebtStrategyHandler")
          case None => 
            EventResponse.SetDebtStrategy(SetDebtStrategyResult.NoActiveGroup, NormalDebtStrategy())
      case _ => EventResponse.NextHandler

case class CalculateDebtsHandler(next: Option[HandlerTemplate]) extends HandlerTemplate:
  def check(args: Map[String, Any], app: App): EventResponse = 
    args.get("operation") match
      case Some("calculateDebts") =>
        app.active_group match
          case Some(group) =>
            EventResponse.CalculateDebts(CalculateDebtsResult.Success, group.calculateDebt())
          case None => 
            EventResponse.CalculateDebts(CalculateDebtsResult.NoActiveGroup, Nil)
      case _ => EventResponse.NextHandler
