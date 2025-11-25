package de.htwg.swe.evenup.model.state

import de.htwg.swe.evenup.control.Controller

class MainMenuState extends AppState:
  override def execute(controller: Controller): Unit = {}
  override def canAddGroup: Boolean                  = true
  override def canRemoveGroup: Boolean               = true // what if no group is there
  override def canAddUser: Boolean                   = false
  override def canRemoveUser: Boolean                = false
  override def canAddExpense: Boolean                = false
  override def canRemoveExpense: Boolean             = false
  override def canEditExpense: Boolean               = false
  override def canAddTransaction: Boolean            = false
  override def canRemoveTransaction: Boolean         = false
  override def canEditTransaction: Boolean           = false
