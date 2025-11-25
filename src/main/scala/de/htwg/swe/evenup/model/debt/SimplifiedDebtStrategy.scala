package de.htwg.swe.evenup.model.debt

import de.htwg.swe.evenup.model.{Group, Person, Transaction, Date}

class SimplifiedDebtStrategy extends DebtCalculationStrategy:
  /*
  TODO: create a simplified Debt calculation
  Example:
    - Alice owes Bob 10€
    - Bob owes Carlos 10€

    -> Alice owes Carlos 10€
    */
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
                //TODO: better way to save debts maybe a debt class or smth, actually balances per person per group would be good
                date = Date(1, 1, 1)
              ))
            else None
          }
      else Nil
    }.toList