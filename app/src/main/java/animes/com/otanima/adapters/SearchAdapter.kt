package animes.com.otanima.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import animes.com.otanima.R
import animes.com.otanima.activities.AnimeActivity
import animes.com.otanima.models.Anime
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class SearchAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mData = mutableListOf<Anime>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            MyViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_search_list,
                    parent, false
                )
            )
        } else {
            HeaderViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.header_lastadded_list,
                    parent, false
                )
            )
        }
    }

    override fun getItemCount() = if (mData.size > 0) {
        mData.size + 1
    }else {
        mData.size
    }

    override fun getItemViewType(position: Int) = if (position == 0) 0 else 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            val data = mData[position - 1]

            Glide.with(holder.itemView.context)
                .load(data.img)
                .into(holder.img)

            holder.txt1.text = data.name

            holder.itemView.setOnClickListener(null)
            holder.itemView.setOnClickListener {
                val context = holder.itemView.context
                val intent = Intent(context, AnimeActivity::class.java)
                intent.putExtra("data", data)
                context.startActivity(intent)
            }
        }

        if (holder is HeaderViewHolder) {
            holder.txt1.text = "RESULTADO DA PESQUISA"
        }
    }

    fun setData(data: List<Anime>) {
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById<ImageView>(R.id.img)
        val txt1 = itemView.findViewById<TextView>(R.id.text1)
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txt1 = itemView.findViewById<TextView>(R.id.text1)
    }

}