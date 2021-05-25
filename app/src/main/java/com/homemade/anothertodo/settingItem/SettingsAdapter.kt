package com.homemade.anothertodo.settingItem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.homemade.anothertodo.databinding.SettingItemBinding

class SettingsAdapter : RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {
    private lateinit var clickListener: ClickListener
    var data = listOf<SettingItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    interface ClickListener {
        fun onClick(settingItem: SettingItem): Unit?
    }

    fun setOnClickListener(listener: ClickListener) {
        this.clickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
        holder.itemView.isEnabled = item.enabled && item.clicked
        holder.itemView.setOnClickListener { clickListener.onClick(item) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(private val binding: SettingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SettingItem) {
            binding.setting = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SettingItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun getItemCount() = data.size

}