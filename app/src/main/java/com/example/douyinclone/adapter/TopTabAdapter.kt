package com.example.douyinclone.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.douyinclone.R
import com.example.douyinclone.data.model.TabItem

class TopTabAdapter(
    private val onTabClick: (Int) -> Unit
) : RecyclerView.Adapter<TopTabAdapter.TabViewHolder>() {
    
    private var tabs: List<TabItem> = emptyList()
    
    fun submitList(newTabs: List<TabItem>) {
        tabs = newTabs
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_top_tab, parent, false)
        return TabViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        val tab = tabs[position]
        holder.bind(tab)
        holder.itemView.setOnClickListener {
            onTabClick(tab.id)
        }
    }
    
    override fun getItemCount(): Int = tabs.size
    
    class TabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTab: TextView = itemView.findViewById(R.id.tv_tab)
        private val viewIndicator: View = itemView.findViewById(R.id.view_indicator)
        
        fun bind(tab: TabItem) {
            tvTab.text = tab.title
            
            if (tab.isSelected) {
                tvTab.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_primary))
                tvTab.setTypeface(null, Typeface.BOLD)
                tvTab.textSize = 18f
                viewIndicator.visibility = View.VISIBLE
            } else {
                tvTab.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_secondary))
                tvTab.setTypeface(null, Typeface.NORMAL)
                tvTab.textSize = 15f
                viewIndicator.visibility = View.INVISIBLE
            }
        }
    }
}
