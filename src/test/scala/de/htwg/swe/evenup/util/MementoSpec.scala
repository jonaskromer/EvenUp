package de.htwg.swe.evenup.util

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.AppComponent.IApp
import de.htwg.swe.evenup.model.AppComponent.BaseAppImpl.App
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.MainMenuState

class MementoSpec extends AnyWordSpec with Matchers:

  "Memento" should {

    "store app state" in:
      val app     = App(Nil, None, None, MainMenuState())
      val memento = Memento(app)
      memento.app shouldBe app

    "be a case class" in:
      val app      = App(Nil, None, None, MainMenuState())
      val memento1 = Memento(app)
      val memento2 = Memento(app)
      memento1 shouldBe memento2

    "store different app states independently" in:
      val app1     = App(Nil, None, None, MainMenuState())
      val app2     = App(Nil, None, None, MainMenuState())
      val memento1 = Memento(app1)
      val memento2 = Memento(app2)
      memento1.app shouldBe app1
      memento2.app shouldBe app2
  }
