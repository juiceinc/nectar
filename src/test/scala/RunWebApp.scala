import org.eclipse.jetty.server.nio.SelectChannelConnector
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext

object RunWebApp extends App {
  val server = new Server
  val scc = new SelectChannelConnector
  scc.setPort(8080)
  server.setConnectors(Array(scc))

  val context = new WebAppContext
  context.setContextPath("/")
  context.setWar("src/main/webapp")

  server.setHandler(context)

  try {
    println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP")
    server.start()
    while (System.in.available() == 0) {
      Thread.sleep(5000)
    }
    server.stop()
    server.join()
  } catch {
    case exc : Exception => {
      exc.printStackTrace()
      System.exit(100)
    }
  }
}
