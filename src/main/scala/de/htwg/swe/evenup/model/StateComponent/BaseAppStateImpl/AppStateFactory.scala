package de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl

import de.htwg.swe.evenup.model.StateComponent.IAppState
import de.htwg.swe.evenup.model.StateComponent.IAppStateFactory

object AppStateFactory extends IAppStateFactory:

  def apply(stateName: String): IAppState =
    stateName match
      case "MainMenuState"     => MainMenuState()
      case "InGroupState"      => InGroupState()
      case "InEmptyGroupState" => InEmptyGroupState()
      case _                   => MainMenuState()
