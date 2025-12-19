package de.htwg.swe.evenup.view.tui

import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.financial.ShareComponent.BaseShareImpl.Share

object ShareParser:
  
  enum ParseError:
    case InvalidFormat(input: String)
    case InvalidAmount(input: String, reason: String)
    case EmptyPersonName(input: String)
    case NegativeAmount(person: String, amount: Double)
    case ShareSumMismatch(shareSum: Double, totalAmount: Double)
    case PersonNotInGroup(persons: List[String])
  
  def parseShares(input: String): Either[ParseError, List[Share]] =
    if input.trim.isEmpty then
      Left(ParseError.InvalidFormat("Empty input"))
    else
      val parts = input.split("_").toList
      parseSharesList(parts)
  
  private def parseSharesList(parts: List[String]): Either[ParseError, List[Share]] =
    parts.foldLeft[Either[ParseError, List[Share]]](Right(List.empty)) { (acc, part) =>
      for
        shares <- acc
        share  <- parseSingleShare(part)
      yield shares :+ share
    }
  
  private def parseSingleShare(input: String): Either[ParseError, Share] =
    input.split(":") match
      case Array(name, amountStr) =>
        for
          validName   <- validateName(name, input)
          amount      <- parseAmount(amountStr, input)
          validAmount <- validateAmount(validName, amount)
        yield Share(Person(validName), validAmount)
      
      case _ => 
        Left(ParseError.InvalidFormat(s"Invalid share format: $input (expected Person:Amount)"))
  
  private def validateName(name: String, original: String): Either[ParseError, String] =
    val trimmed = name.trim
    if trimmed.isEmpty then
      Left(ParseError.EmptyPersonName(original))
    else
      Right(trimmed)
  
  private def parseAmount(amountStr: String, original: String): Either[ParseError, Double] =
    amountStr.trim.toDoubleOption match
      case Some(amount) => Right(amount)
      case None => 
        Left(ParseError.InvalidAmount(original, s"'$amountStr' is not a valid number"))
  
  private def validateAmount(person: String, amount: Double): Either[ParseError, Double] =
    if amount < 0 then
      Left(ParseError.NegativeAmount(person, amount))
    else
      Right(amount)
  
  def validateShareSum(
    shares: List[Share], 
    totalAmount: Double, 
    tolerance: Double = 0.01
  ): Either[ParseError, List[Share]] =
    val sum = shares.map(_.amount).sum
    if math.abs(sum - totalAmount) > tolerance then
      Left(ParseError.ShareSumMismatch(sum, totalAmount))
    else
      Right(shares)
  
  def validatePersonsInGroup(
    shares: List[Share], 
    groupMembers: List[IPerson]
  ): Either[ParseError, List[Share]] =
    val invalidPersons = shares
      .map(_.person)
      .filterNot(groupMembers.contains)
      .map(_.name)
    
    if invalidPersons.nonEmpty then
      Left(ParseError.PersonNotInGroup(invalidPersons))
    else
      Right(shares)
  
  def parseAndValidate(
    input: String,
    totalAmount: Double,
    groupMembers: List[IPerson]
  ): Either[ParseError, List[Share]] =
    for
      shares       <- parseShares(input)
      validSum     <- validateShareSum(shares, totalAmount)
      validPersons <- validatePersonsInGroup(validSum, groupMembers)
    yield validPersons
  
  extension (error: ParseError)
    def toMessage: String = error match
      case ParseError.InvalidFormat(inp) => 
        s"Invalid format: $inp"
      case ParseError.InvalidAmount(inp, reason) => 
        s"Invalid amount in '$inp': $reason"
      case ParseError.EmptyPersonName(inp) => 
        s"Empty person name in: $inp"
      case ParseError.NegativeAmount(person, amount) => 
        s"Negative amount for $person: $amount"
      case ParseError.ShareSumMismatch(shareSum, totalAmount) =>
        f"Share sum ($shareSum%.2f€) does not match total amount ($totalAmount%.2f€)"
      case ParseError.PersonNotInGroup(persons) =>
        s"Following persons not in group: ${persons.mkString(", ")}"


extension (parser: Parser)
  def validSharePatternEither(input: String): Either[String, String] =
    val pattern = """^([A-Za-z]+:\d+(\.\d+)?)(_[A-Za-z]+:\d+(\.\d+)?)*$""".r
    if pattern.matches(input) then
      Right(input)
    else
      Left(s"Invalid share pattern: $input")