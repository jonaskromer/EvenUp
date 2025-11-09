package de.htwg.swe.evenup.model

final case class Date(day: Int, month: Int, year: Int):

  override def toString(): String = f"$day%02d.$month%02d.$year%04d"
