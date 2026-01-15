package de.htwg.swe.evenup.control

import de.htwg.swe.evenup.util.Observable
import de.htwg.swe.evenup.model.AppComponent.IApp
import de.htwg.swe.evenup.control.BaseControllerImpl.ArgsHandler
import de.htwg.swe.evenup.model.DateComponent.IDate

trait IController extends Observable {
  var app: IApp
  val argsHandler: ArgsHandler

  def undo(): Unit
  def redo(): Unit
  def quit: Unit
  def load(): Unit

  def gotoMainMenu: Unit
  def gotoGroup(group_name: String): Unit
  def addGroup(group_name: String): Unit
  def addUserToGroup(user_name: String): Unit

  def addExpenseToGroup(
    expense_name: String,
    paid_by: String,
    amount: Double,
    date: IDate,
    shares: Option[String] = None
  ): Unit

  def setDebtStrategy(strategy: String): Unit
  def calculateDebts(): Unit
}
