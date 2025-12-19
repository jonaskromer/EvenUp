package de.htwg.swe.evenup.view.gui

import scala.collection.mutable

object ThemeManager {
  sealed trait Theme
  case object LightTheme extends Theme
  case object DarkTheme extends Theme

  private var currentTheme: Theme = LightTheme
  private val themeObservers = mutable.ListBuffer[() => Unit]()

  def setTheme(theme: Theme): Unit = {
    if (currentTheme != theme) {
      currentTheme = theme
      notifyObservers()
    }
  }

  def getTheme: Theme = currentTheme

  def toggleTheme(): Unit = {
    currentTheme match {
      case DarkTheme  => setTheme(LightTheme)
      case LightTheme => setTheme(DarkTheme)
    }
  }

  def subscribe(observer: () => Unit): Unit = {
    themeObservers += observer
  }

  private def notifyObservers(): Unit = {
    themeObservers.foreach(_())
  }

  // Color definitions for different themes
  object Colors {
    def menuBarBackground: String = currentTheme match {
      case DarkTheme  => "#270f55"
      case LightTheme => "#270f55"
    }

    def buttonBackground: String = currentTheme match {
      case DarkTheme  => "#301c55"
      case LightTheme => "#bdc3c7"
    }

    def addButtonBackground: String = "#ffb700"

    def textColor: String = currentTheme match {
      case DarkTheme  => "#ffffff"
      case LightTheme => "#2c3e50"
    }

    def backgroundColor: String = currentTheme match {
      case DarkTheme  => "#270f55"
      case LightTheme => "#ffffff"
    }

    def alternateBackground: String = currentTheme match {
      case DarkTheme  => "#301c55"
      case LightTheme => "#ecf0f1"
    }

    def inputBackground: String = currentTheme match {
      case DarkTheme  => "#301c55"
      case LightTheme => "#ffffff"
    }

    def inputTextColor: String = currentTheme match {
      case DarkTheme  => "#ffffff"
      case LightTheme => "#2c3e50"
    }

    def borderColor: String = currentTheme match {
      case DarkTheme  => "#555555"
      case LightTheme => "#bdc3c7"
    }

    def cancelButtonBackground: String = "#95a5a6"

    def primaryButtonBackground: String = "#fc5f50"
  }

  // Helper method to generate styled text for buttons
  def buttonStyle(): String =
    s"-fx-background-color: ${Colors.buttonBackground}; -fx-text-fill: ${Colors.textColor};"

  def plusButtonStyle(): String =
    s"-fx-background-color: ${Colors.addButtonBackground}; -fx-text-fill: white;"

  def addButtonStyle(): String =
    s"-fx-font-size: 24px; -fx-background-color: ${Colors.addButtonBackground}; -fx-text-fill: white; -fx-background-radius: 25;"

  def primaryButtonStyle(): String =
    s"-fx-background-color: ${Colors.primaryButtonBackground}; -fx-text-fill: white;"

  def cancelButtonStyle(): String =
    s"-fx-background-color: ${Colors.cancelButtonBackground}; -fx-text-fill: white;"

  def menuBarStyle(): String =
    s"-fx-background-color: ${Colors.menuBarBackground};"

  def textAreaStyle(): String =
    s"-fx-control-inner-background: ${Colors.inputBackground}; -fx-text-fill: ${Colors.inputTextColor};"

  def labelStyle(fontSize: String = "14px"): String =
    s"-fx-font-size: $fontSize; -fx-text-fill: ${Colors.textColor};"

  def boldLabelStyle(fontSize: String = "14px"): String =
    s"-fx-font-size: $fontSize; -fx-font-weight: bold; -fx-text-fill: ${Colors.textColor};"

  def listViewStyle(): String =
    s"-fx-control-inner-background: ${Colors.inputBackground}; -fx-text-fill: ${Colors.inputTextColor};"

  def tabPaneStyle(): String =
    s"-fx-background-color: ${Colors.backgroundColor}; -fx-padding: 0; -fx-tab-header-area-background: ${Colors.alternateBackground};"

  def tabStyle(): String =
    s"-fx-background-color: ${Colors.alternateBackground}; -fx-text-base-color: ${Colors.textColor};"
}
