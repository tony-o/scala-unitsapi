package com.tonyo

import org.scalatest.funsuite.AnyFunSuite
import com.tonyo.Calculator
import java.lang.Math.PI

class CalculatorTest extends AnyFunSuite {
  val testsGood : List[(String, Double, String)] = List(("t * t", 1000.0 * 1000.0, "kg*kg"),
                                                        ("t*t", 1000.0 * 1000.0, "kg*kg"),
                                                        ("t", 1000.0, "kg"),
                                                        ("tonne * tonne", 1000.0 * 1000.0, "kg*kg"),
                                                        ("tonne*tonne", 1000.0 * 1000.0, "kg*kg"),
                                                        ("tonne", 1000.0, "kg"),
                                                        ("(t)", 1000.0, "(kg)"),
                                                        ("((((((((((t))))))))))", 1000.0, "((((((((((kg))))))))))"),
                                                        ("( t    ) *( (  ( ( ( ( t))))))", 1000.0 * 1000.0, "(kg)*((((((kg))))))"),
                                                        ("min", 60, "s"),
                                                        ("minute", 60, "s"),
                                                        ("hour", 3600, "s"),
                                                        ("h", 3600, "s"),
                                                        ("d", 86400, "s"),
                                                        ("day", 86400, "s"),
                                                        ("degree", PI/180.0, "rad"),
                                                        ("º", PI/180.0, "rad"),
                                                        ("arcminute", PI/10800.0, "rad"),
                                                        ("'", PI/10800.0, "rad"),
                                                        ("arcsecond", PI/648000.0, "rad"),
                                                        ("\"", PI/648000.0, "rad"),
                                                        ("ha", 10000.0, "m²"),
                                                        ("hectare", 10000.0, "m²"),
                                                        ("litre", .001, "m³"),
                                                        ("L", .001, "m³"),
                                                        ("(degree/(minute*hectare))", 2.908882086657216E-8, "(rad/(s*m²))"),
                                                        ("(degree/minute)", 2.908882086657216E-4, "(rad/s)"))
  for (t <- testsGood) {
    test(t._1) {
      val tparse  = Calculator.lrParse(t._1) //
      tparse match {
        case Right((d,s)) => assert(d == t._2 && s == t._3)
        case _ => assert(1 == 0)
      }
    }
  }
  val testsBad : List[(String, String)] = List(("t t", "Two non-number tokens in a row"),
                                               ("tt", "Token 'tt' is unknown"),
                                               ("", "Empty calculation requested"),
                                               ("(t t)", "Two non-number tokens in a row"),
                                               ("(t * ( ( ( ( ( ( t t ) ) ) ) ) ) )", "Two non-number tokens in a row"),
                                               ("(t * t", "Unbalanced parens"),
                                               ("((t * t)", "Unbalanced parens"),
                                               ("t * t)", "Unbalanced parens"),
                                               ("(t * t))", "Unbalanced parens"),
                                              )
  for (t <- testsBad) {
    test(t._1) {
      val tparse = Calculator.lrParse(t._1)
      tparse match {
        case Left(s) => assert(s == t._2)
        case _ => assert(1 == 0)
      }
    }
  }
}
