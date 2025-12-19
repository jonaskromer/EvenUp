package de.htwg.swe.evenup.control

import de.htwg.swe.evenup.util.ObservableEvent
import de.htwg.swe.evenup.model.GroupComponent.IGroup
import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.financial.DebtComponent.IDebt
import de.htwg.swe.evenup.model.financial.ExpenseComponent.IExpense
import de.htwg.swe.evenup.model.financial.DebtComponent.IDebtCalculationStrategy

enum EventResponse extends ObservableEvent:
  case Success
  case Undo(result: UndoResult, stack_size: Int)
  case Redo(result: RedoResult, stack_size: Int)
  case Quit
  case MainMenu
  case AddGroup(result: AddGroupResult, group: IGroup)
  case GotoGroup(result: GotoGroupResult, group: IGroup)
  case AddUserToGroup(result: AddUserToGroupResult, user: IPerson, group: IGroup)
  case AddExpenseToGroup(result: AddExpenseToGroupResult, expense: IExpense)
  case CalculateDebts(result: CalculateDebtsResult, debts: List[IDebt])
  case SetDebtStrategy(result: SetDebtStrategyResult, strategy: IDebtCalculationStrategy)
  case UncoveredFailure(error_text: String)
  case NextHandler

enum UndoResult:
  case Success
  case EmptyStack

enum RedoResult:
  case Success
  case EmptyStack

enum AddGroupResult:
  case Success
  case GroupExists

enum GotoGroupResult:
  case Success
  case SuccessEmptyGroup
  case GroupNotFound

enum AddUserToGroupResult:
  case Success
  case UserAlreadyAdded
  case NoActiveGroup

enum AddExpenseToGroupResult:
  case Success
  case NoActiveGroup
  case SharesSumWrong
  case SharesPersonNotFound
  case PaidByNotFound
  case InvalidAmount
  case InvalidShares

enum SetDebtStrategyResult:
  case Success
  case NoActiveGroup

enum CalculateDebtsResult:
  case Success
  case NoActiveGroup
