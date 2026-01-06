package de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl

import de.htwg.swe.evenup.model.PersonComponent.IPerson

import com.google.inject.Inject

final case class Person @Inject() (
  name: String
) extends IPerson: // future: mail, friendlist, login, picture

  override def toString(): String = f"$name"

  override def equals(obj: Any): Boolean =
    obj match
      case p: Person => p.name == this.name
      case _         => false

  override def hashCode(): Int = name.hashCode

  def updateName(name: String): IPerson = copy(name = name)
