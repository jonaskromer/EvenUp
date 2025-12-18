package de.htwg.swe.evenup.model.financial.DebtComponent

import de.htwg.swe.evenup.model.PersonComponent.IPerson

trait IDebt:
  val from: IPerson
  val to: IPerson
  val amount: Double

  def updateFrom(from: IPerson): IDebt
  def updateTo(to: IPerson): IDebt
  def updateAmount(amount: Double): IDebt
