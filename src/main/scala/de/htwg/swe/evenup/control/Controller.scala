package de.htwg.swe.evenup.control

import de.htwg.swe.evenup.model.App
import de.htwg.swe.evenup.util.Observable
import de.htwg.swe.evenup.model.Group
import de.htwg.swe.evenup.model.Person
import de.htwg.swe.evenup.model.Expense

class Controller(var app: App) extends Observable {

  def allGroups: List[Group] = app.groups

  def allUsers: List[Person] = app.groups.flatMap(_.members).distinct

  def addGroup(name: String): Unit =
    val newGroup = Group(name, Nil, Nil)
    app = app.addGroup(newGroup)
    notifyObservers

  def addPersonToGroup(group_name: String, person_name: String): Unit =
    app.findGroupByName(group_name) match
      case Some(group) =>
        val newPerson    = Person(person_name)
        val updatedGroup = group.addMember(newPerson)
        app = app.updateGroup(updatedGroup)
        notifyObservers
      case None =>
        throw new NoSuchElementException(
          s"Group '$group_name' not found."
        ) // TODO: fix error handling to scala style

  def addExpenseToGroup(
    group_name: String,
    expense_name: String,
    paid_by: Person,
    amount: Double,
    shares: Map[Person, Double]
  ): Unit =
    app.findGroupByName(group_name) match
      case Some(group) =>
        val newExpense   = Expense(expense_name, amount, "0", paid_by, shares)
        val updatedGroup = group.addExpense(newExpense)
        app = app.updateGroup(updatedGroup)
        notifyObservers
      case None =>
        throw new NoSuchElementException(
          s"Group '$group_name' not found."
        ) // TODO: fix error handling to scala style

}
