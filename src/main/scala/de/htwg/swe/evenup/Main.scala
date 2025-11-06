package de.htwg.swe.evenup

@main def EvenUp(): Unit =
  println("Welcome to EvenUp!")
  println("Type 'help' for a list of commands.")
  
  val tui = TUI()
  var running = true
  
  while running do
    print("> ")
    val input = scala.io.StdIn.readLine()
    if input != null then
      running = tui.processInput(input)