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
    private val userReportDocument = "userReportDocument"
    private val clientReportCollection = "clientReport"
    private val locationCollection = "location"

    /**
     * Metodo per il recupero di tutti gli utenti presenti nella collezione 'uses' del Database
     */
    fun readAllUsers(): MutableList<UserDB> {
        val userList = emptyList<UserDB>().toMutableList()
        val mongoClient = MongoClient(MongoClientURI(mongoAddress))
        mongoClient.getDatabase(databaseName)
            .getCollection(userCollection).find().forEach {
                    document -> userList.add(composeUser(document))
            }
        mongoClient.close()
        return userList
    }

    /**
     * Metodo che ritorna l'utente avente come username quello passato come argomento
     */
    fun readUserFromUsername(username: String): UserDB {
        val mongoClient = MongoClient(MongoClientURI(mongoAddress))
        val user = composeUser(mongoClient.getDatabase(databaseName).getCollection(userCollection).find().first {
                document -> document["username"].toString() == username
        }!!)
        mongoClient.close()
        return user
    }

    /**
     * Metodo che ritorna l'utente avente come email quella passata come argomento.
     */
    fun readUserFromEmail(email: String): UserDB {
        val mongoClient = MongoClient(MongoClientURI(mongoAddress))
        var user: UserDB
        if (email.isBlank()) {
            user = UserDB("", "", "", "", "", "", "", "")
        } else {
            user = composeUser(mongoClient.getDatabase(databaseName).getCollection(userCollection).find().first {
                    document -> document["email"].toString() == email
            }!!)
        }
        mongoClient.close()
        return user
    }

    /**
     * Metodo per l'inserimento di un utente all'interno della collezione 'users' sul Database
     */
    fun insertUser(userDB: UserDB) {
        val mongoClient = MongoClient(MongoClientURI(mongoAddress))
        mongoClient.getDatabase(databaseName)
            .getCollection(userCollection).insertOne(createUserDocument(userDB))
        mongoClient.close()
    }

    /**
     * Metodo per aggiornare la posizione dell'utente, nella collezione 'users', avente come username quello
     * passato come argomento
     */
    fun updateLocationInUserCollection(username: String, location: String) {
        val mongoClient = MongoClient(MongoClientURI(mongoAddress))
        mongoClient.getDatabase(databaseName)
            .getCollection(userCollection).updateOne(
                Filters.eq("username", username), Updates.set("location", location)
            )
        mongoClient.close()
    }

    /**
     * Metodo per aggiornare la distanza dell'utente, nella collezione 'users', avente come username quello
     * passato come argomento
     */
    fun updateDistanceInUserCollection(username: String, distance: String) {
        val mongoClient = MongoClient(MongoClientURI(mongoAddress))
        mongoClient.getDatabase(databaseName)
            .getCollection(userCollection).updateOne(
                Filters.eq("username", username), Updates.set("distance", distance)
            )
        mongoClient.close()
    }

    /**
     * Metodo per aggiornare la lista delle tipologie di report a cui l'utente, nella collezione 'users',
     * avente come username quello passato come argomento, ?? interessato
     */
    fun updateReportPreferenceInUserCollection(username: String, reportPreference: String) {
        val mongoClient = MongoClient(MongoClientURI(mongoAddress))
        mongoClient.getDatabase(databaseName)
            .getCollection(userCollection).updateOne(
                Filters.eq("username", username), Updates.set("reportPreference", reportPreference)
            )
        mongoClient.close()
    }

    /**
     * Metodo che ritorna tutti di documenti, all'interno della collezione 'userReportDocument', relativi all'utente
     * avente come username quello passato come argomento
     */
    fun getAllReportForUsername(username: String): MutableList<ClientReportDB> {
        val mongoClient = MongoClient(MongoClientURI(mongoAddress))
        val reportForUsername = createListOfClientReport(mongoClient.getDatabase(databaseName)
            .getCollection(userReportDocument).find().filter {
                    document -> document["interestedUsername"] == username
            })
        mongoClient.close()
        return reportForUsername
    }

    /**
     * Metodo che consente all'utente di inserire un Report all'interno della collezione 'clientReportCollection'
     */
    fun insertClientReport(report: ClientReportDB) {
        val mongoClient = MongoClient(MongoClientURI(mongoAddress))
        mongoClient.getDatabase(databaseName)
            .getCollection(clientReportCollection).insertOne(createClientReportDocument(report))
        mongoClient.close()
    }

    /**
     * Metodo che si occupa di inserire username, location e distance di un utento al momento dell'iscrizione
     */
    fun insertLocationAndDistanceInLocationCollection(username: String, location: String, distance: String) {
        val mongoClient = MongoClient(MongoClientURI(mongoAddress))
        mongoClient.getDatabase(databaseName)
            .getCollection(locationCollection).insertOne(
                Document()
                    .append("username", username)
                    .append("location", location)
                    .append("distance", distance)
            )
        mongoClient.close()
    }


    /**
     * Metodo che si occupa di aggiornare la distanza e la posizione per un documento, all'interno della collezione
     * 'location', avente come username quello passato come argomento
     */
    fun updateLocationAndDistanceInLocationCollection(username: String, location: String, distance: String) {
        val mongoClient = MongoClient(MongoClientURI(mongoAddress))

        val filter = Filters.eq("username", username)
        val updates = emptyList<Bson>().toMutableList()
        updates.add(Updates.set("username", username))
        updates.add(Updates.set("location", location))
        updates.add(Updates.set("distance", distance))

        val options = UpdateOptions().upsert(true)

        mongoClient.getDatabase(databaseName)
            .getCollection(locationCollection).updateOne(filter, updates, options)

        mongoClient.close()
    }

    /**
     * Metodo che si occupa di creare una lista di Report a partire da una lista di documenti passata come argomento
     */
    private fun createListOfClientReport(listOfDocuments: List<Document>): MutableList<ClientReportDB> {
        val listOfClientReportDB = emptyList<ClientReportDB>().toMutableList()

        listOfDocuments.forEach {
            document -> listOfClientReportDB.add(createClientReportFromDocument(document))
        }
        return listOfClientReportDB
    }

    /**
     * Metodo che si occupa di creare un Report a partire da un documento
     */
    private fun createClientReportFromDocument(document: Document): ClientReportDB {
        val type = document["type"].toString()
        val location = document["location"].toString()
        val localDateTime = document["localDateTime"].toString()
        val text = document["text"].toString()
        val username = document["username"].toString()

        return ClientReportDB(type, location, localDateTime, text , username)
    }

    /**
     * Metodo che si occupa di creare un documento da inserire all'interno della collezione 'users'
     */
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

    /**
     * Metodo che si occupa di creare un documento da inserire all'interno della collezione 'clientReport'
     */
    private fun createClientReportDocument(report: ClientReportDB) =
        Document()
            .append("type", report.type)
            .append("location", report.location)
            .append("localDateTime", report.localDateTime)
            .append("text", report.text)
            .append("username", report.username)

    /**
     * Metodo che si occupa di restituire un Utente a partire da un documento
     */
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

    /**
     * Metodo che si occupa di controllare il numero di documenti, presenti all'interno della collezione specificata,
     * che hanno un campo email uguale alla mail passata come argomento
     */
    fun checkEmailExistsInCollection(collectionName: String, email: String): Int {
        val mongoClient = MongoClient(MongoClientURI(mongoAddress))
        val count = mongoClient.getDatabase(databaseName)
            .getCollection(collectionName).find().count {
                    document -> document["email"] == email
            }
        mongoClient.close()
        return count
    }

    /**
     * Metodo che si occupa di controllare il numero di documenti, presenti all'interno della collezione specificata,
     * che hanno un campo email e password uguali a quelli passati come argomento
     */
    fun checkEmailExistsWithPasswordInCollection(collectionName: String, email: String, password: String): Int {
        val mongoClient = MongoClient(MongoClientURI(mongoAddress))
        val count = mongoClient.getDatabase(databaseName)
            .getCollection(collectionName).find().count {
                    document -> document["email"] == email && document["password"] == password
            }
        mongoClient.close()
        return count
    }
}