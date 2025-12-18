package de.htwg.swe.evenup.model.financial.ExpenseComponent

import de.htwg.swe.evenup.model.DateComponent.IDate
import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.financial.ShareComponent.IShare

trait IExpenseBuilder {
  def withName(n: String): IExpenseBuilder
  def withAmount(a: Double): IExpenseBuilder
  def onDate(d: IDate): IExpenseBuilder
  def paidBy(p: IPerson): IExpenseBuilder
  def withShares(s: List[IShare]): IExpenseBuilder
  def build(): IExpense
}
