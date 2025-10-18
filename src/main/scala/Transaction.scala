final case class Transaction(from: Person, to: Person, amount: Double):

    override def toString(): String = 
        f"${from.name} paid $amount to ${to.name}."
