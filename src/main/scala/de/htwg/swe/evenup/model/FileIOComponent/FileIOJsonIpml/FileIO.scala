package de.htwg.swe.evenup.model.FileIOComponent.FileIOJsonIpml

import de.htwg.swe.evenup.model.FileIOComponent.IFileIO
import de.htwg.swe.evenup.model.AppComponent.IApp

import scala.io.Source
import play.api.libs.json.Json
import java.io.PrintWriter
import de.htwg.swe.evenup.model.AppComponent.AppDeserializer
import play.api.libs.json.JsObject
import de.htwg.swe.evenup.modules.Default.given
import scala.util.Try

class FileIO extends IFileIO:

  override def load(): IApp =
    Try(Source.fromFile("data/evenup_data.json").getLines().mkString)
      .map(file => Json.parse(file).as[JsObject])
      .map(AppDeserializer.fromJson)
      .getOrElse(summon[IApp])

  override def save(app: IApp): Unit =
    val jsonData   = app.toJson
    val jsonString = Json.prettyPrint(jsonData)
    val writer     = new PrintWriter("data/evenup_data.json")
    writer.write(jsonString)
    writer.close()
