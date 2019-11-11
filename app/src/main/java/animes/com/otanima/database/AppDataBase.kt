package animes.com.otanima.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import animes.com.otanima.interfaces.Dao
import animes.com.otanima.models.Anime
import animes.com.otanima.models.Episode

@Database (entities = [Anime::class, Episode::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun getDao(): Dao

    companion object {
        fun getDataBase(context: Context): AppDataBase {
            return Room.databaseBuilder(context, AppDataBase::class.java, "otanima_db.db")
                .allowMainThreadQueries()
                .build()
        }
    }
}