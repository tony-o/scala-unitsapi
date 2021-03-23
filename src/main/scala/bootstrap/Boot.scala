package bootstrap.liftweb

import net.liftweb.http.LiftRules
import net.liftweb.http._
import net.liftweb.http.rest._
import net.liftweb.json._
import net.liftweb.common.{Full,Empty}
import com.tonyo.Calculator

object API extends RestHelper {
  serve {
    case Req("units" :: "si" :: Nil, _, req) => for {
        unit_s <- S.param("units") ?~ "{\"error\": \"'units' in query string required\"}" ~> 400
        calc_t = Calculator.lrParse(unit_s)
      } yield calc_t match {
        case Right(t) => JObject(List(JField("multiplication_factor", JDouble(t._1)),
                                      JField("unit_name",             JString(t._2)) ))
        case Left(s)  => JObject(List(JField("error", JString(s)))) /* this returns a 200, shouldn't */
      }
  }
}

class Boot {
  def boot() : Unit = {
    LiftRules.statelessDispatch.append(API)
  }
}
