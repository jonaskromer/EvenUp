package de.htwg.swe.evenup.view.gui

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.Includes.eventClosureWrapperWithZeroParam 
import de.htwg.swe.evenup.control.IController

class Gui(controller: IController) extends JFXApp3 {
  
  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "EvenUp - Expense Tracker"
      width = 1400
      height = 900
      
      scene = new Scene {
        val mainView = new MainView(controller)
        controller.add(mainView)
        root = mainView.getRoot

        onCloseRequest = () => controller.quit
      }
    }
  }
}