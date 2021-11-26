package com.bong.meterrecorder.meter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bong.meterrecorder.R
import com.bong.meterrecorder.databinding.ItemMeterBinding
import com.bong.meterrecorder.room.entities.Meter

class MeterAdapter(private val context: Context) :
    ListAdapter<Meter, MeterAdapter.MeterViewHolder>(DIFF_CALLBACK) {

    var itemClickListener: OnItemIdClickListener? = null

    companion object{

        // Diff item callback
        val DIFF_CALLBACK = object: DiffUtil.ItemCallback<Meter>() {
            override fun areItemsTheSame(oldItem: Meter, newItem: Meter): Boolean {
                // Item properties may have changed if reloaded from the DB, but ID is fixed
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Meter, newItem: Meter): Boolean {
                // == is equivalent to .equals in Java
                return oldItem == newItem
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeterViewHolder {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ItemMeterBinding.inflate(inflater, parent, false)

        return MeterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MeterViewHolder, position: Int) {
        val item = getItem(position)
        holder.bindItem(item)
    }

    inner class MeterViewHolder(binding: ItemMeterBinding) : RecyclerView.ViewHolder(binding.root) {

        private val tvName = binding.tvName
        private val btnMore = binding.btnMore

        init {
            btnMore.setOnClickListener {
                showPopupMenu(it)
            }
        }


        fun bindItem(item: Meter) {
            tvName.text = item.name
        }

        private fun showPopupMenu(view: View) {
            val popup = PopupMenu(view.context, view)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.menu_item_more, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                val itemId = item.itemId
                itemClickListener?.onItemIdClicked(bindingAdapterPosition, itemId)
                true
            }
            popup.show()
        }
    }

    interface OnItemIdClickListener{
        fun onItemIdClicked(position: Int, viewId: Int)
    }

}