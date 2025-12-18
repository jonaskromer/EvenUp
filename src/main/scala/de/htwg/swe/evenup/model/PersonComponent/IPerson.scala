package de.htwg.swe.evenup.model.PersonComponent

trait IPerson {
  val name: String
  def updateName(name: String): IPerson
}
