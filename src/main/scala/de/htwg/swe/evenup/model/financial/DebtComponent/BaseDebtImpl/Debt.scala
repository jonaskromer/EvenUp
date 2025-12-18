package de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl

import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.financial.DebtComponent.IDebt

final case class Debt(
  from: IPerson,
  to: IPerson,
  amount: Double
) extends IDebt:

  override def toString(): String = f"${from.name} owes $amount%.2f to ${to.name}."

  def updateFrom(from: IPerson): IDebt = copy(from = from)

  def updateTo(to: IPerson): IDebt        = copy(to = to)
  def updateAmount(amount: Double): IDebt = copy(amount = amount)
