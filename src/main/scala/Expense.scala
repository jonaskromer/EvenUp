final case class Expense(name: String, amount: Double, date: String, paid_by: Person, shares: Map[Person, Double]):

    override def toString(): String =

        val sharesString =
            shares.map { case (person, share) =>
                f"${person.name} owes €$share%.2f"
            }.mkString(", ")

        s"${paid_by.name} paid $amount€ for $name on $date. $sharesString"