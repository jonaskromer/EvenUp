package de.htwg.swe.evenup.model.state

import de.htwg.swe.evenup.control.Controller
import scala.compiletime.ops.boolean

trait AppState:
  def execute(controller: Controller): Unit

  def canLogin: Boolean  = false
  def canLogout: Boolean = true

  def canAddGroup: Boolean
  def canRemoveGroup: Boolean
  def canGotoGroup: Boolean

  def canAddUser: Boolean
  def canRemoveUser: Boolean

  def canAddExpense: Boolean
  def canRemoveExpense: Boolean
  def canEditExpense: Boolean

  def canAddTransaction: Boolean
  def canRemoveTransaction: Boolean
  def canEditTransaction: Boolean

  def canCalculateDebts: Boolean
  def canSetStrategy: Boolean

  def canShowHelp: Boolean = true
  def canQuit: Boolean     = true
  def canUndo: Boolean     = true
  def canRedo: Boolean     = true

  def canGoToMainMenu: Boolean = true
