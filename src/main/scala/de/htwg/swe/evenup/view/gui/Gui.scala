package de.htwg.swe.evenup.view.gui

import scalafx.application.JFXApp3
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.Includes.eventClosureWrapperWithZeroParam
import scalafx.scene.layout.{BorderPane, HBox, Priority, Region}
import scalafx.scene.control.{Button, ProgressIndicator}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.paint.Color
import scala.compiletime.uninitialized

import de.htwg.swe.evenup.control.IController
import de.htwg.swe.evenup.util.Observer
import de.htwg.swe.evenup.util.ObservableEvent

class Gui(controller: IController) extends JFXApp3 with Observer {

  private var mainView: MainView                  = uninitialized
  private var loadingIndicator: ProgressIndicator = uninitialized
  private var menuBar: HBox                       = uninitialized
  private var borderPane: BorderPane              = uninitialized

  override def start(): Unit = {
    controller.add(this)
    createLoadingIndicator()
    mainView = new MainView(controller, loadingIndicator)
    createMenuBar()

    // Register theme change listener to refresh UI
    ThemeManager.addThemeObserver(() => {
      Platform.runLater {
        refreshTheme()
      }
    })

    stage =
      new JFXApp3.PrimaryStage {
        title = "EvenUp - Expense Tracker"
        width = 1400
        height = 900

        scene =
          new Scene {
            borderPane =
              new BorderPane {
                top = menuBar
                center = mainView.getRoot
                style = ThemeManager.getBackgroundStyle
              }
            root = borderPane

            onCloseRequest = () => controller.quit
          }
      }
  }

  private def refreshTheme(): Unit = {
    updateMenuBar()
    mainView.refreshTheme()
  }

  private def createLogo(): HBox = {
    new HBox {
      spacing = 10
      alignment = Pos.CenterLeft
      children = Seq(
        new ImageView {
          image = new Image(getClass.getResource("/images/title_image.png").toString)
          fitWidth = 150
          fitHeight = 60
          preserveRatio = true
        }
      )
    }
  }

  private def createUndoButton(): Button = {
    new Button {
      text = "⟲ Undo"
      style = ThemeManager.getButtonStyle("accent")
      onAction =
        _ => {
          loadingIndicator.visible = true
          controller.undo()
        }
    }
  }

  private def createRedoButton(): Button = {
    new Button {
      text = "⟳ Redo"
      style = ThemeManager.getButtonStyle("accent")
      onAction =
        _ => {
          loadingIndicator.visible = true
          controller.redo()
        }
    }
  }

  private def createThemeToggleButton(): Button = {
    new Button {
      text = if (ThemeManager.isDark) "☀️ Light" else "🌙 Dark"
      style = ThemeManager.getButtonStyle("secondary")
      onAction =
        _ => {
          ThemeManager.toggleTheme()
          text = if (ThemeManager.isDark) "☀️ Light" else "🌙 Dark"
        }
    }
  }

  private def createLoadingIndicator(): Unit = {
    loadingIndicator =
      new ProgressIndicator {
        style = ThemeManager.getProgressIndicatorStyle
        prefWidth = 25
        prefHeight = 25
        visible = false
      }
  }

  private def updateMenuBar(): Unit = {
    menuBar.children = Seq(
      createLogo(),
      createUndoButton(),
      createRedoButton(),
      new Region { hgrow = Priority.Always },
      createThemeToggleButton(),
      loadingIndicator
    )
    menuBar.style = ThemeManager.getMenuBarStyle
  }

  private def createMenuBar(): Unit = {
    menuBar =
      new HBox {
        padding = Insets(10)
        spacing = 20
        alignment = Pos.CenterLeft
        style = ThemeManager.getMenuBarStyle

        children = Seq(
          createLogo(),
          createUndoButton(),
          createRedoButton(),
          new Region { hgrow = Priority.Always },
          createThemeToggleButton(),
          loadingIndicator
        )
      }
  }

  override def update(event: ObservableEvent): Unit = {
    mainView.update(event)
    loadingIndicator.visible = false
  }

}
