package com.bong.meterrecorder.main

import android.content.Context
import android.util.Log
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
import com.bong.meterrecorder.room.entities.extras.ReadingWithPrev
import com.bong.meterrecorder.util.DateTime
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class MainAdapter(private val context: Context) :
    ListAdapter<ReadingWithPrev, MainAdapter.MainViewHolder>(DIFF_CALLBACK) {

    var itemClickListener: OnItemIdClickListener? = null

    companion object{
        const val TAG = "MainAdapter"
        const val MILLIS_IN_A_DAY = 24*60*60*1000

        // Diff item callback
        val DIFF_CALLBACK = object: DiffUtil.ItemCallback<ReadingWithPrev>() {
            override fun areItemsTheSame(oldItem: ReadingWithPrev, newItem: ReadingWithPrev): Boolean {
                // Item properties may have changed if reloaded from the DB, but ID is fixed
                return oldItem.reading.id == newItem.reading.id
            }

            override fun areContentsTheSame(oldItem: ReadingWithPrev, newItem: ReadingWithPrev): Boolean {
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
        holder.bindItem(item)
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


        fun bindItem(item: ReadingWithPrev) {
            val now = DateTime.now()
            val ts = DateTime(item.reading.timeStamp)

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
            tvReading.text = item.reading.value.toString()


            // Calculate difference if there is a next reading
            tvChange.text = if(item.prevReading == null){
                null
            } else {

                // Find the different in days between the 2 time stamps
                val dayDiff = (item.reading.timeStamp / MILLIS_IN_A_DAY - item.prevReading.timeStamp / MILLIS_IN_A_DAY).toInt()

                if (dayDiff == 0) {
                    null
                } else {
                    //Log.d(TAG, "readingDiff = ${item.diff.readingDiff}, dayDiff = ${item.diff.dayDiff}")
                    val result = BigDecimal(item.reading.value.toString())
                        .subtract(BigDecimal(item.prevReading.value.toString()))
                        .divide(BigDecimal(dayDiff.toString()), 3, RoundingMode.HALF_UP)


                    Log.d(TAG, "result = $result")
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