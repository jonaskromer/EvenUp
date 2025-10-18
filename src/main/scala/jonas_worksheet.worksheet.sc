case class Expense(name: String, amount: Double, date: String, paid_by: Person, shares: Map[Person, Double])

case class Person(name: String)

case class Transaction(from: Person, to: Person, amount: Double)

case class BalanceSheet(balances: Map[Person, Double]):
  
    def total: Double = balances.values.sum
    def nonZeroBalances: Map[Person, Double] = balances.filter(_._2 != 0)

case class Group(name: String, members: List[Person], expenses: List[Expense]):

    def addMember(person: Person): Group =
        if (members.contains(person)) this else copy(members = members :+ person)

    def addExpense(expense: Expense): Group =
        copy(expenses = expenses :+ expense)

    def computeBalances: BalanceSheet =
        val initial = members.map(_ -> 0.0).toMap

        val balances = expenses.foldLeft(initial) { (acc, expense) =>
        // Subtract each participant's share except the payer
        val afterShares = expense.shares.foldLeft(acc) { (a, pair) =>
            val (person, share) = pair
            if person == expense.paid_by then a
            else a.updated(person, a(person) - share)
        }

        // Add total amount to payer
        afterShares.updated(expense.paid_by, afterShares(expense.paid_by) + expense.amount)
        }

        BalanceSheet(balances)



def printGroupState(group: Group): Unit =
    println(s"=== Group: ${group.name} ===")
    println(s"Members: ${group.members.map(_.name).mkString(", ")}")
    println("Expenses:")
    if group.expenses.isEmpty then
        println("  (none)")
    else
        group.expenses.foreach { e =>
        println(s"  ${e.name}: ${e.paid_by.name} paid €${e.amount} on ${e.date}")
        e.shares.foreach { case (person, share) =>
            println(f"    → ${person.name} owes €$share%.2f")
        }
        }

    println("\nBalances:")
    val balances = group.computeBalances.balances
    balances.foreach { case (p, b) =>
        val sign = if b >= 0 then "+" else "-"
        println(f"  ${p.name}%-10s : $sign€${math.abs(b)}%.2f")
    }


val p_jonas = Person("Jonas")
val p_bryan = Person("Bryan")

val e_1 = Expense("Wasser", 10.00, date = "01.01.2000", paid_by = p_jonas, shares = Map(p_bryan -> 7, p_jonas -> 3))
val e_2 = Expense("Brot", 20.00, date = "01.01.2000", paid_by = p_bryan, shares = Map(p_bryan -> 17, p_jonas -> 3))
val e_3 = Expense("Sprit", 40.00, date = "01.01.2000", paid_by = p_jonas, shares = Map(p_bryan -> 27, p_jonas -> 13))

var g_wg = Group("WG", List(p_jonas, p_bryan), List(e_1, e_2, e_3))

printGroupState(g_wg)

val e_4 = Expense("Rotwein", 10.00, date = "01.01.2000", paid_by = p_jonas, shares = Map(p_bryan -> 4, p_jonas -> 6))

g_wg = g_wg.addExpense(e_4)

printGroupState(g_wg)