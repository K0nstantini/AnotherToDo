package com.homemade.anothertodo.main_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.homemade.anothertodo.databinding.MainScreenItemBinding
import com.homemade.anothertodo.db.entity.SingleTask
import com.homemade.anothertodo.single_tasks.list.SingleTaskListAdapter

class MainScreenAdapter : ListAdapter<SingleTask, MainScreenAdapter.ViewHolder>(
    SingleTaskListDiffCallback()
) {
    private lateinit var clickListener: ClickListener

    fun interface ClickListener {
        fun onClick(task: SingleTask)
    }

    fun setOnClickListener(listener: ClickListener) {
        this.clickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder) {
        val item = getItem(adapterPosition)!!
        bind(item)
        itemView.setOnClickListener { clickListener.onClick(item) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(private val binding: MainScreenItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SingleTask) {
            binding.singleTask = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MainScreenItemBinding.inflate(layoutInflater, parent, false)
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