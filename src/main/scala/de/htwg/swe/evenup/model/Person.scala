package de.htwg.swe.evenup.model

final case class Person(
  name: String
): // future: mail, friendlist, login, picture

  override def toString(): String = f"$name"

  override def equals(obj: Any): Boolean =
    obj match
      case p: Person => p.name == this.name
      case _         => false

  override def hashCode(): Int = name.hashCode

  def updateName(name: String): Person = copy(name = name)
