package de.htwg.swe.evenup.model.StateComponent

import de.htwg.swe.evenup.control.IController
import de.htwg.swe.evenup.modules.Default.given
import de.htwg.swe.evenup.util.Deserializer
import de.htwg.swe.evenup.util.Serializable

import scala.xml.Elem
import play.api.libs.json.Json
import play.api.libs.json.JsObject

trait IAppState extends Serializable:
  def execute(controller: IController): Unit

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
  def unsupportedKey: Boolean  = false

  override def toXml: Elem =
    <AppState>
      <State>{this.getClass.getSimpleName}</State>
    </AppState>

  override def toJson: JsObject = Json.obj(
    "state" -> this.getClass.getSimpleName
  )

object AppStateDeserializer extends Deserializer[IAppState]:
  val factory: IAppStateFactory = summon[IAppStateFactory]

  override def fromXml(xml: Elem): IAppState =
    val appStateElem =
      if xml.label == "AppState" then xml else (xml \ "AppState").headOption.map(_.asInstanceOf[Elem]).getOrElse(xml)
    val name = (appStateElem \ "State").text
    factory(name)

  override def fromJson(json: JsObject): IAppState =
    val name = (json \ "state").as[String]
    factory(name)

trait IAppStateFactory:
  def apply(stateName: String): IAppState
