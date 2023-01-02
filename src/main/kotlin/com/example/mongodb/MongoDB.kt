package com.example.mongodb

import com.example.models.ClientReportDB
import com.example.models.UserDB
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import org.bson.Document
import org.bson.conversions.Bson

class MongoDB {

    private val mongoAddress =
        "mongodb+srv://antonioIannotta:AntonioIannotta-26@citylife.f5vv5xs.mongodb.net/?retryWrites=true"
    private val databaseName = "CityLife"
    private val userCollection = "users"
    private val serverReportCollection = "serverReport"
    private val clientReportCollection = "clientReport"
    private val locationCollection = "location"

    fun readAllUsers(): List<Document> {
        return MongoClient(MongoClientURI(mongoAddress)).getDatabase(databaseName)
            .getCollection(userCollection).find().toList()
    }

    fun readUserFromUsername(username: String): UserDB {
        return composeUser(MongoClient(MongoClientURI(mongoAddress)).getDatabase(databaseName)
            .getCollection(userCollection).find().first {
                document -> document["Username"].toString() == username
            }!!)
    }

    fun readUserFromEmail(email: String): UserDB {
        return composeUser(MongoClient(MongoClientURI(mongoAddress)).getDatabase(databaseName)
            .getCollection(userCollection).find().first {
                    document -> document["email"].toString() == email
            }!!)
    }

    fun insertUser(userDB: UserDB) =
        MongoClient(MongoClientURI(mongoAddress)).getDatabase(databaseName)
            .getCollection(userCollection).insertOne(createUserDocument(userDB))

    fun updateLocationInUserCollection(username: String, location: String) {
        MongoClient(MongoClientURI(mongoAddress)).getDatabase(databaseName)
            .getCollection(userCollection).updateOne(
                Filters.eq("username", username), Updates.set("location", location)
            )
    }

    fun updateDistanceInUserCollection(username: String, distance: String) {
        MongoClient(MongoClientURI(mongoAddress)).getDatabase(databaseName)
            .getCollection(userCollection).updateOne(
                Filters.eq("username", username), Updates.set("distance", distance)
            )
    }

    fun updateReportPreferenceInUserCollection(username: String, reportPreference: String) {
        MongoClient(MongoClientURI(mongoAddress)).getDatabase(databaseName)
            .getCollection(userCollection).updateOne(
                Filters.eq("username", username), Updates.set("reportPreference", reportPreference)
            )
    }

    fun lastServerReport() =
        MongoClient(MongoClientURI(mongoAddress)).getDatabase(databaseName)
            .getCollection(serverReportCollection).find().first()

    fun insertClientReport(report: ClientReportDB) =
        MongoClient(MongoClientURI(mongoAddress)).getDatabase(databaseName)
            .getCollection(clientReportCollection).insertOne(createClientReportDocument(report))

    fun insertLocationAndDistanceInLocationCollection(username: String, location: String, distance: String) =
        MongoClient(MongoClientURI(mongoAddress)).getDatabase(databaseName)
            .getCollection(locationCollection).insertOne(
                Document()
                    .append("username", username)
                    .append("location", location)
                    .append("distance", distance)
            )


    fun updateLocationAndDistanceInLocationCollection(username: String, location: String, distance: String) {
        val filter = Filters.eq("Username", username)
        var updates = emptyList<Bson>().toMutableList()
        updates.add(Updates.set("username", username))
        updates.add(Updates.set("location", location))
        updates.add(Updates.set("distance", distance))

        val options = UpdateOptions().upsert(true)

        MongoClient(MongoClientURI(mongoAddress)).getDatabase(databaseName)
            .getCollection(locationCollection).updateOne(filter, updates, options)
    }

    private fun createUserDocument(userDB: UserDB) =
        Document()
            .append("name", userDB.name)
            .append("surname", userDB.surname)
            .append("username", userDB.username)
            .append("email", userDB.email)
            .append("password", userDB.password)
            .append("distance", userDB.distance)
            .append("location", userDB.location)
            .append("reportPreference", userDB.reportPreference)

    private fun createClientReportDocument(report: ClientReportDB) =
        Document()
            .append("type", report.type)
            .append("location", report.location)
            .append("localDateTime", report.localDateTime)
            .append("text", report.text)
            .append("username", report.username)

    private fun composeUser(document: Document): UserDB {
        val name = document["name"].toString()
        val surname = document["surname"].toString()
        val username = document["username"].toString()
        val email = document["email"].toString()
        val password = document["password"].toString()
        val distance = document["distance"].toString()
        val location = document["location"].toString()
        val reportPreference = document["reportPreference"].toString()

        return UserDB(name, surname, username, email, password, distance, location, reportPreference)
    }

    fun checkEmailExistsInCollection(collectionName: String, email: String) =
        MongoClient(MongoClientURI(mongoAddress)).getDatabase(databaseName)
            .getCollection(collectionName).find().count {
                document -> document["email"] == email
            }

    fun checkEmailExistsWithPasswordInCollection(collectionName: String, email: String, password: String) =
        MongoClient(MongoClientURI(mongoAddress)).getDatabase(databaseName)
            .getCollection(collectionName).find().count {
                    document -> document["email"] == email && document["password"] == password
            }
}