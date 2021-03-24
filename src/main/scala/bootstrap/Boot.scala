package bootstrap.liftweb

import net.liftweb.http.LiftRules
import net.liftweb.http._
import net.liftweb.http.rest._
import net.liftweb.json._
import net.liftweb.common.{Full,Empty,Failure,Box}
import com.tonyo.Calculator

object API extends RestHelper {

  /* allow us to return a well formed error if str is invalid */
  def boxify(str : String) : Box[(Double, String)] = Calculator.lrParse(str) match {
    case Right(t) => Full(t)
    case Left(s)  => Failure(JsonAST.compactRender(JObject(List(JField("error", JString(s))))))
  }

  serve {
    case Req("units" :: "si" :: Nil, _, req) => for {
        unit_s <- S.param("units") ?~ "{\"error\": \"'units' in query string required\"}" ~> 400
        calc_t <- boxify(unit_s) ~> 422
      } yield JObject(List(JField("multiplication_factor", JDouble(calc_t._1)),
                           JField("unit_name",             JString(calc_t._2)) ))
  }
}

class Boot {
  def boot() : Unit = {
    LiftRules.statelessDispatch.append(API)
  }
}
