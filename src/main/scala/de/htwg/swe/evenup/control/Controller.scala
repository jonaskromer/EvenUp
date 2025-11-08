package de.htwg.swe.evenup.control

import de.htwg.swe.evenup.model.App
import de.htwg.swe.evenup.util.{Observable, ObservableEvent}
import de.htwg.swe.evenup.model.Group
import de.htwg.swe.evenup.model.Person
import de.htwg.swe.evenup.model.Expense
import de.htwg.swe.evenup.model.Date

enum ControllerEvent extends ObservableEvent:
  case Quit
  case MainMenu
  case NewGroupCreated
  case PersonAddedToGroup
  case ExpenseAddedToGroup
  case GroupCreationFinished

class Controller(var app: App) extends Observable {

  def quit: Unit =
    // TODO: save state
    notifyObservers(ControllerEvent.Quit)

  def allGroups: List[Group] = app.groups

  def allUsers: List[Person] = app.groups.flatMap(_.members).distinct

  def finishGroupSetup: Unit = notifyObservers(
    ControllerEvent.GroupCreationFinished
  )

  def gotoMainMenu: Unit =
    app.updateActiveGroup(None)
    notifyObservers(ControllerEvent.MainMenu)

  def addGroup(name: String): Unit =
    val newGroup = Group(name, Nil, Nil)
    app = app.addGroup(newGroup)
    app = app.updateActiveGroup(Some(name))
    notifyObservers(ControllerEvent.NewGroupCreated)

  def addPersonToGroup(group_name: String, person_name: String): Unit =
    app.findGroupByName(group_name) match
      case Some(group) =>
        val newPerson    = Person(person_name)
        val updatedGroup = group.addMember(newPerson)
        app = app.updateGroup(updatedGroup)
        notifyObservers(ControllerEvent.PersonAddedToGroup)
      case None => println("Group not found.")

  def addExpenseToGroup(
    group_name: String,
    expense_name: String,
    paid_by: Person,
    amount: Double,
    date: Date,
    shares: Map[Person, Double]
  ): Unit =
    app.findGroupByName(group_name) match
      case Some(group) =>
        val newExpense   = Expense(expense_name, amount, date, paid_by, shares)
        val updatedGroup = group.addExpense(newExpense)
        app = app.updateGroup(updatedGroup)
        notifyObservers(ControllerEvent.ExpenseAddedToGroup)
      case None => println("Group not found.")

}
