package de.htwg.swe.evenup.model.financial.debt

import de.htwg.swe.evenup.model.{Group, Person}

class SimplifiedDebtStrategy extends DebtCalculationStrategy:
  /*
  Example:
    - Alice owes Bob 10€
    - Bob owes Carlos 10€

    -> Alice owes Carlos 10€
   */

  override def toString(): String = "simplified"

  override def calculateDebts(group: Group): List[Debt] =
    val balances = calculateBalances(group)
    
    var creditors = balances.filter(_._2 > 0.01).toList.sortBy(-_._2)
    var debtors = balances.filter(_._2 < -0.01).toList.sortBy(_._2)
    
    val debts = scala.collection.mutable.ListBuffer[Debt]()
    
    var creditorIdx = 0
    var debtorIdx = 0
    
    while (creditorIdx < creditors.length && debtorIdx < debtors.length) {
      val (creditor, creditAmount) = creditors(creditorIdx)
      val (debtor, debtAmount) = debtors(debtorIdx)
      
      val settleAmount = math.min(creditAmount, -debtAmount)
      
      debts += Debt(from = debtor, to = creditor, amount = settleAmount)
      
      val newCreditAmount = creditAmount - settleAmount
      val newDebtAmount = debtAmount + settleAmount
      
      creditors = creditors.updated(creditorIdx, (creditor, newCreditAmount))
      debtors = debtors.updated(debtorIdx, (debtor, newDebtAmount))
      
      if (newCreditAmount < 0.01) creditorIdx += 1
      if (newDebtAmount > -0.01) debtorIdx += 1
    }
    
    debts.toList
