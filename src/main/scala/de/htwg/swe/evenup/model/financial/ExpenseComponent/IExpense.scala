package de.htwg.swe.evenup.model.financial.ExpenseComponent

import de.htwg.swe.evenup.model.DateComponent.IDate
import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.financial.ShareComponent.IShare

trait IExpense {
  val name: String
  val amount: Double
  val date: IDate
  val paid_by: IPerson
  val shares: List[IShare]

  def updateName(name: String): IExpense
  def updateAmount(amount: Double): IExpense
  def updateDate(date: IDate): IExpense
  def updatePaidBy(paid_by: IPerson): IExpense
  def updateShares(shares: List[IShare]): IExpense
}
