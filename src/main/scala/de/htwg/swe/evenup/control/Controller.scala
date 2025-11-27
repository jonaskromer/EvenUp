package de.htwg.swe.evenup.control

import de.htwg.swe.evenup.model.App
import de.htwg.swe.evenup.util.{Observable, ObservableEvent}
import de.htwg.swe.evenup.model.Group
import de.htwg.swe.evenup.model.Person
import de.htwg.swe.evenup.model.financial.Expense
import de.htwg.swe.evenup.model.Date
import de.htwg.swe.evenup.model.Share
import de.htwg.swe.evenup.model.financial.{Expense, Transaction, ExpenseBuilder}
import de.htwg.swe.evenup.model.financial.debt.{NormalDebtStrategy, SimplifiedDebtStrategy, Debt}
import de.htwg.swe.evenup.model.state.*

enum ControllerEvent extends ObservableEvent:
  case Quit
  case MainMenu
  case AddGroup(result: AddGroupResult, group: Group)
  case GotoGroup(result: GotoGroupResult, group: Group)
  case AddUserToGroup(result: AddUserToGroupResult, user: Person)
  case AddExpense(result: AddExpenseResult, expense: Expense)
  case CalculateDebts(debts: List[Debt])
  case SwitchStrategy(strategyName: String)

enum AddGroupResult:
  case Success

enum GotoGroupResult:
  case Success
  case SuccessEmptyGroup
  case GroupNotFound

enum AddUserToGroupResult:
  case Success
  case UserAlreadyAdded
  case NoActiveGroup

enum AddExpenseResult:
  case Success
  case ActiveGroupNotFound
  case SharesSumWrong
  case SharesPersonNotFound
  case PaidByNotFound

class Controller(var app: App) extends Observable {

  def quit: Unit =
    // TODO: save state
    notifyObservers(ControllerEvent.Quit)
    System.exit(0)

  def allGroups: List[Group] = app.groups

  def allUsers: List[Person] = app.groups.flatMap(_.members).distinct

  def gotoMainMenu: Unit =
    app = app.updateActiveGroup(None)
    app = app.updateAppState(MainMenuState())
    notifyObservers(ControllerEvent.MainMenu)

  def gotoGroup(group: Group): Unit =
    app.findGroup(group) match
      case Some(group) =>
        app = app.updateActiveGroup(Some(group))
        if group.members.length == 1 then
          app = app.updateAppState(InEmptyGroupState())
          notifyObservers(
            ControllerEvent.GotoGroup(GotoGroupResult.SuccessEmptyGroup, group)
          )
        else
          app = app.updateAppState(InGroupState())
          notifyObservers(
            ControllerEvent.GotoGroup(GotoGroupResult.Success, group)
          )
      case None =>
        notifyObservers(
          ControllerEvent.GotoGroup(GotoGroupResult.GroupNotFound, group)
        )

  def addGroup(group: Group): Unit =
    app = app.addGroup(group)
    app = app.updateActiveGroup(Some(group))
    notifyObservers(ControllerEvent.AddGroup(AddGroupResult.Success, group))

  def addUserToGroup(user: Person): Unit =
    app.active_group match
      case Some(group) =>
        val already_added_user = group.members.contains(user)
        if already_added_user then
          notifyObservers(
            ControllerEvent.AddUserToGroup(
              AddUserToGroupResult.UserAlreadyAdded,
              user
            )
          )
          return
        val updatedGroup = app
          .findGroup(group)
          .get
          .addMember(user)
        app = app
          .updateGroup(updatedGroup)
          .updateActiveGroup(Some(updatedGroup))
        notifyObservers(
          ControllerEvent.AddUserToGroup(AddUserToGroupResult.Success, user)
        )
      case None =>
        notifyObservers(
          ControllerEvent.AddUserToGroup(
            AddUserToGroupResult.NoActiveGroup,
            user
          )
        )

  def addExpenseToGroup(
    expense_name: String,
    paid_by: Person,
    amount: Double,
    date: Date = Date(1, 1, 2000),
    shares: Option[List[Share]] = None
  ): Unit =
    app.active_group match
      case Some(active_group) =>
        val group         = app.findGroup(active_group).get
        val valid_paid_by = group.members.contains(paid_by)
        if !valid_paid_by then
          notifyObservers(
            ControllerEvent.AddExpense(
              AddExpenseResult.PaidByNotFound,
              Expense(
                "",
                0,
                Date(0, 0, 1000),
                Person(""),
                List(Share(Person(""), 0))
              )
            )
          )
          return
        shares match
          case Some(share) =>
            val newExpense = ExpenseBuilder()
              .withName(expense_name)
              .withAmount(amount)
              .onDate(date)
              .paidBy(paid_by)
              .withShares(share)
              .build()
            val updatedGroup = group.addExpense(newExpense)
            app = app.updateGroup(updatedGroup)
            notifyObservers(
              ControllerEvent.AddExpense(AddExpenseResult.Success, newExpense)
            )
          case None =>
            val n     = group.members.length
            val total = BigDecimal(amount).setScale(
              2,
              BigDecimal.RoundingMode.HALF_UP
            )
            val baseShare = (total / n).setScale(
              2,
              BigDecimal.RoundingMode.DOWN
            )
            val initialShares  = group.members.map(_ -> baseShare).toMap
            val totalRounded   = initialShares.values.sum
            val remainder      = total - totalRounded
            val adjustedShares =
              if remainder == 0 then initialShares
              else
                val firstMember = group.members.head
                initialShares.updated(
                  firstMember,
                  initialShares(firstMember) + remainder
                )
            val evenShare =
              adjustedShares.map { case (person, share) =>
                Share(
                  person,
                  share.setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
                )
              }.toList
            val newExpense = ExpenseBuilder()
              .withName(expense_name)
              .withAmount(amount)
              .onDate(date)
              .paidBy(paid_by)
              .withShares(evenShare)
              .build()
            val updatedGroup = group.addExpense(newExpense)
            app = app.updateGroup(updatedGroup)
            notifyObservers(
              ControllerEvent.AddExpense(AddExpenseResult.Success, newExpense)
            )
      case None => println("No active group.")
  /*
  def newTransaction(
    amount: Double,
    to: Person,
    from: Person = app.active_user.get,
    date: Date = Date(1, 1, 2000)
  ): Unit = app.active_group.get.addTransaction(
    Transaction(from, Person("test"), 10.0, date)
  )
   */
  def calculateDebts(): Unit =
    app.active_group match
      case Some(group) =>
        val debts = group.calculateDebt()
        notifyObservers(ControllerEvent.CalculateDebts(debts))
      case None => println("No active group.")

  def setDebtStrategy(input: String): Unit =
    app.active_group match
      case Some(group) =>
        input match
          case "normal" =>
            group.changeDebtCalculationStrategy(NormalDebtStrategy())
            notifyObservers(ControllerEvent.SwitchStrategy("Normal"))
          case "simplified" =>
            group.changeDebtCalculationStrategy(SimplifiedDebtStrategy())
            notifyObservers(ControllerEvent.SwitchStrategy("Simplified"))
          case _ => println("No such strategy.")
      case None => println("No active group.")
}