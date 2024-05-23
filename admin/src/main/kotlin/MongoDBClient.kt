import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase

object MongoDBClient {
    private const val CONNECTION_STRING = "mongodb+srv://stanojabozinov:nojco123@p1.allgmev.mongodb.net/?retryWrites=true&w=majority&appName=P1"
    private val client = MongoClients.create(CONNECTION_STRING)
    val database: MongoDatabase = client.getDatabase("test")
}