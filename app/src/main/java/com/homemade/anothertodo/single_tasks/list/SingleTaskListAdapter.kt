package com.homemade.anothertodo.single_tasks.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.homemade.anothertodo.databinding.SingleTaskItemBinding
import com.homemade.anothertodo.db.entity.SingleTask

class SingleTaskListAdapter : ListAdapter<SingleTask, SingleTaskListAdapter.ViewHolder>(
    SingleTaskListDiffCallback()
) {
    private lateinit var clickListener: ClickListener
    private lateinit var longClickListener: LongClickListener
    private var selectedItems = listOf<Int>()

    interface ClickListener {
        fun onClick(task: SingleTask)
    }

    interface LongClickListener {
        fun onLongClick(task: SingleTask): Boolean
    }

    fun setOnClickListener(listener: ClickListener) {
        this.clickListener = listener
    }

    fun setOnLongClickListener(listener: LongClickListener) {
        this.longClickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder) {
        val item = getItem(adapterPosition)!!

        itemView.isActivated = selectedItems.contains(adapterPosition)

        bind(item)
        itemView.setOnClickListener { clickListener.onClick(item) }
        itemView.setOnLongClickListener { longClickListener.onLongClick(item) }
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

    fun setSelections(items: List<Int>) {
        val list = selectedItems.toList()
        selectedItems = listOf()
        list.forEach { notifyItemChanged(it) }
        selectedItems = items
        selectedItems.forEach { notifyItemChanged(it) }
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