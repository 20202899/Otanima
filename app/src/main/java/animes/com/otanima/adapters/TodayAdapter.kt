package animes.com.otanima.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import animes.com.otanima.R
import animes.com.otanima.models.Anime
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class TodayAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mData = mutableListOf<Anime>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_today_list,
            parent, false))
    }

    override fun getItemCount() = mData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            val data = mData[position]
            Glide.with(holder.itemView.context)
                .load(data.img)
                .into(holder.img)
        }
    }

    fun setData (data: MutableList<Anime>) {
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }

    inner class MyViewHolder (itemView: View) : RecyclerView.ViewHolder (itemView) {
        val img = itemView.findViewById<ImageView>(R.id.img)
    }

}