package com.homemade.anothertodo.main_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.homemade.anothertodo.databinding.MainScreenItemBinding
import com.homemade.anothertodo.db.entity.SingleTask

class MainScreenAdapter : ListAdapter<SingleTask, MainScreenAdapter.ViewHolder>(
    SingleTaskListDiffCallback()
) {
    private lateinit var clickListener: ClickListener
    private var selectedPosition = -1

    fun interface ClickListener {
        fun onClick(task: SingleTask)
    }

    fun setOnClickListener(listener: ClickListener) {
        this.clickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder) {
        val item = getItem(adapterPosition)!!

        itemView.apply {
            isActivated = selectedPosition == adapterPosition
            setOnClickListener { clickListener.onClick(item) }
        }

        bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    fun setSelections(taskPosition: Int) {
        notifyItemChanged(selectedPosition)
        selectedPosition = taskPosition
        notifyItemChanged(selectedPosition)
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