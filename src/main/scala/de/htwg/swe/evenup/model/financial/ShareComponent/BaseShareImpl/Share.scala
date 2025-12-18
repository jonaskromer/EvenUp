package de.htwg.swe.evenup.model.financial.ShareComponent.BaseShareImpl

import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.financial.ShareComponent.IShare

final case class Share(person: IPerson, amount: Double) extends IShare
