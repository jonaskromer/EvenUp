package de.htwg.swe.evenup.model.debt

import de.htwg.swe.evenup.model.{Group, Transaction}

class DebtCalculator(var strategy: DebtCalculationStrategy):
  def setStrategy(strategy: DebtCalculationStrategy): Unit =
    this.strategy = strategy

  def calculate(group: Group): List[Transaction] =
    strategy.calculateDebts(group)