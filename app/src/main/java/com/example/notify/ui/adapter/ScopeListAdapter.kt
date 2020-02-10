package com.example.notify.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import com.example.notify.R
import com.example.notify.structure.response.ScopeList

class ScopeListAdapter internal constructor(context: Context, private val mData: List<ScopeList.Scope>) :
    RecyclerView.Adapter<ScopeListAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater
    private var mClickListener: ItemClickListener? = null

    init {
        this.mInflater = LayoutInflater.from(context)
    }

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.recycler_scope_item, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val animal = mData[position]
        holder.nameView.text = animal.name
    }

    // total number of rows
    override fun getItemCount(): Int {
        return mData.size
    }


    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        internal var nameView: TextView
        internal var iconView: TextView

        init {
            nameView = itemView.findViewById(R.id.name)
            nameView.setTag(1)
            nameView.setOnClickListener(this)

            iconView = itemView.findViewById(R.id.share)
            iconView.setTag(2)
            iconView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val tag = view.tag

            when {
                tag == 1 -> {
                    if (mClickListener != null) mClickListener!!.onItemClick(view, adapterPosition)
                }
                tag == 2 -> {
                    if (mClickListener != null) mClickListener!!.onIconClick(view, adapterPosition)
                }
            }
        }
    }

    // convenience method for getting data at click position
    internal fun getItem(id: Int): ScopeList.Scope {
        return mData[id]
    }

    // allows clicks events to be caught
    internal fun setClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener = itemClickListener
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
        fun onIconClick(view: View, position: Int)
    }
}