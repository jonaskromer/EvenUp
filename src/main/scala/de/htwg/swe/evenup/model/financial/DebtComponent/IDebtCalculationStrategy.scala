package de.htwg.swe.evenup.model.financial.DebtComponent

import de.htwg.swe.evenup.model.GroupComponent.IGroup
import de.htwg.swe.evenup.model.PersonComponent.IPerson

trait IDebtCalculationStrategy {
  def calculateDebts(group: IGroup): List[IDebt]
  def calculateBalances(group: IGroup): Map[IPerson, Double]
}
