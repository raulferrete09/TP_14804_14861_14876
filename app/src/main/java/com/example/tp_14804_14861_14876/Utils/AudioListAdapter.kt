package com.example.tp_14804_14861_14876.Utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tp_14804_14861_14876.R
import java.io.File

class AudioListAdapter : RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> {
    private var allFiles: Array<File>
    private lateinit var timeAgo: TimeAgo
    private var onItemListClick: onItemList_Click


    constructor(allFiles: Array<File>, onItemListClick: onItemList_Click?) {
        this.allFiles = allFiles
        this.onItemListClick = onItemListClick!!
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val view =
                LayoutInflater.from(parent.context).inflate(R.layout.single_list_item, parent, false)
        timeAgo = TimeAgo()
        return AudioViewHolder(view)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        holder.list_tv_title.text = allFiles[position].name
        holder.list_tv_date.text = timeAgo.getTimeAgo(allFiles[position].lastModified())
    }

    override fun getItemCount(): Int {
        return allFiles.size
    }

    inner class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {
        private val list_iv_play: ImageView
        val list_tv_title: TextView
        val list_tv_date: TextView


        init {
            list_iv_play = itemView.findViewById(R.id.list_iv_play)
            list_tv_title = itemView.findViewById(R.id.list_tv_title)
            list_tv_date = itemView.findViewById(R.id.list_tv_date)

            itemView.setOnClickListener(this)

        }

        override fun onClick(v: View?) {
            onItemListClick.onClickListener(allFiles[adapterPosition], adapterPosition)
        }
    }

    interface onItemList_Click {
        fun onClickListener(file: File, position: Int)
    }

}