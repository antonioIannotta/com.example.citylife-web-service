package com.example.plugins

import com.example.models.ClientReportDB
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
        get("/users/signInUser/{email?}/{password?}") {
            val email = call.parameters["email"]!!
            val password = call.parameters["password"]!!

            println("USEEEEEERS")
            if (MongoDB().checkEmailExistsWithPasswordInCollection("users", email, password) == 1) {
                val user = MongoDB().readUserFromEmail(call.parameters["email"]!!)
                println("User --> " + user)
                call.respond(user)
            } else {
                call.respond(UserDB("", "", "", "", "", "", "",""))
                call.respondText("User not found!", status = HttpStatusCode.OK)
            }
        }
        /*get("/users/insertUser/{name?}/{surname?}/" +
                "{username?}/{email?}/{password?}/{distance?}/" +
                "{location?}/{reportPreference?}") {
            val user = User(
                call.parameters["name"]!!,
                call.parameters["surname"]!!,
                call.parameters["username"]!!,
                call.parameters["email"]!!,
                call.parameters["password"]!!,
                call.parameters["distance"]!!,
                call.parameters["location"]!!,
                call.parameters["reportPreference"]!!
            )
            if (MongoDB().checkEmailExistsInCollection("users", user.email) == 0) {
                MongoDB().insertUser(user)
                call.respondText("User inserted successfully!")
            } else {
                call.respondText("The email is already used for another account!")
            }
        }*/
        post("/users/insertUser") {
            val user = call.receive<UserDB>()
            if (MongoDB().checkEmailExistsInCollection("users", user.email) == 0) {
                MongoDB().insertUser(user)
            }
        }
        get("/users/updateLocation/{username?}/{location?}") {
            MongoDB().updateLocationInUserCollection(call.parameters["username"]!!, call.parameters["location"]!!)
            call.respondText("Location updated correctly!")
        }
        get("/users/updateDistance/{username?}/{distance?}") {
            MongoDB().updateDistanceInUserCollection(call.parameters["username"]!!, call.parameters["distance"]!!)
            call.respondText("Distance updated correctly!")
        }
        get("/users/updateReportPreference/{username?}/{reportPreference}") {
            MongoDB()
                .updateReportPreferenceInUserCollection(call.parameters["username"]!!, call.parameters["reportPreference"]!!)
            call.respondText("Report preference updated correctly!")
        }
        get("/users/lastReport") {
            val lastServerReport = MongoDB().lastServerReport()
            if (lastServerReport == null) {
                call.respondText("No Report stored!")
            } else {
                call.respond(lastServerReport)
            }
        }
        post("/users/insertReport") {
            val report = call.receive<ClientReportDB>()
            MongoDB().insertClientReport(report)
            call.respondText("Client report correctly inserted!")
        }
        get("/location/insertLocationAndDistance/{username?}/{location?}/{distance?}") {
            MongoDB().insertLocationAndDistanceInLocationCollection(
                call.parameters["username"]!!,
                call.parameters["location"]!!,
                call.parameters["distance"]!!
            )

            call.respondText("Username, location and distance correctly inserted!")
        }
        get("/location/updateLocationAndDistance/{username?}/{location?}/{distance?}") {
            MongoDB().updateLocationAndDistanceInLocationCollection(
                call.parameters["username"]!!,
                call.parameters["location"]!!,
                call.parameters["distance"]!!
            )

            call.respondText("Username, location and distance correctly updated!")
        }
    }
}
