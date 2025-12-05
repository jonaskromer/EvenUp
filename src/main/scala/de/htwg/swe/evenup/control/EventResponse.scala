package de.htwg.swe.evenup.control

import de.htwg.swe.evenup.util.ObservableEvent
import de.htwg.swe.evenup.model.Group
import de.htwg.swe.evenup.model.Person
import de.htwg.swe.evenup.model.financial.Expense
import de.htwg.swe.evenup.model.financial.debt.Debt
import de.htwg.swe.evenup.model.financial.debt.DebtCalculationStrategy

enum EventResponse extends ObservableEvent:
  case Success
  case Undo(result: UndoResult, stack_size: Int)
  case Redo(result: RedoResult, stack_size: Int)
  case Quit
  case MainMenu
  case AddGroup(result: AddGroupResult, group: Group)
  case GotoGroup(result: GotoGroupResult, group: Group)
  case AddUserToGroup(result: AddUserToGroupResult, user: Person, group: Group)
  case AddExpenseToGroup(result: AddExpenseToGroupResult, expense: Expense)
  case CalculateDebts(result: CalculateDebtsResult, debts: List[Debt])
  case SetDebtStrategy(result: SetDebtStrategyResult, strategy: DebtCalculationStrategy)
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

enum SetDebtStrategyResult:
  case Success
  case NoActiveGroup

enum CalculateDebtsResult:
  case Success
  case NoActiveGroup
