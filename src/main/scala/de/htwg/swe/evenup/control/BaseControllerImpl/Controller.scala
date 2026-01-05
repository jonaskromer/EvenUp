package de.htwg.swe.evenup.control.BaseControllerImpl

import de.htwg.swe.evenup.model.AppComponent.BaseAppImpl.App
import de.htwg.swe.evenup.util.{Observable, ObservableEvent, UndoManager}
import de.htwg.swe.evenup.control.*
import de.htwg.swe.evenup.model.AppComponent.IApp
import de.htwg.swe.evenup.model.DateComponent.IDate

import com.google.inject.Inject

class Controller @Inject() (var app: IApp) extends IController:

  val undoManager = new UndoManager
  val argsHandler = new ArgsHandler

  def undo(): Unit =
    val response = argsHandler.checkOrDelegate(
      Map("operation" -> "undo", "undo_stack_size" -> undoManager.getUndoStackSize),
      app
    )
    response match
      case EventResponse.Undo(UndoResult.Success, _) =>
        undoManager.undoStep
        notifyObservers(response)
      case EventResponse.Undo(UndoResult.EmptyStack, _) => notifyObservers(response)
      case _                                            => EventResponse.UncoveredFailure("undo")

  def redo(): Unit =
    val response = argsHandler.checkOrDelegate(
      Map("operation" -> "redo", "redo_stack_size" -> undoManager.getRedoStackSize),
      app
    )
    response match
      case EventResponse.Redo(RedoResult.Success, _) =>
        undoManager.redoStep
        notifyObservers(response)
      case EventResponse.Redo(RedoResult.EmptyStack, _) => notifyObservers(response)
      case _                                            => EventResponse.UncoveredFailure("redo")

  def quit: Unit =
    // TODO: save state
    notifyObservers(EventResponse.Quit)
    System.exit(0)

  def gotoMainMenu: Unit = undoManager.doStep(GotoMainMenuCommand(this))

  def gotoGroup(group_name: String): Unit =
    val response = argsHandler.checkOrDelegate(Map("operation" -> "gotoGroup", "group_name" -> group_name), app)
    response match
      case EventResponse.GotoGroup(GotoGroupResult.SuccessEmptyGroup, group) =>
        undoManager.doStep(GotoEmptyGroupCommand(this, group))
      case EventResponse.GotoGroup(GotoGroupResult.Success, group) => undoManager.doStep(GotoGroupCommand(this, group))
      case EventResponse.GotoGroup(GotoGroupResult.GroupNotFound, group) =>
        notifyObservers(EventResponse.GotoGroup(GotoGroupResult.GroupNotFound, group))
      case _ => EventResponse.UncoveredFailure("gotoGroup")

  def addGroup(group_name: String): Unit =
    val response = argsHandler.checkOrDelegate(Map("operation" -> "addGroup", "group_name" -> group_name), app)
    response match
      case EventResponse.AddGroup(AddGroupResult.Success, new_group) =>
        undoManager.doStep(AddGroupCommand(this, new_group))
      case (EventResponse.AddGroup(AddGroupResult.GroupExists, existingGroup)) =>
        notifyObservers(
          EventResponse.AddGroup(AddGroupResult.GroupExists, existingGroup)
        )
      case _ => EventResponse.UncoveredFailure("addGroup")

  def addUserToGroup(user_name: String): Unit =
    val response = argsHandler.checkOrDelegate(Map("operation" -> "addUserToGroup", "user_name" -> user_name), app)
    response match
      case (EventResponse.AddUserToGroup(AddUserToGroupResult.Success, user, group)) =>
        undoManager.doStep(AddUserToGroupCommand(this, user))
      case EventResponse.AddUserToGroup(AddUserToGroupResult.UserAlreadyAdded, user, group) =>
        notifyObservers(EventResponse.AddUserToGroup(AddUserToGroupResult.UserAlreadyAdded, user, group))
      case EventResponse.AddUserToGroup(AddUserToGroupResult.NoActiveGroup, user, group) =>
        notifyObservers(EventResponse.AddUserToGroup(AddUserToGroupResult.NoActiveGroup, user, group))
      case _ => EventResponse.UncoveredFailure("addUserToGroup")

  def addExpenseToGroup(
    expense_name: String,
    paid_by: String,
    amount: Double,
    date: IDate,
    shares: Option[String] = None
  ): Unit =
    val response = argsHandler.checkOrDelegate(
      Map(
        "operation"    -> "addExpenseToGroup",
        "expense_name" -> expense_name,
        "paid_by"      -> paid_by,
        "amount"       -> amount,
        "date"         -> date,
        "shares"       -> shares
      ),
      app
    )
    response match
      case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.Success, expense) =>
        undoManager.doStep(AddExpenseToGroupCommand(this, expense))
      case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.NoActiveGroup, expense) =>
        notifyObservers(EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.NoActiveGroup, expense))
      case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.PaidByNotFound, expense) =>
        notifyObservers(EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.PaidByNotFound, expense))
      case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.InvalidAmount, expense) =>
        notifyObservers(EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.InvalidAmount, expense))
      case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.SharesPersonNotFound, expense) =>
        notifyObservers(EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.SharesPersonNotFound, expense))
      case _ => EventResponse.UncoveredFailure("addExpenseToGroup")

  def setDebtStrategy(strategy: String): Unit =
    val response = argsHandler.checkOrDelegate(Map("operation" -> "setDebtStrategy", "strategy" -> strategy), app)
    response match
      case EventResponse.SetDebtStrategy(SetDebtStrategyResult.Success, strategy) =>
        undoManager.doStep(SetDebtCalculationStrategy(this, strategy))
      case EventResponse.SetDebtStrategy(SetDebtStrategyResult.NoActiveGroup, strategy) =>
        notifyObservers(EventResponse.SetDebtStrategy(SetDebtStrategyResult.NoActiveGroup, strategy))
      case _ => EventResponse.UncoveredFailure("setDebtStrategy")

  def calculateDebts(): Unit =
    val response = argsHandler.checkOrDelegate(Map("operation" -> "calculateDebts"), app)
    response match
      case EventResponse.CalculateDebts(CalculateDebtsResult.Success, debts) =>
        undoManager.doStep(CalculateDebtsCommand(this, debts))
      case EventResponse.CalculateDebts(CalculateDebtsResult.NoActiveGroup, debts) =>
        notifyObservers(EventResponse.CalculateDebts(CalculateDebtsResult.NoActiveGroup, debts))
      case _ => EventResponse.UncoveredFailure("calculateDebts")

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
