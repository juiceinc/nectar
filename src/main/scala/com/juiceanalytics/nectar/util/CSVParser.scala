package com.juiceanalytics.nectar.util

import scala.util.parsing.combinator.RegexParsers


/**
 * Parse Comma Separated Values using the recommendation detailed in RFC4180.
 *
 * @see http://tools.ietf.org/html/rfc4180
 * @see http://stackoverflow.com/questions/5063022/use-scala-parser-combinator-to-parse-csv-files
 * @author Jon Buffington
 */
object CSVParser extends RegexParsers {
  // Spaces are elements in CSV content.
  override def skipWhitespace: Boolean = false

  private val COMMA = ","

  private val DQUOTE = "\""

  // combine 2 dquotes into 1
  private val DQUOTE2 = "\"\"" ^^ {
    case _ => "\""
  }

  private val CRLF = "\r\n" | "\n"

  private val TXT = "[^\",\r\n]".r

  private val SPACES = "[ \t]+".r

  protected def file: Parser[List[List[String]]] = repsep(record, CRLF) <~ (CRLF ?)

  protected def record: Parser[List[String]] = repsep(field, COMMA)

  protected def field: Parser[String] = escaped | nonescaped

  protected def escaped: Parser[String] = {
    ((SPACES ?) ~> DQUOTE ~> ((TXT | COMMA | CRLF | DQUOTE2) *) <~ DQUOTE <~ (SPACES ?)) ^^ {
      case ls => ls.mkString("")
    }
  }

  protected def nonescaped: Parser[String] = (TXT *) ^^ {
    case ls => ls.mkString("")
  }

  /**
   * Parse multi-line CSV content provided by the input.
   *
   * @param in Is the multi-line CSV content to be parsed.
   * @return Returns either Some(table of parse strings) or None.
   */
  def parseFile(in: java.io.Reader): Option[List[List[String]]] = parseAll(file, in) match {
    case Success(res, _) => Some(res)
    case _ => None
  }

  /**
   * Parse a single line of CSV content provided by the input.
   *
   * @param s Is the CSV line to be parsed.
   * @return Returns either Some(list of strings columns) or None.
   */
  def parseLine(s: String): Option[List[String]] = parse(record, s) match {
    case Success(res, _) => Some(res)
    case _ => None
  }
}
