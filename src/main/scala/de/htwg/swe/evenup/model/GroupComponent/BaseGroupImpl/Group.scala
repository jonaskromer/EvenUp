package de.htwg.swe.evenup.model.GroupComponent.BaseGroupImpl

import de.htwg.swe.evenup.model.GroupComponent.IGroup
import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.financial.DebtComponent.IDebt
import de.htwg.swe.evenup.model.financial.ExpenseComponent.IExpense
import de.htwg.swe.evenup.model.financial.TransactionComponent.ITransaction
import de.htwg.swe.evenup.model.financial.DebtComponent.IDebtCalculationStrategy
import de.htwg.swe.evenup.model.GroupComponent.IGroupFactory

final case class Group(
  name: String,
  members: List[IPerson],
  expenses: List[IExpense],
  transactions: List[ITransaction],
  debt_strategy: IDebtCalculationStrategy
) extends IGroup:

  override def toString(): String =
    val members_string  = members.map(_.toString()).mkString(", ")
    val expenses_string = expenses.map(_.toString()).mkString("\n")

    f"The group $name has the following users and expenses.\nUsers: $members_string\nExpenses:\n$expenses_string"

  def addMember(person: IPerson): IGroup =
    if (members.contains(person))
      this
    else
      copy(members = members :+ person)

  def removeMember(person: IPerson): IGroup =
    if (members.contains(person))
      copy(members = members.filterNot(_ == person))
    else
      this

  def addExpense(expense: IExpense): IGroup = copy(expenses = expenses :+ expense)

  def removeExpense(expense: IExpense): IGroup =
    if (expenses.contains(expense))
      copy(expenses = expenses.filterNot(_ == expense))
    else
      this

  def updateName(name: String): IGroup = copy(name = name)

  def addTransaction(transaction: ITransaction): IGroup = copy(transactions = transactions :+ transaction)

  def removeTransaction(transaction: ITransaction): IGroup =
    if (transactions.contains(transaction))
      copy(transactions = transactions.filterNot(_ == transaction))
    else
      this

  def updateDebtCalculationStrategy(strategy: IDebtCalculationStrategy): IGroup = copy(debt_strategy = strategy)

  def calculateDebt(): List[IDebt] = debt_strategy.calculateDebts(this)

  def containsUser(user_name: String): Boolean = members.exists(_.name == user_name)

object GroupFactory extends IGroupFactory:
  override def apply(
      name: String,
      members: List[IPerson],
      expenses: List[IExpense],
      transactions: List[ITransaction],
      debt_strategy: IDebtCalculationStrategy
  ): IGroup =
    Group(name, members, expenses, transactions, debt_strategy)