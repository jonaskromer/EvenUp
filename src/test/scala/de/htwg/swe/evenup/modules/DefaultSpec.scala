package de.htwg.swe.evenup.modules

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.AppComponent.IApp
import de.htwg.swe.evenup.model.StateComponent.IAppState
import de.htwg.swe.evenup.control.IController
import de.htwg.swe.evenup.model.DateComponent.IDateFactory
import de.htwg.swe.evenup.model.GroupComponent.IGroupFactory
import de.htwg.swe.evenup.model.PersonComponent.IPersonFactory
import de.htwg.swe.evenup.model.StateComponent.IAppStateFactory
import de.htwg.swe.evenup.model.financial.ExpenseComponent.IExpenseFactory
import de.htwg.swe.evenup.model.financial.ShareComponent.IShareFactory
import de.htwg.swe.evenup.model.financial.TransactionComponent.ITransactionFactory
import de.htwg.swe.evenup.model.FileIOComponent.IFileIO
import de.htwg.swe.evenup.model.AppComponent.BaseAppImpl.App
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.MainMenuState

class DefaultSpec extends AnyWordSpec with Matchers:

  "Default module" should {

    "provide IAppState given" in:
      import Default.given
      val state = summon[IAppState]
      state shouldBe a[MainMenuState]

    "provide IApp given" in:
      import Default.given
      val app = summon[IApp]
      app shouldBe a[App]

    "provide IDateFactory given" in:
      import Default.given
      val factory = summon[IDateFactory]
      factory shouldBe a[IDateFactory]

    "provide IGroupFactory given" in:
      import Default.given
      val factory = summon[IGroupFactory]
      factory shouldBe a[IGroupFactory]

    "provide IPersonFactory given" in:
      import Default.given
      val factory = summon[IPersonFactory]
      factory shouldBe a[IPersonFactory]

    "provide IAppStateFactory given" in:
      import Default.given
      val factory = summon[IAppStateFactory]
      factory shouldBe a[IAppStateFactory]

    "provide IExpenseFactory given" in:
      import Default.given
      val factory = summon[IExpenseFactory]
      factory shouldBe a[IExpenseFactory]

    "provide IShareFactory given" in:
      import Default.given
      val factory = summon[IShareFactory]
      factory shouldBe a[IShareFactory]

    "provide ITransactionFactory given" in:
      import Default.given
      val factory = summon[ITransactionFactory]
      factory shouldBe a[ITransactionFactory]

    "provide IFileIO given" in:
      import Default.given
      val fileIO = summon[IFileIO]
      fileIO shouldBe a[IFileIO]
  }
