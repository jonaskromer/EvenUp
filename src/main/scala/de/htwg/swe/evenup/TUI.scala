package de.htwg.swe.evenup

class TUI(var state: AppState = AppState(Nil)):

  def processInput(input: String): Boolean =
    val tokens = input.trim.split("\\s+").toList
      tokens match
        case "quit" :: Nil =>
          false
        case "help" :: Nil =>
          printHelp()
          true
        case "users" :: Nil =>
          showUsers()
          true
        case "groups" :: Nil =>
          showGroups()
          true
        case "createuser" :: username :: Nil =>
          createUser(username)
          true
        case "creategroup" :: groupname :: Nil =>
          createGroup(groupname)
          true
        case "showgroup" :: groupname :: Nil =>
          showGroup(groupname)
          true
        case "adduser" :: username :: groupname :: Nil =>
          addToGroup(username, groupname)
          true
        case "addexpense" :: expensename :: groupname :: payee :: amount :: date :: debtor :: debtpercentage :: Nil =>
          //addExpense(expensename, groupname, payee, amount, date, debtor, debtpercentage)
          println("Not yet implemented.")
          true
        case _ =>
          println("Unknown command. Please use 'help' for more information.")
          true
  
  private def printHelp(): Unit =
    println("""
            |Commands:
            |  quit                           - Closes the program
            |  help                           - Shows this help
            |  users                          - Shows all users
            |  groups                         - Shows all groups
            |  createuser [username]          - Creates a user
            |  creategroup [groupname]        - Creates a group
            |  showgroup [groupname]          - Shows details of a group
            |  adduser [username] [groupname] - Adds a user to a group
            |  addexpense [name] [groupname] [payee] [amount] [date] [debtor] [debtpercentage]  - Adds an expense to a group
            |""".stripMargin)

  private def showUsers(): Unit =
    if state.allUsers.isEmpty then
      println("No users have been created.")
    else
      println("Users:")
      state.allUsers.foreach { Person =>
        println(s" ${Person.name}")
      }

  private def showGroups(): Unit =
    if state.allGroups.isEmpty then
      println("No groups have been created.")
    else
      println("Groups:")
      state.allGroups.foreach { group =>
        println(s" ${group.name}")
      }

  private def createUser(name: String): Unit =
    if state.findUserByName(name).isDefined then
      println("User already exists.")
    else
      val newUser = Person(name)
      state = state.addUser(newUser)
      println(s"The user ${name} has been created.")

  private def createGroup(name: String): Unit =
    if state.findGroupByName(name).isDefined then
      println("Group already exists.")
    else
      val newGroup = Group(name, Nil, Nil)
      state = state.addGroup(newGroup)
      println(s"The group ${name} has been created.")

  private def showGroup(name: String): Unit =
    state.findGroupByName(name) match
      case Some(group) =>
        group.toString()
      case None =>
        println(s"Group ${name} does not exist.")

  private def addToGroup(username: String, groupname: String): Unit =
  state.findUserByName(username) match
    case Some(user) =>
      state.findGroupByName(groupname) match
        case Some(group) =>
          val newGroup = group.addMember(user)
          state = state.updateGroup(newGroup)
        case None =>
          println(s"Group ${groupname} does not exist.")
    case None =>
      println(s"User ${username} does not exist.")