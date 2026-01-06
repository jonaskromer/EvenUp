package de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl

import de.htwg.swe.evenup.control.IController
import de.htwg.swe.evenup.model.StateComponent.IAppState

import com.google.inject.Inject

class InGroupState @Inject() extends IAppState:
  override def execute(controller: IController): Unit = {}

  override def canAddGroup: Boolean    = false
  override def canRemoveGroup: Boolean = false
  override def canGotoGroup: Boolean   = false

  override def canAddUser: Boolean    = true
  override def canRemoveUser: Boolean = true

  override def canAddExpense: Boolean    = true
  override def canRemoveExpense: Boolean = true
  override def canEditExpense: Boolean   = true

  override def canAddTransaction: Boolean    = true
  override def canRemoveTransaction: Boolean = true
  override def canEditTransaction: Boolean   = true

  override def canCalculateDebts: Boolean = true
  override def canSetStrategy: Boolean    = true

  override def canLogin: Boolean = false
