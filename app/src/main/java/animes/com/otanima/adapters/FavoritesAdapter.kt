package animes.com.otanima.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import animes.com.otanima.R
import animes.com.otanima.activities.AnimeActivity
import animes.com.otanima.database.AppDataBase
import animes.com.otanima.models.Anime
import animes.com.otanima.models.AnimeEpisodes
import com.bumptech.glide.Glide

class FavoritesAdapter (var context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mData = mutableListOf<AnimeEpisodes>()

    init {
        val db = AppDataBase.getDataBase(context)
        val dao = db.getDao()
        mData.clear()
        mData.addAll(dao.getAnimes())
        db.close()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_search_list,
                parent, false))
    }

    override fun getItemCount() = mData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            val data = mData[position]

            Glide.with(holder.itemView.context)
                .load(data.anime?.img)
                .into(holder.img)

            holder.txt1.text = data.anime?.name

            holder.itemView.setOnClickListener(null)
            holder.itemView.setOnClickListener {
                val context = holder.itemView.context
                val intent = Intent(context, AnimeActivity::class.java)
                intent.putExtra("data", data)
                context.startActivity(intent)
            }
        }
    }

    inner class MyViewHolder (itemView: View) : RecyclerView.ViewHolder (itemView) {
        val img = itemView.findViewById<ImageView>(R.id.img)
        val txt1 = itemView.findViewById<TextView>(R.id.text1)
    }

}