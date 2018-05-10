package com.lpfn.utils

import org.scalatest.{Matchers, WordSpec}

class ConsoleParserSpec extends WordSpec with Matchers {

  "ConsoleParser" should {
    "return a defined timeout parameter" in {
      ConsoleParser.parseArguments(List("-t", "20")) should be (Right(20))
    }

    "return a default timeout if list of argument is empty" in {
      ConsoleParser.parseArguments(List.empty) should be (Right(60))
    }

    "return a help in different case" in {
      ConsoleParser.parseArguments(List("-b", "20")) should be (Left(ConsoleParser.Help))
    }

  }

}
