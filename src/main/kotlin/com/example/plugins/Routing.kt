package com.example.plugins

import ReportType
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

        /**
         * Route per il recupero di tutti gli utenti dalla collezione 'users' del database
         */
        get("/users") {
            val userList = MongoDB().readAllUsers()
            if (userList.isNotEmpty()) {
                call.respond(userList)
            } else {
                call.respondText("No users found!", status = HttpStatusCode.OK)
            }
        }

        /**
         * Route per il recupero di un utente con un certo username dalla collezione 'users' nel database
         */
        get("/users/{username?}") {
            val user = MongoDB().readUserFromUsername(call.parameters["username"]!!)
            if (!user.equals(null)) {
                call.respond(user)
            } else {
                call.respondText("User not found!", status = HttpStatusCode.OK)
            }
        }

        /**
         * Route che si occupa di ricevere le informazioni di accesso per effettuare l'operazione di SignIn effettuando
         * tutti i controlli necessari
         */
        get("/users/signInUser") {
            val accessInformation = call.receive<AccessInformation>()

            if(accessInformation.userEmail.isBlank()) {
                call.respond(UserDB("", "", "", "", "", 0.0.toString(), "", emptyList<ReportType>()
                    .toMutableList().toString()))
            }

            if (MongoDB().checkEmailExistsWithPasswordInCollection("users",
                    accessInformation.userEmail, accessInformation.userPassword) == 1) {
                val user = MongoDB().readUserFromEmail(accessInformation.userEmail)
                call.respond(user)
            } else {
                call.respond(UserDB("", "", "", "", "", 0.0.toString(), "",emptyList<ReportType>()
                    .toMutableList().toString()))
                call.respondText("User not found!", status = HttpStatusCode.OK)
            }
        }

        /**
         * Route che si occupa di inserire un utente all'interno della collezione 'users' del database
         */
        post("/users/insertUser") {
            val user = call.receive<UserDB>()
            if (MongoDB().checkEmailExistsInCollection("users", user.email) == 0) {
                MongoDB().insertUser(user)
            }
        }

        /**
         * Route che si occupa di effettuare l'aggiornamento della posizione all'interno della collezione 'users' per un
         * utente avente un certo username presente all'interno dell'oggetto serializzato LocationDB passato nel body
         */
        put("/users/updateLocation") {
            val location = call.receive<LocationDB>()
            MongoDB().updateLocationInUserCollection(location.username, location.location)
            call.respondText("Location updated correctly!")
        }

        /**
         * Route che si occupa di effettuare l'aggiornamento della distanza all'interno della collezione 'users' per un
         * utente avente un certo username presente all'interno dell'oggetto serializzato LocationDB passato nel body
         */
        put("/users/updateDistance") {
            val location = call.receive<LocationDB>()
            MongoDB().updateDistanceInUserCollection(location.username, location.distance)
            call.respondText("Distance updated correctly!")
        }

        /**
         * Route che si occupa di effettuare l'aggiornamento della lista delle segnalazioni di interesse all'interno della collezione 'users' per un
         * utente avente un certo username passato come parametro
         */
        get("/users/updateReportPreference/{username?}/{reportPreference?}") {
            MongoDB()
                .updateReportPreferenceInUserCollection(call.parameters["username"]!!, call.parameters["reportPreference"]!!)
            call.respondText("Report preference updated correctly!")
        }

        /**
         * Route che consente di ritornare tutti i report che riguardano, per via della distanza di interesse, un certo
         * utente
         */
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

        /**
         * Route che si occupa di inserire un report all'interno della collezione 'clientReport' sul database
         */
        post("/users/insertReport") {
            val report = call.receive<ClientReportDB>()
            MongoDB().insertClientReport(report)
            call.respondText("Client report correctly inserted!")
        }

        /**
         * Route che si occupa di inserire, al momento dell'iscrizione al sistema, un documento all'interno della collezione
         * 'location' sul database avente come campi quelli passati nell'oggetto LocationDB nel body della richiesta
         */
        post("/location/insertLocationAndDistance/") {
            val location = call.receive<LocationDB>()
            MongoDB().insertLocationAndDistanceInLocationCollection(
                location.username,
                location.location,
                location.distance
            )

            call.respondText("Username, location and distance correctly inserted!")
        }

        /**
         * Route che si occupa di effettuare l'aggiornamento di posizione e distanza di interesse per un certo
         * username all'interno della collezione 'location'
         */
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
