package de.htwg.swe.evenup.model.state

import de.htwg.swe.evenup.control.Controller

class LoginState extends AppState:
  override def execute(controller: Controller): Unit = {}
  override def canLogin: Boolean                     = true
  override def canLogout: Boolean                    = false
  override def canAddGroup: Boolean                  = false
  override def canRemoveGroup: Boolean               = false
  override def canAddUser: Boolean                   = false
  override def canRemoveUser: Boolean                = false
  override def canAddExpense: Boolean                = false
  override def canRemoveExpense: Boolean             = false
  override def canEditExpense: Boolean               = false
  override def canAddTransaction: Boolean            = false
  override def canRemoveTransaction: Boolean         = false
  override def canEditTransaction: Boolean           = false
