package com.bong.meterrecorder.main

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bong.meterrecorder.R
import com.bong.meterrecorder.databinding.ViewEditReadingBinding
import com.bong.meterrecorder.room.entities.Reading
import com.bong.meterrecorder.room.viewmodels.SingleReadingViewModel
import com.bong.meterrecorder.room.viewmodels.ViewModelUtil
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*


class EditReadingDialogFragment: DialogFragment(), DatePickerDialogFragment.DatePickedListener, TimePickerDialogFragment.TimePickedListener {
    //private lateinit var tilDate : TextInputLayout
    //private lateinit var etDate : TextInputEditText
    //private lateinit var tilTime : TextInputLayout
    //private lateinit var etTime : TextInputEditText
    private lateinit var tilReading : TextInputLayout
    private lateinit var etReading : TextInputEditText

    private lateinit var tvDate : TextView
    private lateinit var tvTime : TextView


    companion object{

        const val KEY_ID = "KEY_ID"
        const val NEW_ID = 0L   // New id

        fun newInstance(id: Long? = null): EditReadingDialogFragment {
            val args = Bundle().apply {
                putLong(KEY_ID, id?:NEW_ID)
            }

            val fragment = EditReadingDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args = requireArguments()
        val id = args.getLong(KEY_ID)

        // View Model
        val factory = ViewModelUtil.createFor(SingleReadingViewModel(requireActivity().application, id))
        val viewModel = ViewModelProvider(this, factory)[SingleReadingViewModel::class.java]


        val view = ViewEditReadingBinding.inflate(layoutInflater)
        view.let {
            tvDate = it.tvDate.apply {

                setOnClickListener{
                    var year: Int
                    var month: Int
                    var day: Int

                    try {
                        val text = tvDate.text.toString().split("/")
                        year = text[2].toInt()
                        month = text[1].toInt()
                        day = text[0].toInt()
                    } catch (e: Exception){
                        year = -1
                        month = -1
                        day = -1
                    }
                    DatePickerDialogFragment.newInstance(
                        year, month, day
                    ).show(childFragmentManager, "date")
                }
            }
            tvTime = it.tvTime.apply {
                setOnClickListener{
                    var hour: Int
                    var minute: Int

                    try {
                        val text = tvTime.text.toString().split(":")
                        hour = text[0].toInt()
                        minute = text[1].toInt()
                    } catch (e: Exception){
                        hour = -1
                        minute = -1
                    }
                    TimePickerDialogFragment.newInstance(
                        hour, minute
                    ).show(childFragmentManager, "time")
                }
            }

            /*
            tilDate = it.tilDate.apply {
                setOnClickListener{
                    Toast.makeText(context, "tilDate clicked", Toast.LENGTH_SHORT).show()
                }
            }

            etDate = it.etDate.apply {

                setOnClickListener{
                    Toast.makeText(context, "etDate clicked", Toast.LENGTH_SHORT).show()
                    var year: Int
                    var month: Int
                    var day: Int

                    try {
                        val text = etDate.text.toString().split("/")
                        year = text[2].toInt()
                        month = text[1].toInt()
                        day = text[0].toInt()
                    } catch (e: Exception){
                        year = -1
                        month = -1
                        day = -1
                    }
                    DatePickerDialogFragment.newInstance(
                        year, month, day
                    ).show(childFragmentManager, "date")
                }
            }
            etTime = it.etTime
            tilTime = it.tilTime.apply {
                setOnClickListener{
                    var hour: Int
                    var minute: Int

                    try {
                        val text = etDate.text.toString().split("/")
                        hour = text[1].toInt()
                        minute = text[0].toInt()
                    } catch (e: Exception){
                        hour = -1
                        minute = -1
                    }
                    TimePickerDialogFragment.newInstance(
                        hour, minute
                    ).show(childFragmentManager, "time")
                }
            }
            */
            etReading = it.etReading
            tilReading = it.tilReading


        }



        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(if(id == NEW_ID) R.string.new_reading else R.string.edit_reading)
            .setView(view.root)
            .setPositiveButton(R.string.done) { dialog: DialogInterface?, which: Int ->

            }
            .setNegativeButton(R.string.cancel) {
                    dialog: DialogInterface?, which: Int ->

            }.show()

        dialog.getButton(Dialog.BUTTON_POSITIVE)
            .setOnClickListener { v: View ->
                run {

                    if (!isDateValid() or !isTimeValid() or !isReadingValid()) {
                        return@run
                    }

                    val item = Reading(
                        id = id,
                        value = etReading.text.toString().toFloat(),
                        timeStamp = System.currentTimeMillis(),
                        modified = System.currentTimeMillis()
                    )

                    if (id == NEW_ID) {
                        viewModel.insert(item)
                    } else {
                        viewModel.update(item)
                    }

                    dismiss()
                }
            }


        // Load the record if it is not a new record
        if (id == NEW_ID) {
            val now = Calendar.getInstance()
            with(now){
                val year = get(Calendar.YEAR)
                val month = get(Calendar.MONTH)
                val dayOfMonth = get(Calendar.DAY_OF_MONTH)

                val hour = get(Calendar.HOUR_OF_DAY)
                val min = get(Calendar.MINUTE)

                setDate(year, month, dayOfMonth)
                setTime(hour, min)
            }
        } else {
            viewModel.item.observe(this, {
                etReading.setText(
                    if(it.value.isNaN()) "" else it.value.toString()
                )
                setDate(it.timeStamp)
                setTime(it.timeStamp)

            })
        }

        return dialog
    }

    private fun isDateValid(): Boolean{
        return true
    }

    private fun isTimeValid(): Boolean{
        return true
    }


    private fun isReadingValid(): Boolean{
        return true
    }



    override fun onDatePicked(year: Int, month: Int, day: Int) {
        setDate(day, month, year)
    }

    private fun setDate(ts: Long){
        val cal = Calendar.getInstance().apply {
            timeInMillis = ts
        }

        setDate(
            cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
        )
    }

    private fun setDate(year: Int, month: Int, day: Int){
        //etDate.setText("${day}/${month}/${year}")
        tvDate.setText(String.format("%02d/%02d/%04d", day, month, year))
    }

    override fun onTimePicked(hour: Int, minute: Int) {
        setTime(hour, minute)
    }

    private fun setTime(ts: Long){
        val cal = Calendar.getInstance().apply {
            timeInMillis = ts
        }

        setTime(
            cal[Calendar.HOUR_OF_DAY], cal[Calendar.MINUTE]
        )
    }

    private fun setTime(hour: Int, minute: Int){
        tvTime.setText(String.format("%02d:%02d", hour, minute))

    }
}