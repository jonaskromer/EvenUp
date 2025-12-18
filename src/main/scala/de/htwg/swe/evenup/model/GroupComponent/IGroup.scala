package de.htwg.swe.evenup.model.GroupComponent

import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.financial.ExpenseComponent.IExpense
import de.htwg.swe.evenup.model.financial.TransactionComponent.ITransaction
import de.htwg.swe.evenup.model.financial.DebtComponent.IDebtCalculationStrategy
import de.htwg.swe.evenup.model.financial.DebtComponent.IDebt

trait IGroup:
  val name: String
  val members: List[IPerson]
  val expenses: List[IExpense]
  val transactions: List[ITransaction]
  val debt_strategy: IDebtCalculationStrategy

  def addMember(person: IPerson): IGroup
  def removeMember(person: IPerson): IGroup

  def addExpense(expense: IExpense): IGroup
  def removeExpense(expense: IExpense): IGroup

  def updateName(name: String): IGroup

  def addTransaction(transaction: ITransaction): IGroup
  def removeTransaction(transaction: ITransaction): IGroup

  def updateDebtCalculationStrategy(strategy: IDebtCalculationStrategy): IGroup

  def calculateDebt(): List[IDebt]

  def containsUser(user_name: String): Boolean
