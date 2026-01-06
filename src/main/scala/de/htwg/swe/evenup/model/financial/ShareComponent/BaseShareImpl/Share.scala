package de.htwg.swe.evenup.model.financial.ShareComponent.BaseShareImpl

import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.financial.ShareComponent.IShare

import com.google.inject.Inject

final case class Share @Inject() (person: IPerson, amount: Double) extends IShare
