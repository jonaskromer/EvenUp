package de.htwg.swe.evenup

class TUI(var state: AppState = AppState(Nil)):
  def processInput(input: String): Option[AppState] =
    val tokens = input.trim.split("\\s+").toList
    tokens match
      case "quit" :: Nil => 
        println("Goodbye!")
        None
      case "help" :: Nil =>
        printHelp()
        Some(state)
      case "groups" :: Nil =>
        showGroups()
        Some(state)
      case "users" :: Nil =>
        showUsers()
        Some(state)
      case "creategroup" :: name :: Nil =>
        createGroup(name)
        Some(state)
      case "addusertogroup" :: groupName :: userName :: Nil =>
        addUserToGroup(groupName, userName)
        Some(state)
      case "addexpense" :: groupName :: expenseName :: amount :: date :: paidBy :: Nil =>
        addExpense(groupName, expenseName, amount.toDouble, date, paidBy)
        Some(state)
      case "showgroup" :: name :: Nil =>
        showGroupDetails(name)
        Some(state)
      case "createperson" :: name :: Nil =>
        createPerson(name)
        Some(state)
      case _ =>
        println("Unknown command. Use 'help' to receive a list of commands.")
        Some(state)

  private def printHelp(): Unit =
    println("""
      |Commands:
      |  help                     - Shows this help
      |  quit                     - Closes the program
      |  groups                   - Shows all groups
      |  users                    - Shows all users
      |  showgroup [name]         - Shows details of a group
      |  creategroup [name]       - Creates a group
      |  createperson [name]      - Creates a person
      |  adduser [gruppe] [name]  - Fügt einen Benutzer zu einer Gruppe hinzu
      |  addexpense [gruppe] [name] [betrag] [datum] [bezahlt_von] - Fügt eine Ausgabe hinzu
      |""".stripMargin)

  private def showGroups(): Unit =
    if state.allGroups.isEmpty then
      println("Keine Gruppen vorhanden.")
    else
      println("Gruppen:")
      state.allGroups.foreach { group =>
        println(s"  ${group.name} (${group.members.size} Mitglieder, ${group.expenses.size} Ausgaben)")
      }

  private def showUsers(): Unit =
    val users = state.allUsers
    if users.isEmpty then
      println("Keine Benutzer vorhanden.")
    else
      println("Alle Benutzer:")
      users.foreach(user => println(s"  ${user.name}"))

  private def createGroup(name: String): Unit =
    val newGroup = Group(name, List(), List())
    state = state.addGroup(newGroup)
    println(s"Gruppe '$name' wurde erstellt.")

  private def addUserToGroup(groupName: String, userName: String): Unit =
    state.findGroupByName(groupName) match
      case Some(group) =>
        val newUser = Person(userName)
        val updatedGroup = group.addMember(newUser)
        state = state.updateGroup(updatedGroup)
        println(s"Benutzer '$userName' wurde zur Gruppe '${group.name}' hinzugefügt.")
      case None =>
        println(s"Gruppe '$groupName' wurde nicht gefunden.")

  private def showGroupDetails(name: String): Unit =
    state.findGroupByName(name) match
      case Some(group) =>
        println(group.toString())
      case None =>
        println(s"Gruppe '$name' wurde nicht gefunden.")

  private def createPerson(name: String): Unit =
    val person = Person(name)
    println(s"Person '${person.name}' wurde erstellt.")

  private def addExpense(groupName: String, expenseName: String, amount: Double, date: String, paidByName: String): Unit =
    state.findGroupByName(groupName) match
      case Some(group) =>
        group.members.find(_.name == paidByName) match
          case Some(paidBy) =>
            // Erstelle eine gleichmäßige Aufteilung zwischen allen Gruppenmitgliedern
            val shareAmount = amount / group.members.size
            val shares = group.members.map(m => m -> shareAmount).toMap
            val expense = Expense(expenseName, amount, date, paidBy, shares)
            val updatedGroup = group.copy(expenses = group.expenses :+ expense)
            state = state.updateGroup(updatedGroup)
            println(s"Ausgabe '$expenseName' (${amount}€) wurde zur Gruppe '${group.name}' hinzugefügt.")
          case None =>
            println(s"Benutzer '$paidByName' wurde in der Gruppe nicht gefunden.")
      case None =>
        println(s"Gruppe '$groupName' wurde nicht gefunden.")