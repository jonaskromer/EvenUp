package de.htwg.swe.evenup.model

final case class Date(day: Int, month: Int, year: Int):

  override def toString(): String = s"$day.$month.$year"
