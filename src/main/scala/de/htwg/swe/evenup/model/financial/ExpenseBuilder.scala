package de.htwg.swe.evenup.model.financial

import de.htwg.swe.evenup.model.{Date, Person, Share}

class ExpenseBuilder:
  private var name: String        = "noexpensenameset"
  private var amount: Double      = 0.0
  private var date: Date          = Date(1, 1, 2000)
  private var paidBy: Person      = Person("nopersonnameset")
  private var shares: List[Share] = Nil

  def withName(n: String): ExpenseBuilder        = { name = n; this }
  def withAmount(a: Double): ExpenseBuilder      = { amount = a; this }
  def onDate(d: Date): ExpenseBuilder            = { date = d; this }
  def paidBy(p: Person): ExpenseBuilder          = { paidBy = p; this }
  def withShares(s: List[Share]): ExpenseBuilder = { shares = s; this }

  def build(): Expense =
    // TODO: add checks for missing args
    Expense(name, amount, date, paidBy, shares)
