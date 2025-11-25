package de.htwg.swe.evenup.model.debt

import de.htwg.swe.evenup.model.{Group, Person, Transaction, Date}

class NormalDebtStrategy extends DebtCalculationStrategy:
  override def calculateDebts(group:Group): List[Debt] =
    val balances = DebtCalculationUtils.calculateBalances(group)
    
    balances.flatMap { case (person, balance) =>
      if balance < 0 then
        group.expenses
          .filter(_.shares.exists(_.person == person))
          .flatMap { expense =>
            val share = expense.shares.find(_.person == person).get
            if expense.paid_by != person then
              Some(Debt(
                from = person,
                to = expense.paid_by,
                amount = share.amount
              ))
            else None
          }
      else Nil
    }.toList