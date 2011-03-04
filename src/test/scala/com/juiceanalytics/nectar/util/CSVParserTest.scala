package com.juiceanalytics.nectar.util

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import java.io.StringReader


@RunWith(classOf[JUnitRunner])
class CSVParserTest extends FunSuite with ShouldMatchers {
  val fileText    = "Alabama,M,0,30479,32055\nAlabama,M,1,29904,32321\r\nAlabama,M,2,30065,31789"
  val fileFixture = new StringReader(fileText)
  val lineFixture = "\"Alabama\", M,\t0,30479, 32055"

  test("a parsed file contains 3 rows") {
    val parsed = CSVParser.parseFile(fileFixture)
    parsed.isDefined should be(true)
    parsed.get.size should be(3)
  }

  test("a parsed line contains 5 columns") {
    val parsed = CSVParser.parseLine(lineFixture)
    parsed.isDefined should be(true)
    parsed.get.size should be(5)
  }
}
