final case class Group(name: String, members: List[Person], expenses: List[Expense]):

  override def toString(): String = 

    val membersString = members.map(_.toString()).mkString(", ")
    val expensesString = expenses.map(_.toString()).mkString("\n")

    f"The group $name has the following users and expenses.\nUsers: $membersString\nExpenses:\n$expensesString"

  def addMember(person: Person): Group =
    if (members.contains(person)) this else copy(members = members :+ person)

  def addExpense(expense: Expense): Group =
    copy(expenses = expenses :+ expense)