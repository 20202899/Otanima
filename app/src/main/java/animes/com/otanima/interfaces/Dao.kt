package animes.com.otanima.interfaces

import androidx.room.*
import androidx.room.Dao
import animes.com.otanima.models.Anime
import animes.com.otanima.models.AnimeEpisodes
import animes.com.otanima.models.Episode

@Dao
interface Dao {
    @Insert
    fun insert(anime: Anime)

    @Insert
    fun insert(episode: Episode)

    @Insert
    fun insert(episodes: MutableList<Episode>)

    @Query("SELECT * FROM Anime")
    fun getAnimes (): MutableList<AnimeEpisodes>

    @Query("SELECT * FROM Anime where id = :animeId")
    fun getAnimeById(animeId: Int): AnimeEpisodes?

    @Delete
    fun deleteAnime(vararg anime: Anime?)
}