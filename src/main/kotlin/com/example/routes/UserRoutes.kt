package com.example.routes

import com.example.mongodb.MongoDB
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.customerRouting() {
    route("/costumer") {
        val userList = MongoDB().readAllUsers()
        get {
            if (userList.isNotEmpty()) {
                call.respond(userList)
            } else {
                call.respondText("No users found!", status = HttpStatusCode.OK)
            }
        }
    }
}
