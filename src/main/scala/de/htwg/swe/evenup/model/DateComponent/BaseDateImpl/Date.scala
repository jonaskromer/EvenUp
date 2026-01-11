package de.htwg.swe.evenup.model.DateComponent.BaseDateImpl

import de.htwg.swe.evenup.model.DateComponent.IDate

final case class Date(day: Int, month: Int, year: Int) extends IDate:

  override def toString(): String = f"$day%02d.$month%02d.$year%04d"
