package animes.com.otanima.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "Anime")
data class Anime(var name: String, var url: String, var img: String, @PrimaryKey var id: Int = -1) :
    Serializable

@Entity(
    tableName = "Episode",
    foreignKeys = [ForeignKey(
        entity = Anime::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("episode_id")
    )]
)
data class Episode(
    var name: String,
    var url: String,
    var data: String,
    var img: String,
    @ColumnInfo(name = "episode_id") var id: Int
) : Serializable

class Link(var url: String) : Serializable