package com.example.tp_14804_14861_14876.Utils

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.tp_14804_14861_14876.Activitys.uploadPDF
import com.example.tp_14804_14861_14876.Fragments.MainFragment
import com.example.tp_14804_14861_14876.R
import java.io.File
import java.util.ArrayList

class ReportListAdapter : RecyclerView.Adapter<ReportListAdapter.ReportViewHolder> {
    var uploadPDFS: ArrayList<uploadPDF>
    private lateinit var timeAgo: TimeAgo
    private var onItemListClick: ReportListAdapter.onItemList_Click


    constructor(uploadPDFS: ArrayList<uploadPDF>, onItemListClick: MainFragment) {
        this.uploadPDFS = uploadPDFS
        this.onItemListClick = onItemListClick!!
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.single_list_item_report, parent, false)
        timeAgo = TimeAgo()
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.list_tv_titleReport.text = uploadPDFS[position].name
        //holder.list_tv_dateReport.text = timeAgo.getTimeAgo(uploadPDFS[position].lastModified())
    }

    override fun getItemCount(): Int {
        return uploadPDFS.size
    }

    inner class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val list_iv_anomaly: ImageView
        val list_tv_titleReport: TextView
        val list_tv_dateReport: TextView


        init {
            list_iv_anomaly = itemView.findViewById(R.id.list_iv_anomaly)
            list_tv_titleReport = itemView.findViewById(R.id.list_tv_titleReport)
            list_tv_dateReport = itemView.findViewById(R.id.list_tv_dateReport)

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onItemListClick.onClickListener(uploadPDFS[adapterPosition], adapterPosition)
        }

    }

    interface onItemList_Click {
        fun onClickListener(uploadPDFS: uploadPDF , position: Int){

        }


    }

}