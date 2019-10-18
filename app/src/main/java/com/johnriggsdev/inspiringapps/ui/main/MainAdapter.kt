package com.johnriggsdev.inspiringapps.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.johnriggsdev.inspiringapps.R
import kotlinx.android.synthetic.main.row_main.view.*

class MainAdapter(private val sequences : List<MutableMap.MutableEntry<String, Int>>, private val context : Context) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_main, parent, false)
        return MainViewHolder(view)
    }

    override fun getItemCount(): Int {
        return sequences.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val sequence = sequences[position]

        @Suppress("DEPRECATION")
        when (position % 3) {
            0 -> holder.view.position_text.setBackgroundColor(context.resources.getColor(R.color.ia_blue))
            1 -> holder.view.position_text.setBackgroundColor(context.resources.getColor(R.color.ia_red))
            2 -> holder.view.position_text.setBackgroundColor(context.resources.getColor(R.color.ia_green))
        }

        val ordinalPos = position + 1
        holder.view.position_text.text = "$ordinalPos"

        val endpoints = sequence.key.split("_")
        holder.view.sequence_text.text = context.resources.getString(R.string.sequence, endpoints[0], endpoints[1], endpoints[2])
        holder.view.count_text.text = context.resources.getString(R.string.count, sequence.value)
    }

    class MainViewHolder(val view : View) : RecyclerView.ViewHolder(view)
}