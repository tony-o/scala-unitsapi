package com.tonyo

import java.util.StringTokenizer
import java.lang.Math.PI
import scala.collection.mutable.{ListBuffer,StringBuilder}

object Op extends Enumeration {
  val DIV, MUL, NIL, NUM = Value
}

case class Tok (var valu : Double, var act : Op.Value, var pos : Int)

object Calculator {
  def collapse(tokens : ListBuffer[Tok], eatLast : Boolean) : Option[String] = {
    if (tokens.last.act != Op.NUM) {
      return Some("Invalid query")
    }
    var tok : Tok    = tokens.last
    var acc : Double = tok.valu
    var pos : Int    = tok.pos
    tokens.dropRightInPlace(1)
    while (tokens.length > 0 && tokens.last.act != Op.NIL) {
      tok = tokens.last
      tokens.dropRightInPlace(1)
      if (tokens.length == 0 || tokens.last.act != Op.NUM) {
        return Some("Two non-number tokens in a row")
      }
      tok.act match {
        case Op.DIV => { acc = tokens.last.valu / acc }
        case Op.MUL => { acc = tokens.last.valu * acc }
      }
      tokens.dropRightInPlace(1)
    }
    if ((eatLast && tokens.length == 0) || (!eatLast && tokens.length > 0 && tokens.last.act != Op.NIL)) {
      return Some("Unbalanced parens");
    }
    if (eatLast) tokens.dropRightInPlace(1)
    tokens += new Tok(acc, Op.NUM, pos)
    None
  }

  def lrParse(str : String) : Either[String, (Double, String)] = {
    val tokenizer = new StringTokenizer(str, "()/* \t", true)
    val unitstr   = new StringBuilder()
    var tokens    = new ListBuffer[Tok]()
    var position  = 0

    while (tokenizer.hasMoreTokens()) {
      val token : String = tokenizer.nextToken()
      token match {
        case "(" => { tokens += new Tok(0.0, Op.NIL, position); unitstr ++= "(" }
        case ")" => {
          unitstr ++= ")"
          collapse(tokens, true) match {
            case Some(s) => return Left(s)
            case _ => {}
          }
        }
        case "/" => {tokens += new Tok(0.0, Op.DIV, position)
                     unitstr ++= "/"}
        case "*" => {tokens += new Tok(0.0, Op.MUL, position)
                     unitstr ++= "*"}

        //seconds
        case "min"|"minute" => {tokens += new Tok(60.0, Op.NUM, position)
                                unitstr ++= "s"}
        case "hour"|"h"     => {tokens += new Tok(3600, Op.NUM, position)
                                unitstr ++= "s"}
        case "day"|"d"      => {tokens += new Tok(86400, Op.NUM, position)
                                unitstr ++= "s"}

        //radians
        case "degree"|"º"     => {tokens += new Tok(PI/180.0, Op.NUM, position)
                                  unitstr ++= "rad"}
        case "arcminute"|"'"  => {tokens += new Tok(PI/10800.0, Op.NUM, position)
                                  unitstr ++= "rad" }
        case "arcsecond"|"\"" => {tokens += new Tok(PI/648000.0, Op.NUM, position)
                                  unitstr ++= "rad" }

        //area
        case "hectare"|"ha" => {tokens += new Tok(10000.0, Op.NUM, position)
                                unitstr ++= "m²"}

        //volume
        case "litre"|"L" => {tokens += new Tok(.001, Op.NUM, position)
                             unitstr ++= "m³"}
        
        //mass
        case "tonne"|"t" => {tokens += new Tok(1000.0, Op.NUM, position)
                             unitstr ++= "kg"}
        
        case " "|"\t" => { }
        case _   => return Left(s"Token '$token' is unknown")
      }
      position += token.length()
    }
    if (tokens.length == 0) {
      return Left("Empty calculation requested")
    }
    collapse(tokens, false)
    if (tokens.length != 1) {
      if (tokens.filter(_.act == Op.NIL).length > 0) {
        return Left("Unbalanced parens")
      }
      return Left("Two non-number tokens in a row")
    }
    return Right(tokens.last.valu, unitstr.toString())
  }
}
