package de.htwg.swe.evenup.model.AppComponent

import de.htwg.swe.evenup.model.GroupComponent.IGroup
import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.StateComponent.IAppState
import de.htwg.swe.evenup.util.Serializable
import de.htwg.swe.evenup.util.Deserializer
import de.htwg.swe.evenup.model.AppComponent.BaseAppImpl.App
import de.htwg.swe.evenup.modules.Default.given

import scala.xml.Elem
import play.api.libs.json.Json
import play.api.libs.json.JsObject
import de.htwg.swe.evenup.model.GroupComponent.GroupDeserializer
import de.htwg.swe.evenup.model.PersonComponent.PersonDeserializer
import de.htwg.swe.evenup.model.StateComponent.AppStateDeserializer

trait IApp extends Serializable:
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

  override def toXml: Elem =
    <App>
      <Groups>
        {groups.map(_.toXml)}
      </Groups>
      {
      active_user match
        case Some(user) => <ActiveUser>{user.toXml}</ActiveUser>
        case None       => scala.xml.Null
    }
      {
      active_group match
        case Some(group) => <ActiveGroup>{group.toXml}</ActiveGroup>
        case None        => scala.xml.Null
    }
      <State>{state.toXml}</State>
    </App>

  override def toJson: JsObject = Json.obj(
    "groups"     -> groups.map(_.toJson),
    "activeUser" -> (active_user match
      case Some(user) => user.toJson
      case None       => Json.obj()),
    "activeGroup" -> (active_group match
      case Some(group) => group.toJson
      case None        => Json.obj()),
    "state" -> state.toJson
  )

object AppDeserializer extends Deserializer[IApp]:

  override def fromXml(xml: Elem): IApp = {
    val groups = (xml \ "Groups" \ "Group").map(node => GroupDeserializer.fromXml(node.asInstanceOf[Elem])).toList
    val activeUserNode = (xml \ "ActiveUser").headOption
    val activeUserOpt  =
      activeUserNode match
        case Some(node) if node.nonEmpty => Some(PersonDeserializer.fromXml((node \ "Person").head.asInstanceOf[Elem]))
        case _                           => None
    val activeGroupNode = (xml \ "ActiveGroup").headOption
    val activeGroupOpt  =
      activeGroupNode match
        case Some(node) if node.nonEmpty => Some(GroupDeserializer.fromXml((node \ "Group").head.asInstanceOf[Elem]))
        case _                           => None
    val state = AppStateDeserializer.fromXml((xml \ "State").head.asInstanceOf[Elem])
    App(groups, activeUserOpt, activeGroupOpt, state)
  }

  override def fromJson(json: JsObject): IApp = {
    val groups        = (json \ "groups").as[List[JsObject]].map(obj => GroupDeserializer.fromJson(obj))
    val activeUserOpt = (json \ "activeUser").toOption.flatMap {
      case obj: JsObject if obj.keys.nonEmpty => Some(PersonDeserializer.fromJson(obj))
      case _                                  => None
    }
    val activeGroupOpt = (json \ "activeGroup").toOption.flatMap {
      case obj: JsObject if obj.keys.nonEmpty => Some(GroupDeserializer.fromJson(obj))
      case _                                  => None
    }
    val state = AppStateDeserializer.fromJson((json \ "state").as[JsObject])
    App(groups, activeUserOpt, activeGroupOpt, state)
  }
