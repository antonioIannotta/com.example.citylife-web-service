package com.example.mongodb

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import org.bson.Document

class MongoDB {

    val mongoAddress =
        "mongodb+srv://antonioIannotta:AntonioIannotta-26@citylife.f5vv5xs.mongodb.net/?retryWrites=true"
    val databaseName = "CityLife"
    val userCollection = "users"

    fun readAllUsers(): List<Document> {
        return MongoClient(MongoClientURI(mongoAddress)).getDatabase(databaseName)
            .getCollection(userCollection).find().toList()
    }
}