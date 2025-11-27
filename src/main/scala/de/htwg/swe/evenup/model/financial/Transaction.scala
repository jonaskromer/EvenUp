package de.htwg.swe.evenup.model.financial

import de.htwg.swe.evenup.model.Person
import de.htwg.swe.evenup.model.Date

final case class Transaction(
  from: Person,
  to: Person,
  amount: Double,
  date: Date
):

  override def toString(): String = f"${from.name} paid $amount%.2f to ${to.name} on ${date}."

  def updateFrom(from: Person): Transaction = copy(from = from)

  def updateTo(to: Person): Transaction = copy(to = to)

  def updateAmount(amount: Double): Transaction = copy(amount = amount)

  def updateDate(date: Date): Transaction = copy(date = date)
