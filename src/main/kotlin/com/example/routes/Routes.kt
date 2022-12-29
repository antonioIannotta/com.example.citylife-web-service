package com.example.routes

import com.example.models.ClientReport
import com.example.models.User
import com.example.mongodb.MongoDB
import io.ktor.http.*
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
            if (user.isEmpty()) {
                call.respond(user)
            } else {
                call.respondText("User not found!", status = HttpStatusCode.OK)
            }
        }
        post("/insertUser") {
            val user = call.receive<User>()
            MongoDB().insertUser(user)
            call.respondText("User inserted successfully!")
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
