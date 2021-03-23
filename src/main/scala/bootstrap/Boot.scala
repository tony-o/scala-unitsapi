package bootstrap.liftweb

import net.liftweb.http.LiftRules
import net.liftweb.http._
import net.liftweb.http.rest._
import net.liftweb.json._
import java.util.StringTokenizer
import java.lang.Math.PI
import scala.collection.mutable.{ListBuffer,StringBuilder}

object Op extends Enumeration {
  val DIV, MUL, NIL, NUM = Value
}

object API extends RestHelper {
  def parseDelim : String = "()/* \t"
  def lrParse(str : String) : JObject = {
    val tokenizer = new StringTokenizer(str, parseDelim, true)
    val values  = new ListBuffer[Double]()
    val actions = new ListBuffer[Op.Value]()
    val unitstr = new StringBuilder()
    while (tokenizer.hasMoreTokens()) {
      val token : String = tokenizer.nextToken()
      token match {
        case "(" => { values += 0.0; actions += Op.NIL; unitstr ++= "(" }
        case ")" => {
          unitstr ++= ")"
          if (actions.last != Op.NUM) {
            return JObject(List(JField("error", JString("invalid query."))))
          }
          var acc    : Double = values.last
          var action : Op.Value = Op.NIL
          var value  : Double = 0.0
          values.dropRightInPlace(1)
          actions.dropRightInPlace(1)
          while (actions.length > 0 && actions.last != Op.NIL) {
            action = actions.last
            value  = values.last
            values.dropRightInPlace(1)
            actions.dropRightInPlace(1)
            if (actions.last != Op.NUM) {
              return JObject(List(JField("error", JString("invalid query!"))))
            }
            value = values.last
            actions.dropRightInPlace(1)
            values.dropRightInPlace(1)
            action match {
              case Op.DIV => { acc = value / acc }
              case Op.MUL => { acc = value * acc }
            }
          }
          //drop the nil
          actions.dropRightInPlace(1)
          values.dropRightInPlace(1)
          actions += Op.NUM
          values += acc
        }//collapse to Op.NIL
        case "/" => { values += 0.0; actions += Op.DIV; unitstr ++= "/" }
        case "*" => { values += 0.0; actions += Op.MUL; unitstr ++= "*"}

        //seconds
        case "min"|"minute" => { values += 60.0;  actions += Op.NUM; unitstr ++= "s" }
        case "hour"|"h"     => { values += 3600;  actions += Op.NUM; unitstr ++= "s" }
        case "day"|"d"      => { values += 86400; actions += Op.NUM; unitstr ++= "s" }

        //radians
        case "degree"|"º"     => { values += PI/180.0;    actions += Op.NUM; unitstr ++= "rad" }
        case "arcminute"|"'"  => { values += PI/10800.0;  actions += Op.NUM; unitstr ++= "rad" }
        case "arcsecond"|"\"" => { values += PI/648000.0; actions += Op.NUM; unitstr ++= "rad" }

        //area
        case "hectare"|"ha" => { values += 10000.0; actions += Op.NUM; unitstr ++= "m²"}

        //volume
        case "litre"|"L" => { values += .001; actions += Op.NUM; unitstr ++= "m³" }
        
        //mass
        case "tonne"|"t" => { values += 1000.0; actions += Op.NUM; unitstr ++= "kg" }
        
        //ignore ws
        case " "|"\t" => { }
        //return an error
        case _   => return JObject(List(JField("error", JString(s"token '$token' is unknown"))))
      }
    }
    if (actions.last != Op.NUM) {
      return JObject(List(JField("error", JString("invalid query."))))
    }
    var acc    : Double = values.last
    var action : Op.Value = Op.NIL
    var value  : Double = 0.0
    values.dropRightInPlace(1)
    actions.dropRightInPlace(1)
    while (actions.length > 0 && actions.last != Op.NIL) {
      action = actions.last
      value  = values.last
      values.dropRightInPlace(1)
      actions.dropRightInPlace(1)
      if (actions.last != Op.NUM) {
        return JObject(List(JField("error", JString("invalid query!"))))
      }
      value = values.last
      actions.dropRightInPlace(1)
      values.dropRightInPlace(1)
      action match {
        case Op.DIV => { acc = value / acc }
        case Op.MUL => { acc = value * acc }
      }
    }
    JObject(List(JField("multiplication_factor", JDouble(acc)),
                 JField("unit_name", JString(unitstr.toString()))))
  }
  serve {
    case Req("units" :: "si" :: Nil, _, req) => for (
      unit_s <- S.param("units") ?~ "{\"error\": \"unit in query string required\"}" ~> 400
    ) yield lrParse(unit_s)
  }
}

class Boot {
  def boot() : Unit = {
    LiftRules.statelessDispatch.append(API)
  }
}
