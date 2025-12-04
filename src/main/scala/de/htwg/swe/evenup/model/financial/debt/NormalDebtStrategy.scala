package de.htwg.swe.evenup.model.financial.debt

import de.htwg.swe.evenup.model.{Group, Person}

class NormalDebtStrategy extends DebtCalculationStrategy:

  override def toString(): String = "normal"

  override def calculateDebts(group: Group): List[Debt] =
    val balances = calculateBalances(group)

    val debtMap = scala.collection.mutable.Map[(Person, Person), Double]()

    group.expenses.foreach { expense =>
      expense.shares.foreach { share =>
        if share.person != expense.paid_by then
          val key = (share.person, expense.paid_by)
          debtMap(key) = debtMap.getOrElse(key, 0.0) + share.amount
      }
    }

    val processedPairs = scala.collection.mutable.Set[(Person, Person)]()

    debtMap.flatMap { case ((from, to), amount) =>
      val pair        = (from, to)
      val reversePair = (to, from)

      if !processedPairs.contains(pair) && !processedPairs.contains(reversePair) then
        processedPairs.add(pair)
        processedPairs.add(reversePair)

        val reverseAmount = debtMap.getOrElse(reversePair, 0.0)
        val netAmount     = amount - reverseAmount

        if netAmount > 0 then Some(Debt(from = from, to = to, amount = netAmount))
        else if netAmount < 0 then Some(Debt(from = to, to = from, amount = -netAmount))
        else None
      else None
    }.toList
