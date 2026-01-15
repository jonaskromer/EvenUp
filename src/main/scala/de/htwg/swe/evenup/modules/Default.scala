package de.htwg.swe.evenup.modules

import de.htwg.swe.evenup.model.AppComponent.IApp
import de.htwg.swe.evenup.model.AppComponent.BaseAppImpl.App
import de.htwg.swe.evenup.model.StateComponent.IAppState
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.MainMenuState
import de.htwg.swe.evenup.control.IController
import de.htwg.swe.evenup.control.BaseControllerImpl.Controller
import de.htwg.swe.evenup.model.DateComponent.IDateFactory
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.DateFactory
import de.htwg.swe.evenup.model.GroupComponent.IGroupFactory
import de.htwg.swe.evenup.model.GroupComponent.BaseGroupImpl.GroupFactory
import de.htwg.swe.evenup.model.PersonComponent.IPersonFactory
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.PersonFactory
import de.htwg.swe.evenup.model.StateComponent.IAppStateFactory
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.AppStateFactory
import de.htwg.swe.evenup.model.financial.ExpenseComponent.IExpenseFactory
import de.htwg.swe.evenup.model.financial.ExpenseComponent.BaseExpenseImpl.ExpenseFactory
import de.htwg.swe.evenup.model.financial.ShareComponent.IShareFactory
import de.htwg.swe.evenup.model.financial.ShareComponent.BaseShareImpl.ShareFactory
import de.htwg.swe.evenup.model.financial.TransactionComponent.ITransactionFactory
import de.htwg.swe.evenup.model.financial.TransactionComponent.BaseTransactionImpl.TransactionFactory
import de.htwg.swe.evenup.model.FileIOComponent.IFileIO
//import de.htwg.swe.evenup.model.FileIOComponent.FileIOXmlImpl.FileIO
import de.htwg.swe.evenup.model.FileIOComponent.FileIOJsonIpml.FileIO

object Default:
  given IAppState   = MainMenuState()
  given IApp        = App(Nil, None, None, summon[IAppState])
  given IController = Controller(using summon[IApp])

  given IDateFactory        = DateFactory
  given IGroupFactory       = GroupFactory
  given IPersonFactory      = PersonFactory
  given IAppStateFactory    = AppStateFactory
  given IExpenseFactory     = ExpenseFactory
  given IShareFactory       = ShareFactory
  given ITransactionFactory = TransactionFactory

  given IFileIO = FileIO()
