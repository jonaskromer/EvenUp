package de.htwg.swe.evenup.model.financial.TransactionComponent

import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.DateComponent.IDate

trait ITransaction {
  val from: IPerson
  val to: IPerson
  val amount: Double
  val date: IDate

  def updateFrom(from: IPerson): ITransaction
  def updateTo(to: IPerson): ITransaction
  def updateAmount(amount: Double): ITransaction
  def updateDate(date: IDate): ITransaction
}
