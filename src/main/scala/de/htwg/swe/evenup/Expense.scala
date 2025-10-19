package de.htwg.swe.evenup

final case class Expense(
  name: String,
  amount: Double,
  date: String,
  paid_by: Person,
  shares: Map[Person, Double]
):

  override def toString(): String =
    val sharesString = shares
      .map { case (person, share) => f"${person.name} owes $share%.2f€" }
      .mkString(", ")

    f"${paid_by.name} paid $amount%.2f€ for $name on $date. $sharesString."

  def updateName(name: String): Expense = copy(name = name)

  def updateAmount(amount: Double): Expense = copy(amount = amount)

  def updateDate(date: String): Expense = copy(date = date)

  def updatePaidBy(paid_by: Person): Expense = copy(paid_by = paid_by)

  def updateShares(shares: Map[Person, Double]): Expense = copy(shares = shares)
