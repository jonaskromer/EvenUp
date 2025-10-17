final case class Transaction(from: Person, to: Person, amount: Double):

  override def toString: String =
    s"${from} has paid ${amount}€ to ${to}."
