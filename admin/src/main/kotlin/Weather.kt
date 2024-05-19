import java.time.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Weather(
    val name: String?,
    @Serializable(with = LocalDateTimeSerializer::class) val timestamp: LocalDateTime = LocalDateTime.now(),
    val data: Map<String, String?>

)