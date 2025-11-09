package de.htwg.swe.evenup.view.tui

class Prompter {

  def promptNewGroup: Unit =
    println(
      f"Create a new group by using => ${TuiKeys.newGroup.key} <group name>"
    )
    print(">")

  def promptAddUser: Unit =
    println(
      f"Add a user to a group by using => ${TuiKeys.addUserToGroup.key} <group name> <user name>"
    )
    print(">")

  def promptAddUserOrContinue: Unit =
    println(
      f"Add a user to a group by using => ${TuiKeys.addUserToGroup.key} <group name> <user name> or proceed by using => ${TuiKeys.proceed.key}"
    )
    print(">")

  def promptInGroup: Unit =
    println(f"Add Expense by.....")
    print(">")

  def promptMainMenu: Unit =
    println(f"Goto group by....")
    print(">")

  def promptInitGroup: Unit =
    println(s"This group is only initialized. add members or expenses...")
    print(">")

}
