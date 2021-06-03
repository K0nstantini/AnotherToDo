package com.homemade.anothertodo.main_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.homemade.anothertodo.databinding.SingleTaskItemBinding
import com.homemade.anothertodo.db.entity.SingleTask

class MainScreenAdapter : ListAdapter<SingleTask, MainScreenAdapter.ViewHolder>(
    SingleTaskListDiffCallback()
) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder) {
        val item = getItem(adapterPosition)!!
        bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(private val binding: SingleTaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SingleTask) {
            binding.singleTask = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SingleTaskItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}


class SingleTaskListDiffCallback : DiffUtil.ItemCallback<SingleTask>() {

    override fun areItemsTheSame(oldItem: SingleTask, newItem: SingleTask): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: SingleTask, newItem: SingleTask): Boolean {
        return oldItem == newItem
    }
}