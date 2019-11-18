package animes.com.otanima.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import animes.com.otanima.R
import animes.com.otanima.activities.VideoActivity
import animes.com.otanima.models.Anime
import animes.com.otanima.models.Episode
import com.bumptech.glide.Glide
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.animation.AccelerateDecelerateInterpolator


class LastAddedAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mData = mutableListOf<Episode>()
    private var lastIndex = -1

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            HeaderViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.header_lastadded_list,
                    parent, false
                )
            )
        } else {
            MyViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_lastadded_list,
                    parent, false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            0
        } else {
            1
        }
    }

    override fun getItemId(position: Int): Long {
        return if (position == 0) {
            hashCode().toLong()
        } else {
            mData[position - 1].id.toLong()
        }
    }

    override fun getItemCount() = if (mData.size > 0) {
        mData.size + 1
    } else {
        mData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            val data = mData[position - 1]

            Glide.with(holder.itemView.context)
                .load(data.img)
                .into(holder.img)

            holder.text1.text = data.name

            holder.itemView.setOnClickListener(null)

            holder.itemView.setOnClickListener {
                val context = it.context
                val intent = Intent(context, VideoActivity::class.java)
                intent.putExtra("data", data)
                context.startActivity(intent)
            }

            setAnimation(holder, position)
        }
    }

    fun setData(data: MutableList<Episode>) {
        if (mData.size == 0) {
            mData.addAll(data)
            notifyDataSetChanged()
        } else {
            data.forEach {
                mData.add(it)
                notifyItemInserted(mData.lastIndex + 1)
            }
        }

    }

    private fun setAnimation(holder: MyViewHolder, position: Int) {
//        if (position > lastIndex) {
        holder.itemView.startAnimation(
            AnimationUtils.loadAnimation(
                holder.itemView.context,
                R.anim.anim_in
            )
        )

        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            holder.img_play,
            PropertyValuesHolder.ofFloat("scaleX", 1.4f),
            PropertyValuesHolder.ofFloat("scaleY", 1.4f)
        )
        scaleDown.duration = 1000
        scaleDown.interpolator = AccelerateDecelerateInterpolator()
        scaleDown.repeatCount = ObjectAnimator.INFINITE
        scaleDown.repeatMode = ObjectAnimator.REVERSE

        scaleDown.start()
//            lastIndex = position
//        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById<ImageView>(R.id.img)
        val img_play = itemView.findViewById<ImageView>(R.id.img_play)
        val text1 = itemView.findViewById<TextView>(R.id.text1)
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}