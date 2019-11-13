package animes.com.otanima.models

import androidx.room.*
import java.io.Serializable


@Entity(tableName = "Anime")
data class Anime(var name: String, var url: String, var img: String, @PrimaryKey var id: Int = -1) :
    Serializable

@Entity(
    tableName = "Episode",
    foreignKeys = [ForeignKey(
        entity = Anime::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("anime_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Episode(
    var name: String,
    var url: String,
    var data: String?,
    var img: String?,
    @PrimaryKey var id: Int,
    @ColumnInfo(name = "anime_id") var animeId: Int
) : Serializable

class Link(var url: String) : Serializable