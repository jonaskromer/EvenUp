package de.htwg.swe.evenup.view.gui

import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import de.htwg.swe.evenup.control.*
import de.htwg.swe.evenup.util.{Observer, ObservableEvent}
import de.htwg.swe.evenup.model.Group
import scala.compiletime.uninitialized

class Gui(controller: Controller) extends Observer with JFXApp3:

  controller.add(this)
  private var mainStage: JFXApp3.PrimaryStage = uninitialized

  override def start(): Unit =
    mainStage = new JFXApp3.PrimaryStage {
      title = "EvenUp - split expenses with friends"
      width = 800
      height = 600
    }
    showGroupsPage()
    stage = mainStage

  override def update(event: ObservableEvent): Unit =
    Platform.runLater {
      event match
        case EventResponse.GotoGroup(GotoGroupResult.Success, group) =>
          showGroupDetailPage(group)
        case EventResponse.GotoGroup(GotoGroupResult.SuccessEmptyGroup, group) =>
          showGroupDetailPage(group)
        case EventResponse.MainMenu =>
          showGroupsPage()
        case _ =>
    }

  def showGroupsPage(): Unit =
    mainStage.scene = new Scene {
    content = new GroupsPage(controller, Gui.this)
    }

  def showGroupDetailPage(group: Group): Unit =
    mainStage.scene = new Scene {
    content = new GroupDetailPage(controller, Gui.this, group)
    }

object GuiApp:
  def apply(controller: Controller): Gui = new Gui(controller)
  def startGui(controller: Controller): Unit =
    val gui = new Gui(controller)
    gui.main(Array())