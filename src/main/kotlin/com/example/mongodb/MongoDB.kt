package com.example.mongodb

import com.example.models.ClientReport
import com.example.models.User
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import org.bson.BsonArray
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

    fun readUserFromUsername(username: String): User {
        return composeUser(MongoClient(MongoClientURI(mongoAddress)).getDatabase(databaseName)
            .getCollection(userCollection).find().first {
                document -> document["Username"].toString() == username
            }!!)
    }

    fun readUserFromEmail(email: String): User {
        return composeUser(MongoClient(MongoClientURI(mongoAddress)).getDatabase(databaseName)
            .getCollection(userCollection).find().first {
                    document -> document["Username"].toString() == email
            }!!)
    }

    fun insertUser(user: User): String {
        var insertResult = ""

        insertResult = if (checkEmailExistsInCollection(userCollection, user.email)) {
            MongoClient(MongoClientURI(mongoAddress)).getDatabase(databaseName)
                .getCollection(userCollection).insertOne(createUserDocument(user))

            "OK"
        } else {
            "ERROR!"
        }
        return insertResult
    }

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

    fun insertClientReport(report: ClientReport) =
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

    private fun createUserDocument(user: User) =
        Document()
            .append("name", user.name)
            .append("surname", user.surname)
            .append("username", user.username)
            .append("email", user.email)
            .append("password", user.password)
            .append("distance", user.distance)
            .append("location", user.location)

    private fun createClientReportDocument(report: ClientReport) =
        Document()
            .append("type", report.type)
            .append("location", report.location)
            .append("localDateTime", report.localDateTime)
            .append("text", report.text)
            .append("username", report.username)

    private fun composeUser(document: Document): User {
        val name = document["name"].toString()
        val surname = document["surname"].toString()
        val username = document["username"].toString()
        val email = document["email"].toString()
        val password = document["password"].toString()
        val distance = document["distance"].toString()
        val location = document["location"].toString()
        val reportPreference = document["reportPreference"].toString()

        return User(name, surname, username, email, password, distance, location, reportPreference)
    }

    private fun checkEmailExistsInCollection(collectionName: String, email: String): Boolean =
        MongoClient(MongoClientURI(mongoAddress)).getDatabase(databaseName)
            .getCollection(collectionName).find().count {
                document -> document["email"] == email
            } == 1

    fun checkEmailExistsWithPasswordInCollection(collectionName: String, email: String, password: String) =
        MongoClient(MongoClientURI(mongoAddress)).getDatabase(databaseName)
            .getCollection(collectionName).find().count {
                    document -> document["email"] == email && document["password"] == password
            } == 1
}