package scala.akka.remote

import com.typesafe.config.ConfigFactory

object RemoteConfig {
    // Get the configuration object for the plane
    val planeConfig = ConfigFactory.load("plane-remote")
    // Easy access to the host of the airport
    val airportHost = planeConfig.getString("scala.akka.avionics.airport-host")
    // Easy access to the port of the airport
    val airportPort = planeConfig.getString("scala.akka.avionics.airport-port")
    
    // Get the configuration object for the airport
    val airportConfig = ConfigFactory.load("airport-remote")
    // Easy access to the host of the plane
    val planeHost = airportConfig.getString("scala.akka.avionics.plane-host")
    // Easy access to the port of the plane
    val planePort = airportConfig.getString("scala.akka.avionics.plane-port")	
}
