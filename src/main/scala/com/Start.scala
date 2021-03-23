import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext
import net.liftweb.common.Loggable
import net.liftweb.util.{LoggingAutoConfigurer, Props}

object Start extends App with Loggable {
  LoggingAutoConfigurer().apply()
  logger.trace("system environment: " + sys.env)
  logger.trace("system props: " + sys.props)
  logger.info("args: " + args.toList)

  startLift()

  def startLift() : Unit = {
    logger.info("starting Lift server")

    val port: Int = 8080 //Props.getInt("jetty.port").openOrThrowException("cannot find jetty.port configuration value")
    logger.info(s"port number is $port")

    val webappDir: String = Option(this.getClass.getClassLoader.getResource("webapp"))
      .map(_.toExternalForm)
      .filter(_.contains("jar:file:")) // this is a hack to distinguish in-jar mode from "expanded"
      .getOrElse("src/main/webapp")

    logger.info(s"webappDir: $webappDir")

    val server = new Server(port)
    val context = new WebAppContext(webappDir, Props.get("jetty.contextPath").openOr("/"))
    server.setHandler(context)
    server.start()
    logger.info(s"Lift server started on port $port")
    server.join()
  }
}
