package de.htwg.swe.evenup.model.financial.TransactionComponent.BaseTransactionImpl

import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.DateComponent.IDate
import de.htwg.swe.evenup.model.financial.TransactionComponent.ITransaction
import de.htwg.swe.evenup.model.financial.TransactionComponent.ITransactionFactory

final case class Transaction(
  from: IPerson,
  to: IPerson,
  amount: Double,
  date: IDate
) extends ITransaction:

  override def toString(): String = f"${from.name} paid $amount%.2f to ${to.name} on ${date}."

  def updateFrom(from: IPerson): ITransaction = copy(from = from)

  def updateTo(to: IPerson): ITransaction = copy(to = to)

  def updateAmount(amount: Double): ITransaction = copy(amount = amount)

  def updateDate(date: IDate): ITransaction = copy(date = date)

object TransactionFactory extends ITransactionFactory:
  def apply(from: IPerson, to: IPerson, amount: Double, date: IDate): ITransaction = Transaction(from, to, amount, date)
