package de.htwg.swe.evenup.model.financial.debt

import de.htwg.swe.evenup.model.Group

trait DebtCalculationStrategy:
  def calculateDebts(group: Group): List[Debt]