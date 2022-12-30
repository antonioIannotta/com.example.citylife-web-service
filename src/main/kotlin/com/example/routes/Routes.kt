package com.example.routes

import com.example.models.ClientReport
import com.example.models.User
import com.example.mongodb.MongoDB
import io.ktor.http.*
import io.ktor.http.cio.HttpMessage
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.customerRouting() {
    route("/users") {
        val userList = MongoDB().readAllUsers()
        get {
            if (userList.isNotEmpty()) {
                call.respond(userList)
            } else {
                call.respondText("No users found!", status = HttpStatusCode.OK)
            }
        }
        get("{username?}") {
            val user = MongoDB().readUserFromUsername(call.parameters["username"]!!)
            if (!user.equals(null)) {
                call.respond(user)
            } else {
                call.respondText("User not found!", status = HttpStatusCode.OK)
            }
        }
        get("{email?}/{password?}") {
            val email = call.parameters["email"]!!
            val password = call.parameters["password"]!!

            if (MongoDB().checkEmailExistsWithPasswordInCollection("users", email, password)) {
                val user = MongoDB().readUserFromEmail(call.parameters["email"]!!)
                call.respond(user)
            } else {
                call.respond(User("", "", "", "", "", "", "",""))
                call.respondText("User not found!", status = HttpStatusCode.OK)
            }
        }
        post("/insertUser") {
            val user = call.receive<User>()
            if (MongoDB().insertUser(user) == "OK") {
                call.respondText("User inserted successfully!")
            } else {
                call.respondText("The email is already used for another account!")
            }

        }
        get("/updateLocation/{username?}/{location?}") {
            MongoDB().updateLocationInUserCollection(call.parameters["username"]!!, call.parameters["location"]!!)
            call.respondText("Location updated correctly!")
        }
        get("/updateDistance/{username?}/{distance?}") {
            MongoDB().updateDistanceInUserCollection(call.parameters["username"]!!, call.parameters["distance"]!!)
            call.respondText("Distance updated correctly!")
        }
        get("/updateReportPreference/{username?}/{reportPreference}") {
            MongoDB()
                .updateReportPreferenceInUserCollection(call.parameters["username"]!!, call.parameters["reportPreference"]!!)
            call.respondText("Report preference updated correctly!")
        }
        get("/lastReport") {
            val lastServerReport = MongoDB().lastServerReport()
            if (lastServerReport == null) {
                call.respondText("No Report stored!")
            } else {
                call.respond(lastServerReport)
            }
        }
        post ("/insertReport") {
            val report = call.receive<ClientReport>()
            MongoDB().insertClientReport(report)
            call.respondText("Client report correctly inserted!")
        }
    }
    route("/location") {
        get ("/insertLocationAndDistance/{username?}/{location?}/{distance?}") {

            MongoDB().insertLocationAndDistanceInLocationCollection(
                call.parameters["username"]!!,
                call.parameters["location"]!!,
                call.parameters["distance"]!!
            )

            call.respondText("Username, location and distance correctly inserted!")
        }
        get ("/updateLocationAndDistance/{username?}/{location?}/{distance?}") {

            MongoDB().updateLocationAndDistanceInLocationCollection(
                call.parameters["username"]!!,
                call.parameters["location"]!!,
                call.parameters["distance"]!!
            )

            call.respondText("Username, location and distance correctly updated!")
        }
    }
}
