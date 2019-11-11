package animes.com.otanima.models

import androidx.room.Embedded
import androidx.room.Relation
import java.io.Serializable

class AnimeEpisodes : Serializable{
    @Embedded var anime: Anime? = null
    @Relation (parentColumn = "id", entityColumn = "anime_id", entity = Episode::class)
    var episodes = mutableListOf<Episode>()
}