package de.htwg.swe.evenup.model.debt

import de.htwg.swe.evenup.model.{Group, Transaction}

trait DebtCalculationStrategy:
  def calculateDebts(group: Group): List[Transaction]