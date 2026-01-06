package de.htwg.swe.evenup.model.DateComponent.BaseDateImpl

import de.htwg.swe.evenup.model.DateComponent.IDate

import com.google.inject.Inject

final case class Date @Inject() (day: Int, month: Int, year: Int) extends IDate:

  override def toString(): String = f"$day%02d.$month%02d.$year%04d"
