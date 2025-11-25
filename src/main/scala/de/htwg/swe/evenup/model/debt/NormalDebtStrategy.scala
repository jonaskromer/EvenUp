package de.htwg.swe.evenup.model.debt

import de.htwg.swe.evenup.model.{Group, Person, Transaction, Date}

class NormalDebtStrategy extends DebtCalculationStrategy:
  override def calculateDebts(group:Group): List[Transaction] =
    val balances = DebtCalculationUtils.calculateBalances(group)
    
    balances.flatMap { case (person, balance) =>
      if balance < 0 then
        group.expenses
          .filter(_.shares.exists(_.person == person))
          .flatMap { expense =>
            val share = expense.shares.find(_.person == person).get
            if expense.paid_by != person then
              Some(Transaction(
                from = person,
                to = expense.paid_by,
                amount = share.amount,
                //TODO: better way to show debts maybe a debt class or smth
                date = Date(1, 1, 1)
              ))
            else None
          }
      else Nil
    }.toList