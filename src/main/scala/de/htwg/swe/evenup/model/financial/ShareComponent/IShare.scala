package de.htwg.swe.evenup.model.financial.ShareComponent

import de.htwg.swe.evenup.model.PersonComponent.IPerson

trait IShare {
  val person: IPerson
  val amount: Double
}
