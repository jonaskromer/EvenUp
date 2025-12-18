package de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl

import de.htwg.swe.evenup.model.financial.DebtComponent.IDebtCalculationStrategy
import de.htwg.swe.evenup.model.GroupComponent.IGroup
import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.financial.DebtComponent.IDebt

trait DebtCalculationStrategy extends IDebtCalculationStrategy:
  def calculateDebts(group: IGroup): List[IDebt]

  def calculateBalances(group: IGroup): Map[IPerson, Double] =
    val balances = scala.collection.mutable.Map[IPerson, Double]()

    group.members.foreach(member => balances(member) = 0.0)

    group.expenses.foreach { expense =>
      balances(expense.paid_by) = balances(expense.paid_by) + expense.amount

      expense.shares.foreach { share =>
        balances(share.person) = balances(share.person) - share.amount
      }
    }
    balances.toMap
