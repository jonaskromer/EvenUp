package de.htwg.swe.evenup.model.FileIOComponent.FileIOXmlImpl

import de.htwg.swe.evenup.model.FileIOComponent.IFileIO
import de.htwg.swe.evenup.model.AppComponent.IApp
import de.htwg.swe.evenup.model.AppComponent.AppDeserializer
import de.htwg.swe.evenup.modules.Default.given
import scala.util.Try

class FileIO extends IFileIO:

  override def load(): IApp = Try(scala.xml.XML.loadFile("data/evenup_data.xml"))
    .map(AppDeserializer.fromXml)
    .getOrElse(summon[IApp])

  override def save(app: IApp): Unit =
    val xmlData = app.toXml
    scala.xml.XML.save("data/evenup_data.xml", xmlData)
