package de.htwg.swe.evenup.view.tui

class Prompter {

  def promptNewGroup: Unit = println(
    f"Create a new group by using => ${TuiKeys.newGroup.key} <group name>"
  )

  def promptAddUser: Unit = println(
    f"Add a user to a group by using => ${TuiKeys.addUserToGroup.key} <group name> <user name>"
  )

  def promptAddUserOrContinue: Unit = println(
    f"Add a user to a group by using => ${TuiKeys.addUserToGroup.key} <group name> <user name> or proceed by using => ${TuiKeys.proceed.key}"
  )

  def promptInGroup: Unit = println(
    f"Add Expense by....."
  )

  def promptMainMenu: Unit = println(f"Goto group by....")

}
