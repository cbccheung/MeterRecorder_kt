package com.bong.meterrecorder.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bong.meterrecorder.R
import com.bong.meterrecorder.room.entities.Reading
import com.bong.meterrecorder.util.DateTime
import java.util.*

class MainAdapter(private val context: Context) :
    ListAdapter<Reading, MainAdapter.Companion.MainViewHolder>(DIFF_CALLBACK) {

    companion object{
        const val MILLIS_IN_A_DAY = 24*60*60*1000

        class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            private val tvDate = itemView.findViewById<TextView>(R.id.tv_date)
            private val tvChange = itemView.findViewById<TextView>(R.id.tv_change)
            private val tvReading = itemView.findViewById<TextView>(R.id.tv_reading)


            fun bindItem(item: Reading, oldItem: Reading?) {
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

                tvDate.setText(tsDisplay)
                tvReading.setText(item.value.toString())

                if(oldItem != null){
                    val params = intArrayOf(Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND)

                    // Timestamp at 00:00 for current item
                    val currentTs = DateTime(item.timeStamp).toCalendar().apply {
                        params.forEach { i ->
                            set(i, 0)
                        }
                    }.timeInMillis


                    // Timestamp at 00:00 for the previous item
                    val oldTs = DateTime(oldItem.timeStamp).toCalendar().apply {
                        params.forEach { i ->
                            set(i, 0)
                        }
                    }.timeInMillis

                    val dayDiff = (currentTs - oldTs) / MILLIS_IN_A_DAY

                    tvChange.text = if(dayDiff == 0L){
                        null
                    } else {
                        val valAvg = (item.value - oldItem.value) / dayDiff
                        tvChange.resources.getString(R.string.val_per_day, valAvg)
                    }

                }
            }

        }

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
        val oldItem = if(position == 0) null else getItem(position - 1)
        holder.bindItem(item, oldItem)
    }

}