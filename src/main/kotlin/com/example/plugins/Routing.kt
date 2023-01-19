package com.example.plugins

import com.example.models.AccessInformation
import com.example.models.ClientReportDB
import com.example.models.LocationDB
import com.example.models.UserDB
import com.example.mongodb.MongoDB
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*

fun Application.configureRouting() {

    routing {

        get("/") {
            call.respondText("Hello World!")
        }

        get("/users") {
            val userList = MongoDB().readAllUsers()
            if (userList.isNotEmpty()) {
                call.respond(userList)
            } else {
                call.respondText("No users found!", status = HttpStatusCode.OK)
            }
        }

        get("/users/{username?}") {
            val user = MongoDB().readUserFromUsername(call.parameters["username"]!!)
            if (!user.equals(null)) {
                call.respond(user)
            } else {
                call.respondText("User not found!", status = HttpStatusCode.OK)
            }
        }

        get("/users/signInUser") {
            val accessInformation = call.receive<AccessInformation>()

            if(accessInformation.userEmail.isBlank()) {
                call.respond(UserDB("", "", "", "", "", 0.0.toString(), "",""))
            }

            if (MongoDB().checkEmailExistsWithPasswordInCollection("users",
                    accessInformation.userEmail, accessInformation.userPassword) == 1) {
                val user = MongoDB().readUserFromEmail(accessInformation.userEmail)
                call.respond(user)
            } else {
                call.respond(UserDB("", "", "", "", "", 0.0.toString(), "",""))
                call.respondText("User not found!", status = HttpStatusCode.OK)
            }
        }

        post("/users/insertUser") {
            val user = call.receive<UserDB>()
            if (MongoDB().checkEmailExistsInCollection("users", user.email) == 0) {
                MongoDB().insertUser(user)
            }
        }

        /*get("/users/updateLocation/{username?}/{location?}") {
            MongoDB().updateLocationInUserCollection(call.parameters["username"]!!, call.parameters["location"]!!)
            call.respondText("Location updated correctly!")
        }*/

        put("/users/updateLocation") {
            val location = call.receive<LocationDB>()
            MongoDB().updateLocationInUserCollection(location.username, location.location)
            call.respondText("Location updated correctly!")
        }

        put("/users/updateDistance") {
            val location = call.receive<LocationDB>()
            MongoDB().updateDistanceInUserCollection(location.username, location.distance)
            call.respondText("Distance updated correctly!")
        }

        get("/users/updateReportPreference/{username?}/{reportPreference?}") {
            MongoDB()
                .updateReportPreferenceInUserCollection(call.parameters["username"]!!, call.parameters["reportPreference"]!!)
            call.respondText("Report preference updated correctly!")
        }

        get("/users/getReportForUser/{username?}") {
            val username = call.parameters["username"]!!
            val listOfReport = MongoDB().getAllReportForUsername(username)

            if (listOfReport.isEmpty()) {
                call.respond(emptyList<ClientReportDB>().toMutableList())
                call.respondText("No report is of interest for the user")
            } else {
                call.respond(listOfReport)
            }
        }

        post("/users/insertReport") {
            val report = call.receive<ClientReportDB>()
            MongoDB().insertClientReport(report)
            call.respondText("Client report correctly inserted!")
        }

        post("/location/insertLocationAndDistance/") {
            val location = call.receive<LocationDB>()
            MongoDB().insertLocationAndDistanceInLocationCollection(
                location.username,
                location.location,
                location.distance
            )

            call.respondText("Username, location and distance correctly inserted!")
        }

        put("/location/updateLocationAndDistance") {
            val location = call.receive<LocationDB>()

            MongoDB().updateLocationAndDistanceInLocationCollection(
                location.username,
                location.location,
                location.distance
            )
            call.respondText("Username, location and distance correctly updated!")
        }
    }
}
