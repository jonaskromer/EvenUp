package de.htwg.swe.evenup.model.FileIOComponent

import de.htwg.swe.evenup.model.AppComponent.IApp

trait IFileIO:
  def load(): IApp
  def save(app: IApp): Unit
