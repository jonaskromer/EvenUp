package de.htwg.swe.evenup.model.AppComponent

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date
import de.htwg.swe.evenup.model.financial.ShareComponent.BaseShareImpl.Share
import de.htwg.swe.evenup.model.financial.ExpenseComponent.BaseExpenseImpl.Expense
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.NormalDebtStrategy
import de.htwg.swe.evenup.model.GroupComponent.BaseGroupImpl.Group
import de.htwg.swe.evenup.model.AppComponent.BaseAppImpl.App
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.MainMenuState
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.InGroupState
import play.api.libs.json.Json

class IAppSpec extends AnyWordSpec with Matchers:

  val alice    = Person("Alice")
  val bob      = Person("Bob")
  val strategy = NormalDebtStrategy()
  val group    = Group("Trip", List(alice, bob), Nil, Nil, strategy)
  val state    = MainMenuState()

  "IApp" should {

    "have groups property" in:
      val app: IApp = App(List(group), Some(alice), None, state)
      app.groups shouldBe List(group)

    "have active_user property" in:
      val app: IApp = App(List(group), Some(alice), None, state)
      app.active_user shouldBe Some(alice)

    "have active_group property" in:
      val app: IApp = App(List(group), Some(alice), Some(group), state)
      app.active_group shouldBe Some(group)

    "have state property" in:
      val app: IApp = App(List(group), Some(alice), None, state)
      app.state shouldBe a[MainMenuState]

    "serialize to XML correctly" in:
      val app = App(List(group), Some(alice), None, state)
      val xml = app.toXml
      xml.label shouldBe "App"
      (xml \ "Groups" \ "Group" \ "Name").text shouldBe "Trip"
      (xml \ "ActiveUser" \ "Person" \ "Name").text shouldBe "Alice"

    "serialize to JSON correctly" in:
      val app  = App(List(group), Some(alice), None, state)
      val json = app.toJson
      (json \ "groups").as[Seq[play.api.libs.json.JsObject]].length shouldBe 1
      (json \ "activeUser" \ "name").as[String] shouldBe "Alice"

    "serialize without active user correctly" in:
      val app  = App(List(group), None, None, state)
      val json = app.toJson
      // When no active user, the JSON may contain an empty object or be absent
      val activeUser = (json \ "activeUser").asOpt[play.api.libs.json.JsObject]
      activeUser.forall(_.keys.isEmpty) shouldBe true

    "serialize with active group correctly" in:
      val app  = App(List(group), Some(alice), Some(group), state)
      val json = app.toJson
      (json \ "activeGroup" \ "name").as[String] shouldBe "Trip"

    "serialize state correctly" in:
      val app  = App(List(group), Some(alice), None, state)
      val json = app.toJson
      (json \ "state" \ "state").as[String] should include("MainMenuState")
  }

  "AppDeserializer" should {

    "deserialize from JSON correctly" in:
      val json = Json.obj(
        "groups" -> Json.arr(
          Json.obj(
            "name"                    -> "Party",
            "members"                 -> Json.arr(Json.obj("name" -> "Charlie")),
            "expenses"                -> Json.arr(),
            "transactions"            -> Json.arr(),
            "debtCalculationStrategy" -> Json.obj("type" -> "NormalDebtStrategy")
          )
        ),
        "activeUser"  -> Json.obj("name" -> "Charlie"),
        "activeGroup" -> Json.obj(
          "name"                    -> "Party",
          "members"                 -> Json.arr(Json.obj("name" -> "Charlie")),
          "expenses"                -> Json.arr(),
          "transactions"            -> Json.arr(),
          "debtCalculationStrategy" -> Json.obj("type" -> "NormalDebtStrategy")
        ),
        "state" -> Json.obj("state" -> "InGroupState")
      )
      val app = AppDeserializer.fromJson(json)
      app.groups.length shouldBe 1
      app.groups.head.name shouldBe "Party"
      app.active_user.get.name shouldBe "Charlie"
      app.active_group.get.name shouldBe "Party"
      app.state shouldBe a[InGroupState]

    "deserialize without active user from JSON" in:
      val json = Json.obj(
        "groups" -> Json.arr(),
        "state"  -> Json.obj("state" -> "MainMenuState")
      )
      val app = AppDeserializer.fromJson(json)
      app.active_user shouldBe None
      app.active_group shouldBe None

    "roundtrip JSON serialization correctly" in:
      val date         = Date(15, 6, 2025)
      val expense      = Expense("Dinner", 30.0, date, alice, List(Share(bob, 15.0)))
      val group2       = Group("Party", List(alice, bob), List(expense), Nil, strategy)
      val original     = App(List(group2), Some(alice), Some(group2), InGroupState())
      val json         = original.toJson
      val deserialized = AppDeserializer.fromJson(json)
      deserialized.groups.length shouldBe original.groups.length
      deserialized.groups.head.name shouldBe original.groups.head.name
      deserialized.active_user.get.name shouldBe original.active_user.get.name
  }
