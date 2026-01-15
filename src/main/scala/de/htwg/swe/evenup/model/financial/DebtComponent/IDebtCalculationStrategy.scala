package de.htwg.swe.evenup.model.financial.DebtComponent

import de.htwg.swe.evenup.model.GroupComponent.IGroup
import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.util.Serializable
import de.htwg.swe.evenup.modules.Default.given

import scala.xml.Elem
import play.api.libs.json.Json
import play.api.libs.json.JsObject

trait IDebtCalculationStrategy extends Serializable:
  def calculateDebts(group: IGroup): List[IDebt]
  def calculateBalances(group: IGroup): Map[IPerson, Double]

  override def toXml: Elem =
    <DebtCalculationStrategy>
      <Type>{this.getClass.getSimpleName}</Type>
    </DebtCalculationStrategy>

  override def toJson: JsObject = Json.obj(
    "type" -> this.getClass.getSimpleName
  )
