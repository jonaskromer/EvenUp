package de.htwg.swe.evenup.model.financial

import de.htwg.swe.evenup.model.{Date, Person}

final case class Expense(
  name: String,
  amount: Double,
  date: Date,
  paid_by: Person,
  shares: List[Share]
):

  override def toString(): String =
    val sharesString = shares
      .map(s => f"${s.person.name} owes ${s.amount}%.2f€")
      .mkString(", ")

    f"${paid_by.name} paid $amount%.2f€ for $name on $date. $sharesString."

  def updateName(name: String): Expense = copy(name = name)

  def updateAmount(amount: Double): Expense = copy(amount = amount)

  def updateDate(date: Date): Expense = copy(date = date)

  def updatePaidBy(paid_by: Person): Expense = copy(paid_by = paid_by)

  def updateShares(shares: List[Share]): Expense = copy(shares = shares)
