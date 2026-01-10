package de.htwg.swe.evenup.modules

import de.htwg.swe.evenup.model.AppComponent.IApp
import de.htwg.swe.evenup.model.AppComponent.BaseAppImpl.App
import de.htwg.swe.evenup.model.StateComponent.IAppState
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.MainMenuState
import de.htwg.swe.evenup.control.IController
import de.htwg.swe.evenup.control.BaseControllerImpl.Controller

object Default:
  given IAppState = MainMenuState()
  given IApp = App(Nil, None, None, summon[IAppState])
  given IController = Controller(using summon[IApp])