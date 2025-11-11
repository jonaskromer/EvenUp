package de.htwg.swe.evenup.control

import de.htwg.swe.evenup.model.App
import de.htwg.swe.evenup.util.{Observable, ObservableEvent}
import de.htwg.swe.evenup.model.Group
import de.htwg.swe.evenup.model.Person
import de.htwg.swe.evenup.model.Expense
import de.htwg.swe.evenup.model.Date
import de.htwg.swe.evenup.model.Share

enum ControllerEvent extends ObservableEvent:
  case Quit
  case MainMenu
  case GroupInitialized
  case PersonAddedToGroup
  case ExpenseAddedToGroup
  case InGroup

class Controller(var app: App) extends Observable {

  def quit: Unit =
    // TODO: save state
    notifyObservers(ControllerEvent.Quit)

  def allGroups: List[Group] = app.groups

  def allUsers: List[Person] = app.groups.flatMap(_.members).distinct

  def gotoMainMenu: Unit =
    app.updateActiveGroup(None)
    notifyObservers(ControllerEvent.MainMenu)

  def gotoGroup(group: Group): Unit =
    app.findGroup(group) match
      case Some(group) =>
        app = app.updateActiveGroup(Some(group))
        if group.members.length == 1 then
          notifyObservers(ControllerEvent.GroupInitialized)
        else notifyObservers(ControllerEvent.InGroup)
      case None => println("Group not found.")

  def addGroup(group: Group): Unit =
    app = app.addGroup(group)
    app = app.updateActiveGroup(Some(group))
    notifyObservers(ControllerEvent.GroupInitialized)

  def addPersonToGroup(person: Person): Unit =
    app.active_group match
      case Some(group) =>
        val updatedGroup = app
          .findGroup(group)
          .get
          .addMember(person)
        app = app.updateGroup(updatedGroup)
        notifyObservers(ControllerEvent.PersonAddedToGroup)
      case None => println("Group not found.")

  def addExpenseToGroup(
    expense_name: String,
    paid_by: String,
    amount: Double,
    date: Date = Date(1,1,2000),
    shares: Option[List[Share]] = None
  ): Unit =
    app.active_group match
      case Some(active_group) => 
        val group = app.findGroup(active_group).get
        shares match
          case Some(share) =>
            val newExpense   = Expense(expense_name, amount, date, Person(paid_by), share)
            val updatedGroup = group.addExpense(newExpense)
            app = app.updateGroup(updatedGroup)
            notifyObservers(ControllerEvent.ExpenseAddedToGroup)
          case None => 
            val n = group.members.length
            val total = BigDecimal(amount).setScale(2, BigDecimal.RoundingMode.HALF_UP)
            val baseShare = (total / n).setScale(2, BigDecimal.RoundingMode.DOWN)
            val initialShares = group.members.map(_ -> baseShare).toMap
            val totalRounded = initialShares.values.sum
            val remainder = total - totalRounded
            val adjustedShares =
              if remainder == 0 then initialShares
              else
                val firstMember = group.members.head
                initialShares.updated(firstMember, initialShares(firstMember) + remainder)
            val evenShare = adjustedShares.map { case (person, share) => Share(person, share.toDouble) }.toList
            val newExpense   = Expense(expense_name, amount, date, Person(paid_by), evenShare)
            val updatedGroup = group.addExpense(newExpense)
            app = app.updateGroup(updatedGroup)
            notifyObservers(ControllerEvent.ExpenseAddedToGroup)
      case None => println("No active group.")

}
