package de.htwg.swe.evenup.model.FileIOComponent.FileIOJsonIpml

import de.htwg.swe.evenup.model.FileIOComponent.IFileIO
import de.htwg.swe.evenup.model.AppComponent.IApp

import scala.io.Source
import play.api.libs.json.Json
import java.io.PrintWriter
import de.htwg.swe.evenup.model.AppComponent.AppDeserializer
import play.api.libs.json.JsObject

class FileIO extends IFileIO:
  override def load(): IApp = 
    val file = Source.fromFile("evenup_data.json").getLines().mkString
    val json = Json.parse(file)

    AppDeserializer.fromJson(json.as[JsObject])

  override def save(app: IApp): Unit =
    val jsonData = app.toJson
    val jsonString = Json.prettyPrint(jsonData)
    val writer = new PrintWriter("evenup_data.json")
    writer.write(jsonString)
    writer.close()