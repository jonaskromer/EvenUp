package de.htwg.swe.evenup

@main def hello(): Unit =


    val p_jonas = Person("Jonas")
    val p_bryan = Person("Bryan")

    val e_1 = Expense("Wasser", 10.00, date = "01.01.2000", paid_by = p_jonas, shares = Map(p_bryan -> 7, p_jonas -> 3))
    val e_2 = Expense("Brot", 20.00, date = "01.01.2000", paid_by = p_bryan, shares = Map(p_bryan -> 17, p_jonas -> 3))
    val e_3 = Expense("Sprit", 40.00, date = "01.01.2000", paid_by = p_jonas, shares = Map(p_bryan -> 27, p_jonas -> 13))

    var g_wg = Group("WG", List(p_jonas, p_bryan), List(e_1, e_2, e_3))

    val e_4 = Expense("Rotwein", 10.00, date = "01.01.2000", paid_by = p_jonas, shares = Map(p_bryan -> 4, p_jonas -> 6))

    var app = AppState(List(g_wg))

    g_wg = g_wg.addExpense(e_4)

    println(g_wg)
