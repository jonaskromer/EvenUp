package de.htwg.swe.evenup.view

object ConsoleColors:
  // Reset
  val RESET = "\u001b[0m"

  // Standard Colors
  val BLACK   = "\u001b[30m"
  val RED     = "\u001b[31m"
  val GREEN   = "\u001b[32m"
  val YELLOW  = "\u001b[33m"
  val BLUE    = "\u001b[34m"
  val MAGENTA = "\u001b[35m"
  val CYAN    = "\u001b[36m"
  val WHITE   = "\u001b[37m"

  // Bright Colors
  val BRIGHT_BLACK   = "\u001b[90m"
  val BRIGHT_RED     = "\u001b[91m"
  val BRIGHT_GREEN   = "\u001b[92m"
  val BRIGHT_YELLOW  = "\u001b[93m"
  val BRIGHT_BLUE    = "\u001b[94m"
  val BRIGHT_MAGENTA = "\u001b[95m"
  val BRIGHT_CYAN    = "\u001b[96m"
  val BRIGHT_WHITE   = "\u001b[97m"

  // Background Colors
  val BG_BLACK   = "\u001b[40m"
  val BG_RED     = "\u001b[41m"
  val BG_GREEN   = "\u001b[42m"
  val BG_YELLOW  = "\u001b[43m"
  val BG_BLUE    = "\u001b[44m"
  val BG_MAGENTA = "\u001b[45m"
  val BG_CYAN    = "\u001b[46m"
  val BG_WHITE   = "\u001b[47m"

  // Bright Background Colors
  val BG_BRIGHT_BLACK   = "\u001b[100m"
  val BG_BRIGHT_RED     = "\u001b[101m"
  val BG_BRIGHT_GREEN   = "\u001b[102m"
  val BG_BRIGHT_YELLOW  = "\u001b[103m"
  val BG_BRIGHT_BLUE    = "\u001b[104m"
  val BG_BRIGHT_MAGENTA = "\u001b[105m"
  val BG_BRIGHT_CYAN    = "\u001b[106m"
  val BG_BRIGHT_WHITE   = "\u001b[107m"

  // Utility function to colorize text
  def colorize(text: String, color: String): String = s"$color$text$RESET"
