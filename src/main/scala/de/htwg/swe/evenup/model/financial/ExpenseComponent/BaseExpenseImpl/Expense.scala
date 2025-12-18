package de.htwg.swe.evenup.model.financial.ExpenseComponent.BaseExpenseImpl

import de.htwg.swe.evenup.model.DateComponent.IDate
import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.financial.ExpenseComponent.IExpense
import de.htwg.swe.evenup.model.financial.ShareComponent.IShare

final case class Expense(
  name: String,
  amount: Double,
  date: IDate,
  paid_by: IPerson,
  shares: List[IShare]
) extends IExpense :

  override def toString(): String =
    val sharesString = shares
      .map(s => f"${s.person.name} owes ${s.amount}%.2f€")
      .mkString(", ")

    f"${paid_by.name} paid $amount%.2f€ for $name on $date. $sharesString."

  def updateName(name: String): IExpense = copy(name = name)

  def updateAmount(amount: Double): IExpense = copy(amount = amount)

  def updateDate(date: IDate): IExpense = copy(date = date)
  def updatePaidBy(paid_by: IPerson): IExpense = copy(paid_by = paid_by)

  def updateShares(shares: List[IShare]): IExpense = copy(shares = shares)