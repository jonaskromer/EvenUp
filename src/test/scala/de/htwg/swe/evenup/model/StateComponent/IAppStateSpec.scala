package de.htwg.swe.evenup.model.StateComponent

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.MainMenuState
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.InGroupState
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.InEmptyGroupState
import play.api.libs.json.Json

class IAppStateSpec extends AnyWordSpec with Matchers:

  "IAppState" should {

    "have default canLogin as false" in:
      // InGroupState has canLogin = false (overrides)
      val state: IAppState = InGroupState()
      state.canLogin shouldBe false

    "have default canLogout as true" in:
      val state: IAppState = MainMenuState()
      state.canLogout shouldBe true

    "have default canShowHelp as true" in:
      val state: IAppState = MainMenuState()
      state.canShowHelp shouldBe true

    "have default canQuit as true" in:
      val state: IAppState = InGroupState()
      state.canQuit shouldBe true

    "have default canUndo as true" in:
      val state: IAppState = InEmptyGroupState()
      state.canUndo shouldBe true

    "have default canRedo as true" in:
      val state: IAppState = MainMenuState()
      state.canRedo shouldBe true

    "have default canGoToMainMenu as true" in:
      val state: IAppState = InGroupState()
      state.canGoToMainMenu shouldBe true

    "have default unsupportedKey as false" in:
      val state: IAppState = MainMenuState()
      state.unsupportedKey shouldBe false

    "serialize MainMenuState to XML correctly" in:
      val state: IAppState = MainMenuState()
      val xml              = state.toXml
      xml.label shouldBe "AppState"
      (xml \ "State").text should include("MainMenuState")

    "serialize MainMenuState to JSON correctly" in:
      val state: IAppState = MainMenuState()
      val json             = state.toJson
      (json \ "state").as[String] should include("MainMenuState")

    "serialize InGroupState to XML correctly" in:
      val state: IAppState = InGroupState()
      val xml              = state.toXml
      (xml \ "State").text should include("InGroupState")

    "serialize InGroupState to JSON correctly" in:
      val state: IAppState = InGroupState()
      val json             = state.toJson
      (json \ "state").as[String] should include("InGroupState")

    "serialize InEmptyGroupState to XML correctly" in:
      val state: IAppState = InEmptyGroupState()
      val xml              = state.toXml
      (xml \ "State").text should include("InEmptyGroupState")

    "serialize InEmptyGroupState to JSON correctly" in:
      val state: IAppState = InEmptyGroupState()
      val json             = state.toJson
      (json \ "state").as[String] should include("InEmptyGroupState")
  }

  "AppStateDeserializer" should {

    "deserialize MainMenuState from XML correctly" in:
      val xml   = <AppState><State>MainMenuState</State></AppState>
      val state = AppStateDeserializer.fromXml(xml)
      state shouldBe a[MainMenuState]

    "deserialize InGroupState from XML correctly" in:
      val xml   = <AppState><State>InGroupState</State></AppState>
      val state = AppStateDeserializer.fromXml(xml)
      state shouldBe a[InGroupState]

    "deserialize InEmptyGroupState from XML correctly" in:
      val xml   = <AppState><State>InEmptyGroupState</State></AppState>
      val state = AppStateDeserializer.fromXml(xml)
      state shouldBe a[InEmptyGroupState]

    "deserialize MainMenuState from JSON correctly" in:
      val json  = Json.obj("state" -> "MainMenuState")
      val state = AppStateDeserializer.fromJson(json)
      state shouldBe a[MainMenuState]

    "deserialize InGroupState from JSON correctly" in:
      val json  = Json.obj("state" -> "InGroupState")
      val state = AppStateDeserializer.fromJson(json)
      state shouldBe a[InGroupState]

    "deserialize InEmptyGroupState from JSON correctly" in:
      val json  = Json.obj("state" -> "InEmptyGroupState")
      val state = AppStateDeserializer.fromJson(json)
      state shouldBe a[InEmptyGroupState]

    "default to MainMenuState for unknown state" in:
      val json  = Json.obj("state" -> "UnknownState")
      val state = AppStateDeserializer.fromJson(json)
      state shouldBe a[MainMenuState]

    "roundtrip XML serialization correctly" in:
      val original: IAppState = InGroupState()
      val xml                 = original.toXml
      val deserialized        = AppStateDeserializer.fromXml(xml)
      deserialized shouldBe a[InGroupState]

    "roundtrip JSON serialization correctly" in:
      val original: IAppState = InEmptyGroupState()
      val json                = original.toJson
      val deserialized        = AppStateDeserializer.fromJson(json)
      deserialized shouldBe a[InEmptyGroupState]
  }
