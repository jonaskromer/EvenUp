package de.htwg.swe.evenup.view.gui

import scala.compiletime.uninitialized

case class Theme(
    primaryBg: String,
    secondaryBg: String,
    surfaceBg: String,
    textPrimary: String,
    textSecondary: String,
    buttonPrimary: String,
    buttonSecondary: String,
    buttonAccent: String,
    buttonNeutral: String,
    menuBarBg: String,
    menuBarText: String,
    accentColor: String,
    borderColor: String,
    chartAxisColor: String,
    chartGridColor: String
)

object Theme {
  val Light = Theme(
    primaryBg = "#ffffff",
    secondaryBg = "#f5f5f5",
    surfaceBg = "#fafafa",
    textPrimary = "#000000",
    textSecondary = "#666666",
    buttonPrimary = "#fc5f50",
    buttonSecondary = "#ffb700",
    buttonAccent = "#301c55",
    buttonNeutral = "#95a5a6",
    menuBarBg = "#270f55",
    menuBarText = "#ffffff",
    accentColor = "#fc5f50",
    borderColor = "#cccccc",
    chartAxisColor = "#000000",
    chartGridColor = "#e0e0e0"
  )

  val Dark = Theme(
    primaryBg = "#1e1e1e",
    secondaryBg = "#2d2d2d",
    surfaceBg = "#3a3a3a",
    textPrimary = "#e0e0e0",
    textSecondary = "#a0a0a0",
    buttonPrimary = "#ff7a6a",
    buttonSecondary = "#ffb700",
    buttonAccent = "#9d4ecc",
    buttonNeutral = "#5a7a7a",
    menuBarBg = "#1a0d2e",
    menuBarText = "#e0e0e0",
    accentColor = "#ff7a6a",
    borderColor = "#444444",
    chartAxisColor = "#e0e0e0",
    chartGridColor = "#404040"
  )
}

object ThemeManager {
  private var currentTheme: Theme = Theme.Light
  private var isDarkMode: Boolean = false
  private var observers: List[() => Unit] = List()

  def getCurrentTheme: Theme = currentTheme

  def isDark: Boolean = isDarkMode

  def toggleTheme(): Unit = {
    isDarkMode = !isDarkMode
    currentTheme = if (isDarkMode) Theme.Dark else Theme.Light
    notifyObservers()
  }

  def setDarkMode(dark: Boolean): Unit = {
    isDarkMode = dark
    currentTheme = if (isDarkMode) Theme.Dark else Theme.Light
    notifyObservers()
  }

  def addThemeObserver(callback: () => Unit): Unit = {
    observers = observers :+ callback
  }

  def removeThemeObserver(callback: () => Unit): Unit = {
    observers = observers.filter(_ ne callback)
  }

  private def notifyObservers(): Unit = {
    observers.foreach(_())
  }

  def getButtonStyle(buttonType: String = "primary"): String = {
    val theme = getCurrentTheme
    buttonType match {
      case "primary" =>
        s"-fx-background-color: ${theme.buttonPrimary}; -fx-text-fill: white;"
      case "secondary" =>
        s"-fx-background-color: ${theme.buttonSecondary}; -fx-text-fill: white;"
      case "accent" =>
        s"-fx-background-color: ${theme.buttonAccent}; -fx-text-fill: white;"
      case "neutral" =>
        s"-fx-background-color: ${theme.buttonNeutral}; -fx-text-fill: white;"
      case _ => s"-fx-background-color: ${theme.buttonPrimary}; -fx-text-fill: white;"
    }
  }

  def getRoundButtonStyle(buttonType: String = "secondary"): String = {
    getButtonStyle(buttonType) + " -fx-font-size: 24px; -fx-background-radius: 25;"
  }

  def getMenuBarStyle: String = {
    val theme = getCurrentTheme
    s"-fx-background-color: ${theme.menuBarBg};"
  }

  def getBackgroundStyle: String = {
    val theme = getCurrentTheme
    s"-fx-background-color: ${theme.primaryBg}; -fx-text-fill: ${theme.textPrimary};"
  }

  def getSurfaceStyle: String = {
    val theme = getCurrentTheme
    s"-fx-control-inner-background: ${theme.surfaceBg}; -fx-text-fill: ${theme.textPrimary};"
  }

  def getLabelStyle: String = {
    val theme = getCurrentTheme
    s"-fx-text-fill: ${theme.textPrimary};"
  }

  def getBoldLabelStyle: String = {
    getLabelStyle + " -fx-font-weight: bold;"
  }

  def getLargeBoldLabelStyle: String = {
    getLabelStyle + " -fx-font-size: 18px; -fx-font-weight: bold;"
  }

  def getProgressIndicatorStyle: String = {
    val theme = getCurrentTheme
    s"-fx-accent: ${theme.accentColor};"
  }
}
