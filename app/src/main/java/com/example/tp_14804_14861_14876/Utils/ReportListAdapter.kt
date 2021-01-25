package com.example.tp_14804_14861_14876.Utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tp_14804_14861_14876.Fragments.MainFragment
import com.example.tp_14804_14861_14876.R
import java.io.File

class ReportListAdapter : RecyclerView.Adapter<ReportListAdapter.ReportViewHolder> {
    private var allFilesReport: Array<File>
    private lateinit var timeAgo: TimeAgo
    private var onItemListClick: ReportListAdapter.onItemList_Click


    constructor(allFilesReport: Array<File>, onItemListClick: MainFragment) {
        this.allFilesReport = allFilesReport
        this.onItemListClick = onItemListClick!!
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.single_list_item_report, parent, false)
        timeAgo = TimeAgo()
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.list_tv_titleReport.text = allFilesReport[position].name
        holder.list_tv_dateReport.text = timeAgo.getTimeAgo(allFilesReport[position].lastModified())
    }

    override fun getItemCount(): Int {
        return allFilesReport.size
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
            onItemListClick.onClickListener(allFilesReport[adapterPosition], adapterPosition)
        }

    }

    interface onItemList_Click {
        fun onClickListener(file: File, position: Int)
    }

}