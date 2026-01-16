package de.htwg.swe.evenup.model.financial.ShareComponent.BaseShareImpl

import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.financial.ShareComponent.IShare
import de.htwg.swe.evenup.model.financial.ShareComponent.IShareFactory

final case class Share(person: IPerson, amount: Double) extends IShare

object ShareFactory extends IShareFactory:
  def apply(person: IPerson, amount: Double): IShare = Share(person, amount)
