package de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl

import de.htwg.swe.evenup.control.IController
import de.htwg.swe.evenup.model.StateComponent.IAppState

class MainMenuState extends IAppState:
  override def execute(controller: IController): Unit = {}

  override def canAddGroup: Boolean    = true
  override def canRemoveGroup: Boolean = true
  override def canGotoGroup: Boolean   = true

  override def canAddUser: Boolean    = false
  override def canRemoveUser: Boolean = false

  override def canAddExpense: Boolean    = false
  override def canRemoveExpense: Boolean = false
  override def canEditExpense: Boolean   = false

  override def canAddTransaction: Boolean    = false
  override def canRemoveTransaction: Boolean = false
  override def canEditTransaction: Boolean   = false

  override def canCalculateDebts: Boolean = false
  override def canSetStrategy: Boolean    = false

  override def canLogin: Boolean        = true
  override def canGoToMainMenu: Boolean = false
