package de.htwg.swe.evenup.model.AppComponent

import de.htwg.swe.evenup.model.GroupComponent.IGroup
import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.StateComponent.IAppState

trait IApp:
  val groups: List[IGroup]
  val active_user: Option[IPerson]
  val active_group: Option[IGroup]
  val state: IAppState

  def allGroups: List[IGroup]
  def allUsers: List[IPerson]

  def addGroup(group: IGroup): IApp
  def updateGroup(updatedGroup: IGroup): IApp
  def updateActiveGroup(active_group: Option[IGroup]): IApp
  def updateAppState(state: IAppState): IApp

  def containsGroup(group_name: String): Boolean
  def getGroup(group_name: String): Option[IGroup]
