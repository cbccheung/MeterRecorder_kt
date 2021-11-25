package com.bong.meterrecorder.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bong.meterrecorder.R
import com.bong.meterrecorder.room.entities.Reading
import com.bong.meterrecorder.util.DateTime
import java.math.BigDecimal
import java.util.*

class MainAdapter(private val context: Context) :
    ListAdapter<Reading, MainAdapter.MainViewHolder>(DIFF_CALLBACK) {

    var itemClickListener: OnItemIdClickListener? = null

    companion object{
        const val MILLIS_IN_A_DAY = 24*60*60*1000


        // Diff item callback
        val DIFF_CALLBACK = object: DiffUtil.ItemCallback<Reading>() {
            override fun areItemsTheSame(oldItem: Reading, newItem: Reading): Boolean {
                // Item properties may have changed if reloaded from the DB, but ID is fixed
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Reading, newItem: Reading): Boolean {
                // == is equivalent to .equals in Java
                return oldItem == newItem
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.item_reading, parent, false)

        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = getItem(position)
        val nextItem = if(position < itemCount - 1) getItem(position + 1) else null
        holder.bindItem(item, nextItem)
    }

    inner class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvDate = itemView.findViewById<TextView>(R.id.tv_date)
        private val tvChange = itemView.findViewById<TextView>(R.id.tv_change)
        private val tvReading = itemView.findViewById<TextView>(R.id.tv_reading)
        private val btnMore = itemView.findViewById<ImageButton>(R.id.btn_more)

        init {
            btnMore.setOnClickListener {
                showPopupMenu(it)
            }
        }


        fun bindItem(item: Reading, nextItem: Reading?) {
            val now = DateTime.now()
            val ts = DateTime(item.timeStamp)

            val thisYear = now.toCalendar().get(Calendar.YEAR)
            val tsYear = ts.toCalendar().get(Calendar.YEAR)

            val tsDisplay = ts.toString(
                if (thisYear == tsYear){
                    DateTime.DATE_FORMAT_DISPLAY_SIMPLE
                } else {
                    DateTime.DATE_FORMAT_DISPLAY_FULL
                }
            )

            tvDate.text = tsDisplay
            tvReading.text = item.value.toString()

            if(nextItem != null){
                val params = intArrayOf(Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND)

                // Timestamp at 00:00 for current item
                val currentTs = DateTime(item.timeStamp).toCalendar().apply {
                    params.forEach { i ->
                        set(i, 0)
                    }
                }.timeInMillis


                // Timestamp at 00:00 for the previous item
                val oldTs = DateTime(nextItem.timeStamp).toCalendar().apply {
                    params.forEach { i ->
                        set(i, 0)
                    }
                }.timeInMillis

                val dayDiff = ((currentTs - oldTs) / MILLIS_IN_A_DAY).toInt()

                tvChange.text = if(dayDiff == 0){
                    null
                } else {
                    // Formula (item value - next item value) / dayDiff
                    // Convert to BigDecimal to avoid precision lost
                    val result = BigDecimal(item.value.toString())
                        .subtract(BigDecimal(nextItem.value.toString()))
                        .divide(BigDecimal(dayDiff)).toString()

                    tvChange.resources.getString(R.string.val_per_day, result)
                }

            }
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