package de.htwg.swe.evenup

final case class Person(name: String): //future: mail, friendlist, login, picture

    override def toString(): String = f"$name"

    def updateName(name: String): Person =
        copy(name = name)
