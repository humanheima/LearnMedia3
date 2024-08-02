package com.dmw.mediasessiondemo.client

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.recyclerview.widget.RecyclerView
import com.dmw.mediasessiondemo.R


/**
 * Created by p_dmweidu on 2024/8/2
 * Desc:
 */
class DemoAdapter(private val data: List<MediaItem>) :
    RecyclerView.Adapter<DemoAdapter.ViewHolder>() {

    var onClickAction: ((MediaItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_music, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onClickAction?.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMusicName: TextView

        init {
            tvMusicName = itemView.findViewById(R.id.tv_music_name)
        }

        fun bind(item: MediaItem) {
            tvMusicName.text = item.mediaMetadata.title
        }
    }
}