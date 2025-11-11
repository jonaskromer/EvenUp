package de.htwg.swe.evenup.view.tui

class Prompter {

  def promptNewGroup: Unit =
    println(
      f"Create a new group by using => ${TuiKeys.newGroup.key} ${TuiKeys.newGroup.usage} "
    )
    print(">")

  def promptAddUser: Unit =
    println(
      f"Add a user to a group by using => ${TuiKeys.addUserToGroup.key} ${TuiKeys.addUserToGroup.usage}"
    )
    print(">")

  def promptInGroup: Unit =
    println(f"Add Expense by using => ${TuiKeys.addExpense.key} ${TuiKeys.addExpense.usage}")
    print(">")

  def promptMainMenu: Unit =
    println(f"Goto group by => ${TuiKeys.gotoGroup.key} ${TuiKeys.gotoGroup.usage}")
    print(">")

  def promptInitGroup: Unit =
    println(s"This group is only initialized.\nAdd a user to a group by using => ${TuiKeys.addUserToGroup.key} ${TuiKeys.addUserToGroup.usage}\nAdd Expense by using => ${TuiKeys.addExpense.key} ${TuiKeys.addExpense.usage}")
    print(">")

}
