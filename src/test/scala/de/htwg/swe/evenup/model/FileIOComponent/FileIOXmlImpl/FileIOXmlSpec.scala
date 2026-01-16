package de.htwg.swe.evenup.model.FileIOComponent.FileIOXmlImpl

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.AppComponent.BaseAppImpl.App
import de.htwg.swe.evenup.model.AppComponent.IApp
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.MainMenuState
import de.htwg.swe.evenup.model.GroupComponent.BaseGroupImpl.Group
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.NormalDebtStrategy
import de.htwg.swe.evenup.modules.Default.given
import java.io.File

class FileIOXmlSpec extends AnyWordSpec with Matchers:

  "FileIO (XML)" should {

    "load returns default app when file doesn't exist" in:
      val fileIO = new FileIO
      val app = fileIO.load()
      app shouldBe a[IApp]

    "save creates an XML file" in:
      val alice    = Person("Alice")
      val bob      = Person("Bob")
      val strategy = NormalDebtStrategy()
      val group    = Group("TestGroup", List(alice, bob), Nil, Nil, strategy)
      val app      = App(List(group), None, None, MainMenuState())

      noException should be thrownBy {
        app.toXml
      }

    "load and save are consistent" in:
      val alice    = Person("Alice")
      val bob      = Person("Bob")
      val strategy = NormalDebtStrategy()
      val group    = Group("TestGroup", List(alice, bob), Nil, Nil, strategy)
      val app      = App(List(group), None, None, MainMenuState())

      val xml = app.toXml
      xml.toString should include("TestGroup")
      xml.toString should include("Alice")
      xml.toString should include("Bob")
  }
