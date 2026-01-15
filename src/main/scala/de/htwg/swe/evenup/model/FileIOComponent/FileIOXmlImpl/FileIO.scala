package de.htwg.swe.evenup.model.FileIOComponent.FileIOXmlImpl

import de.htwg.swe.evenup.model.FileIOComponent.IFileIO
import de.htwg.swe.evenup.model.AppComponent.IApp
import de.htwg.swe.evenup.model.AppComponent.AppDeserializer

class FileIO extends IFileIO:

  override def load(): IApp =
    val file = scala.xml.XML.loadFile("evenup_data.xml")
    if file == null then
      println("File not found")
      return null

    AppDeserializer.fromXml(file)

  override def save(app: IApp): Unit =
    val xmlData = app.toXml
    scala.xml.XML.save("evenup_data.xml", xmlData)
