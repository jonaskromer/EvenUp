package de.htwg.swe.evenup.model.financial.ExpenseComponent.BaseExpenseImpl

import de.htwg.swe.evenup.model.DateComponent.IDate
import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.financial.ExpenseComponent.IExpense
import de.htwg.swe.evenup.model.financial.ExpenseComponent.IExpenseBuilder
import de.htwg.swe.evenup.model.financial.ShareComponent.IShare
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person

import com.google.inject.Inject

class ExpenseBuilder @Inject() extends IExpenseBuilder:
  private var name: String         = "noexpensenameset"
  private var amount: Double       = 0.0
  private var date: IDate          = Date(1, 1, 2000)
  private var paidBy: IPerson      = Person("nopersonnameset")
  private var shares: List[IShare] = Nil

  def withName(n: String): IExpenseBuilder         = { name = n; this }
  def withAmount(a: Double): IExpenseBuilder       = { amount = a; this }
  def onDate(d: IDate): IExpenseBuilder            = { date = d; this }
  def paidBy(p: IPerson): IExpenseBuilder          = { paidBy = p; this }
  def withShares(s: List[IShare]): IExpenseBuilder = { shares = s; this }

  def build(): Expense =
    // TODO: add checks for missing args
    Expense(name, amount, date, paidBy, shares)
