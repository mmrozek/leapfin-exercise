package com.lpfn.utils

import scala.util.parsing.combinator.RegexParsers

object ConsoleParser extends RegexParsers {

  val Help =
    """
      |Help:
      |
      |-h shows this info
      |-t timeout in seconds (default 60)
      |
      |ex. ./run.sh -t 30
    """.stripMargin

  def parseArguments(in: List[String]): Either[String, Long] = in match {
    case Nil => Right(60)
    case "-h" :: Nil =>
      Left(Help)
    case other => parseAll("-t" ~> "\\d+".r ^^ (_.toInt), other.mkString(" ")) match {
      case Success(result, _) =>
        Right(result)
      case NoSuccess(msg, _) =>
        Left(Help)
    }
  }

}
