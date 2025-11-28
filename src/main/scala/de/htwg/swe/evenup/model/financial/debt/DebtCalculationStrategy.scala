package de.htwg.swe.evenup.model.financial.debt

import de.htwg.swe.evenup.model.Group
import de.htwg.swe.evenup.model.Person

trait DebtCalculationStrategy:
  def calculateDebts(group: Group): List[Debt]

  def calculateBalances(group: Group): Map[Person, Double] =
    val balances = scala.collection.mutable.Map[Person, Double]()

    group.members.foreach(member => balances(member) = 0.0)

    group.expenses.foreach { expense =>
      balances(expense.paid_by) = balances(expense.paid_by) + expense.amount

      expense.shares.foreach { share =>
        balances(share.person) = balances(share.person) - share.amount
      }
    }
    balances.toMap
