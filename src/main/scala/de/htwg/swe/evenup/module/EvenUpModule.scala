package de.htwg.swe.evenup.module

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import net.codingwell.scalaguice.ScalaModule

import de.htwg.swe.evenup.model.AppComponent.IApp
import de.htwg.swe.evenup.model.AppComponent.BaseAppImpl.App
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.MainMenuState
import de.htwg.swe.evenup.control.IController
import de.htwg.swe.evenup.control.BaseControllerImpl.Controller
import de.htwg.swe.evenup.view.tui.Tui
import de.htwg.swe.evenup.view.gui.Gui

class EvenUpModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = 
    bind[IApp].toInstance(
      App(Nil, None, None, new MainMenuState())
    )
    
    bind[IController].to[Controller]
    
    bind[Tui].asEagerSingleton()
    bind[Gui].asEagerSingleton()
}