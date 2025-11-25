package de.htwg.swe.evenup.model.debt

import de.htwg.swe.evenup.model.Person

final case class Debt(
  from: Person,
  to: Person,
  amount: Double
):

  override def toString(): String = f"${from.name} owes $amount%.2f to ${to.name}."

  def updateFrom(from: Person): Debt = copy(from = from)

  def updateTo(to: Person): Debt = copy(to = to)

  def updateAmount(amount: Double): Debt = copy(amount = amount)