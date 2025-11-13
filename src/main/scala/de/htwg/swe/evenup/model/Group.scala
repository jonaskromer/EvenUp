package de.htwg.swe.evenup.model

import de.htwg.swe.evenup.model.Expense
import de.htwg.swe.evenup.model.Person

final case class Group(
  name: String,
  members: List[Person],
  expenses: List[Expense],
  transactions: List[Transaction]
):

  override def toString(): String =
    val members_string  = members.map(_.toString()).mkString(", ")
    val expenses_string = expenses.map(_.toString()).mkString("\n")

    f"The group $name has the following users and expenses.\nUsers: $members_string\nExpenses:\n$expenses_string"

  def addMember(person: Person): Group =
    if (members.contains(person))
      this
    else
      copy(members = members :+ person)

  def removeMember(person: Person): Group =
    if (members.contains(person))
      copy(members = members.filterNot(_ == person))
    else
      this

  def addExpense(expense: Expense): Group = copy(expenses = expenses :+ expense)

  def removeExpense(expense: Expense): Group =
    if (expenses.contains(expense))
      copy(expenses = expenses.filterNot(_ == expense))
    else
      this

  def updateName(name: String): Group = copy(name = name)

  def addTransaction(transaction: Transaction): Group = copy(transactions =
    transactions :+ transaction
  )

  def removeTransaction(transaction: Transaction): Group =
    if (transactions.contains(transaction))
      copy(transactions = transactions.filterNot(_ == transaction))
    else
      this
