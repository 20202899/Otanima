package animes.com.otanima.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import animes.com.otanima.R
import animes.com.otanima.activities.VideoActivity
import animes.com.otanima.models.AnimeEpisode
import animes.com.otanima.models.Episode

class EpisodesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val data = mutableListOf<Episode>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_episode_list,
            parent, false))
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            val data = data[position]
            holder.txt1.text = data.name

            holder.itemView.setOnClickListener(null)
            holder.itemView.setOnClickListener {
                val context = it.context
                val intent = Intent(context, VideoActivity::class.java)
                intent.putExtra("data", data)
                context.startActivity(intent)
            }
        }
    }

    fun setData(data: MutableList<Episode>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    inner class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txt1 = itemView.findViewById<TextView>(R.id.text1)
    }

}