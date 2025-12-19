package de.htwg.swe.evenup.view.gui

import scala.annotation.nowarn

import scalafx.Includes._
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout._
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx.stage.{Modality, Stage}
import scalafx.collections.ObservableBuffer
import scalafx.animation.RotateTransition
import scalafx.util.Duration
import scalafx.scene.text.{Font, FontWeight, Text}
import scalafx.scene.image.{Image, ImageView}

import de.htwg.swe.evenup.control.IController
import de.htwg.swe.evenup.control.EventResponse
import de.htwg.swe.evenup.model.GroupComponent.IGroup
import de.htwg.swe.evenup.util.Observer
import de.htwg.swe.evenup.util.ObservableEvent

@nowarn("msg=Implicit parameters should be provided with a `using` clause")
@nowarn("msg=-Wconf:msg=Implicit parameters should be provided with a `using` clause:s")
class MainView(controller: IController) extends Observer {

  private val tabPane                             = new TabPane {}
  private var groupTabs                           = Map[String, Tab]()
  private var groupListView: ListView[String]     = scala.compiletime.uninitialized
  private var searchField: TextField              = scala.compiletime.uninitialized
  private var loadingIndicator: ProgressIndicator = scala.compiletime.uninitialized

  createLoadingIndicator()

  ThemeManager.subscribe(() => {
    tabPane.tabs.foreach { tab =>
      tab.style = ThemeManager.tabStyle()
    }
    val headerBackground = tabPane.lookup(".tab-header-background")
    headerBackground.setStyle(s"-fx-background-color: ${ThemeManager.Colors.backgroundColor};")
  })

  // Top Menu Bar
  private val menuBar =
    new HBox {
      padding = Insets(10)
      spacing = 20
      alignment = Pos.CenterLeft
      style = ThemeManager.menuBarStyle()

      children = Seq(
        createLogo(),
        createUndoButton(),
        createRedoButton(),
        new Region { hgrow = Priority.Always },
        createThemeToggleButton(),
        loadingIndicator
      )
    }

  ThemeManager.subscribe(() => {
    menuBar.style = ThemeManager.menuBarStyle()
  })

  // Home Tab (unclosable)
  private val homeTab =
    new Tab {
      text = "Home"
      closable = false
      style = ThemeManager.tabStyle()
      content = createHomeView()
      onSelectionChanged =
        _ => {
          if (selected.value && searchField != null) {
            updateGroupList(searchField.text.value)
          }
        }
    }

  tabPane.tabs = Seq(homeTab)

  // Set initial tab header background
  val headerBackground = tabPane.lookup(".tab-header-background")
  if (headerBackground != null) {
    headerBackground.setStyle(s"-fx-background-color: ${ThemeManager.Colors.backgroundColor};")
  }

  private val root =
    new BorderPane {
      top = menuBar
      center = tabPane
    }

  def getRoot: BorderPane = root

  private def createLogo(): HBox = {
    new HBox {
      spacing = 10
      alignment = Pos.CenterLeft
      children = Seq(
        new ImageView {
          image = new Image(getClass.getResource("/images/title_image.png").toString)
          fitWidth = 200
          fitHeight = 80
          preserveRatio = true
        }
      )
    }
  }

  private def createUndoButton(): Button = {
    new Button {
      text = "↶ Undo"
      style = ThemeManager.buttonStyle()
      onAction =
        _ => {
          loadingIndicator.visible = true
          controller.undo()
        }
    }
  }

  private def createRedoButton(): Button = {
    new Button {
      text = "↷ Redo"
      style = ThemeManager.buttonStyle()
      onAction =
        _ => {
          loadingIndicator.visible = true
          controller.redo()
        }
    }
  }

  private def createThemeToggleButton(): Button = {
    new Button {
      text = "🌓"
      style = ThemeManager.buttonStyle()
      onAction =
        _ => {
          ThemeManager.toggleTheme()
          updateAllViewsForThemeChange()
        }
    }
  }

  private def updateAllViewsForThemeChange(): Unit = {
    if (homeTab.content != null) {
      homeTab.content = createHomeView()
    }
    groupTabs.foreach { case (groupName, _) =>
      controller.app.getGroup(groupName).foreach { group =>
        groupTabs(groupName).content = new GroupView(controller, group, loadingIndicator).getRoot
      }
    }
  }

  private def createLoadingIndicator(): Unit = {
    loadingIndicator =
      new ProgressIndicator {
        style = "-fx-accent: white;"
        prefWidth = 25
        prefHeight = 25
        visible = false
      }
  }

  private def createHomeView(): VBox = {
    searchField =
      new TextField {
        promptText = "Search groups..."
        prefWidth = 400
      }

    val searchBtn =
      new Button {
        text = "Search"
        style = ThemeManager.primaryButtonStyle()
        onAction = _ => updateGroupList(searchField.text.value)
      }

    val addGroupBtn =
      new Button {
        text = "+"
        style = ThemeManager.addButtonStyle()
        prefWidth = 50
        prefHeight = 50
        onAction = _ => showAddGroupDialog()
      }

    val searchBox =
      new HBox {
        padding = Insets(20)
        spacing = 10
        alignment = Pos.CenterLeft
        style = s"-fx-background-color: ${ThemeManager.Colors.backgroundColor};"
        children = Seq(searchField, searchBtn, new Region { hgrow = Priority.Always }, addGroupBtn)
      }

    groupListView =
      new ListView[String] {
        items = ObservableBuffer[String]()
        prefHeight = 600
        style = ThemeManager.listViewStyle()
      }

    val openGroupBtn =
      new Button {
        text = "Open Group"
        style = ThemeManager.primaryButtonStyle()
        prefWidth = 150
        prefHeight = 40
        onAction =
          _ => {
            val selected = groupListView.selectionModel().selectedItem.value
            if (selected != null && !selected.isEmpty) {
              val groupName = selected.split(" - ")(0)
              openGroupTab(groupName)
            }
          }
      }

    val listContainer =
      new HBox {
        padding = Insets(20)
        spacing = 10
        style = s"-fx-background-color: ${ThemeManager.Colors.backgroundColor};"
        children = Seq(
          new VBox {
            children = Seq(groupListView)
            hgrow = Priority.Always
          },
          new VBox {
            spacing = 10
            alignment = Pos.Center
            children = Seq(openGroupBtn)
          }
        )
      }

    updateGroupList()

    new VBox {
      spacing = 10
      style = s"-fx-background-color: ${ThemeManager.Colors.backgroundColor};"
      children = Seq(searchBox, listContainer)
    }
  }

  private def updateGroupList(filter: String = ""): Unit = {
    Platform.runLater {
      val allGroups      = controller.app.allGroups
      val filteredGroups =
        if (filter.isEmpty)
          allGroups
        else
          allGroups.filter(_.name.toLowerCase.contains(filter.toLowerCase))
      val items = filteredGroups.map { group =>
        s"${group.name} - ${group.members.length} users - ${group.expenses.length} expenses"
      }
      groupListView.items = ObservableBuffer(items*)
    }
  }

  private def showAddGroupDialog(): Unit = {
    val dialog =
      new Stage {
        initModality(Modality.ApplicationModal)
        title = "Add New Group"
        width = 400
        height = 200
        onCloseRequest = _ => loadingIndicator.visible = false
      }

    val nameField =
      new TextField {
        promptText = "Group name"
        prefWidth = 300
      }

    val addBtn =
      new Button {
        text = "Add Group"
        style = ThemeManager.plusButtonStyle()
        onAction =
          _ => {
            if (!nameField.text.value.isEmpty) {
              loadingIndicator.visible = true
              controller.addGroup(nameField.text.value)
              dialog.close()
            }
          }
      }

    val cancelBtn =
      new Button {
        text = "Cancel"
        style = ThemeManager.cancelButtonStyle()
        onAction =
          _ => {
            loadingIndicator.visible = false
            dialog.close()
          }
      }

    dialog.scene =
      new Scene {
        content =
          new VBox {
            padding = Insets(20)
            spacing = 20
            alignment = Pos.Center
            style = s"-fx-background-color: ${ThemeManager.Colors.backgroundColor};"
            children = Seq(
              new Label("Enter group name:") {
                style = ThemeManager.labelStyle()
              },
              nameField,
              new HBox {
                spacing = 10
                alignment = Pos.Center
                children = Seq(addBtn, cancelBtn)
              }
            )
          }
      }

    loadingIndicator.visible = true
    dialog.show()
  }

  private def openGroupTab(groupName: String): Unit = {
    controller.gotoGroup(groupName)

    if (!groupTabs.contains(groupName)) {
      val groupTab =
        new Tab {
          text = groupName
          closable = true
          style = ThemeManager.tabStyle()
          onClosed =
            _ => {
              groupTabs -= groupName
              controller.gotoMainMenu
            }
          onSelectionChanged =
            _ => {
              if (selected.value) {
                controller.gotoGroup(groupName)
              }
            }
        }

      controller.app.getGroup(groupName).foreach { group =>
        groupTab.content = new GroupView(controller, group, loadingIndicator).getRoot
      }

      groupTabs += (groupName -> groupTab)
      tabPane.tabs.add(groupTab)
    }

    groupTabs.get(groupName).foreach { tab =>
      tabPane.selectionModel().select(tab)
    }
  }

  override def update(event: ObservableEvent): Unit = {
    Platform.runLater {
      event match {
        case EventResponse.AddGroup(result, group) =>
          homeTab.content = createHomeView()
          loadingIndicator.visible = false

        case EventResponse.GotoGroup(result, group) =>
          groupTabs.get(group.name).foreach { tab =>
            tab.content = new GroupView(controller, group, loadingIndicator).getRoot
          }

        case EventResponse.AddUserToGroup(result, user, updatedGroup) =>
          groupTabs.get(updatedGroup.name).foreach { tab =>
            tab.content = new GroupView(controller, updatedGroup, loadingIndicator).getRoot
          }
          loadingIndicator.visible = false

        case EventResponse.AddExpenseToGroup(result, expense) =>
          controller.app.active_group.foreach { group =>
            groupTabs.get(group.name).foreach { tab =>
              tab.content = new GroupView(controller, group, loadingIndicator).getRoot
            }
          }
          loadingIndicator.visible = false

        case EventResponse.CalculateDebts(result, debts) =>
          controller.app.active_group.foreach { group =>
            groupTabs.get(group.name).foreach { tab =>
              tab.content = new GroupView(controller, group, loadingIndicator).getRoot
            }
          }
          loadingIndicator.visible = false

        case EventResponse.SetDebtStrategy(result, strategy) =>
          controller.app.active_group.foreach { group =>
            groupTabs.get(group.name).foreach { tab =>
              tab.content = new GroupView(controller, group, loadingIndicator).getRoot
            }
          }

        case EventResponse.Undo(result, stackSize) =>
          homeTab.content = createHomeView()
          controller.app.active_group.foreach { group =>
            groupTabs.get(group.name).foreach { tab =>
              tab.content = new GroupView(controller, group, loadingIndicator).getRoot
            }
          }
          loadingIndicator.visible = false

        case EventResponse.Redo(result, stackSize) =>
          homeTab.content = createHomeView()
          controller.app.active_group.foreach { group =>
            groupTabs.get(group.name).foreach { tab =>
              tab.content = new GroupView(controller, group, loadingIndicator).getRoot
            }
          }
          loadingIndicator.visible = false

        case EventResponse.MainMenu =>
          tabPane.tabs.clear()
          tabPane.tabs.add(homeTab)
          groupTabs = Map.empty
          homeTab.content = createHomeView()
          tabPane.selectionModel().select(homeTab)

        case _ => println(s"Unhandled event: $event")
      }
    }
  }

}
