package de.htwg.swe.evenup.model.DateComponent.BaseDateImpl

import de.htwg.swe.evenup.model.DateComponent.IDate
import de.htwg.swe.evenup.model.DateComponent.IDateFactory

final case class Date(day: Int, month: Int, year: Int) extends IDate:

  override def toString(): String = f"$day%02d.$month%02d.$year%04d"

object DateFactory extends IDateFactory:
  override
  def apply(day: Int, month: Int, year: Int): IDate = Date(day, month, year)
