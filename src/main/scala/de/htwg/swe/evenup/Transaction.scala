package de.htwg.swe.evenup

final case class Transaction(from: Person, to: Person, amount: Double):

    override def toString(): String = 
        f"${from.name} paid $amount%.2f to ${to.name}."

    def updateFrom(from: Person): Transaction =
        copy(from = from)
    
    def updateTo(to: Person): Transaction =
        copy(to = to)

    def updateAmount(amount: Double): Transaction =
        copy(amount = amount)

        