package de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl

import de.htwg.swe.evenup.model.GroupComponent.IGroup
import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.financial.DebtComponent.IDebt

class SimplifiedDebtStrategy extends DebtCalculationStrategy:
  /*
  Example:
    - Alice owes Bob 10€
    - Bob owes Carlos 10€

    -> Alice owes Carlos 10€
   */

  override def toString(): String = "simplified"

  override def calculateDebts(group: IGroup): List[IDebt] =
    val balances = calculateBalances(group)

    var creditors = balances.filter(_._2 > 0.01).toList.sortBy(-_._2)
    var debtors   = balances.filter(_._2 < -0.01).toList.sortBy(_._2)

    val debts = scala.collection.mutable.ListBuffer[IDebt]()

    var creditorIdx = 0
    var debtorIdx   = 0

    while (creditorIdx < creditors.length && debtorIdx < debtors.length) {
      val (creditor, creditAmount) = creditors(creditorIdx)
      val (debtor, debtAmount)     = debtors(debtorIdx)

      val settleAmount = math.min(creditAmount, -debtAmount)

      debts += Debt(from = debtor, to = creditor, amount = settleAmount)

      val newCreditAmount = creditAmount - settleAmount
      val newDebtAmount   = debtAmount + settleAmount

      creditors = creditors.updated(creditorIdx, (creditor, newCreditAmount))
      debtors = debtors.updated(debtorIdx, (debtor, newDebtAmount))

      if (newCreditAmount < 0.01)
        creditorIdx += 1
      if (newDebtAmount > -0.01)
        debtorIdx += 1
    }

    debts.toList
