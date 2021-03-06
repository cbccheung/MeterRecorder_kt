package com.bong.meterrecorder.main

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
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
    private lateinit var viewModel: SingleReadingViewModel

    private lateinit var tilReading : TextInputLayout
    private lateinit var etReading : TextInputEditText

    private lateinit var tvDate : TextView
    private lateinit var tvTime : TextView


    companion object{

        const val KEY_METER_ID = "KEY_METER_ID"
        const val KEY_ID = "KEY_ID"
        const val NEW_ID = 0L   // New id
        private const val MILLIS_IN_A_MINUTE = 1000 * 60

        fun newInstance(meterId: Long, id: Long? = null): EditReadingDialogFragment {
            val args = Bundle().apply {
                putLong(KEY_METER_ID, meterId)
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
        val meterId = args.getLong(KEY_METER_ID)

        // View Model
        val factory = ViewModelUtil.createFor(SingleReadingViewModel(requireActivity().application, meterId, id))
        viewModel = ViewModelProvider(this, factory)[SingleReadingViewModel::class.java]


        val view = ViewEditReadingBinding.inflate(layoutInflater)
        view.let {
            tvDate = it.tvDate.apply {

                setOnClickListener{
                    val ts = viewModel.item.value?.timeStamp
                    if(ts != null){

                        val cal = Calendar.getInstance().apply {
                            timeInMillis = ts
                        }


                        DatePickerDialogFragment.newInstance(
                            cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
                        ).show(childFragmentManager, "date")

                    }
                }
            }
            tvTime = it.tvTime.apply {
                setOnClickListener{
                    val ts = viewModel.item.value?.timeStamp
                    if(ts != null){
                        val cal = Calendar.getInstance().apply {
                            timeInMillis = ts
                        }


                        TimePickerDialogFragment.newInstance(
                            cal[Calendar.HOUR_OF_DAY], cal[Calendar.MINUTE]
                        ).show(childFragmentManager, "date")

                    }
                }
            }


            etReading = it.etReading.apply {
                addTextChangedListener(
                    afterTextChanged = {
                        isReadingValid()
                    }
                )
            }

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

                    // Validations
                    if (!isReadingValid()) {
                        return@run
                    }

                    val oldItem = viewModel.item.value
                    if(oldItem == null){
                        return@run
                    }

                    val item = Reading(
                        id = id,
                        meterId = meterId,
                        value = etReading.text.toString().toFloat(),
                        timeStamp = oldItem.timeStamp / MILLIS_IN_A_MINUTE * MILLIS_IN_A_MINUTE,    //round to minute
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

        viewModel.item.observe(this){
            etReading.setText(
                if(it.value.isNaN()) "" else it.value.toString()
            )
            setDateTime(it.timeStamp)

        }


        return dialog
    }


    private fun isReadingValid(): Boolean{

        try{
            val value = etReading.text.toString().toFloatOrNull()
            if(value == null){
                tilReading.error = resources.getString(R.string.enter_decimal_number)
                return false
            }
            tilReading.error = null
            return true
        } catch (ex: NumberFormatException){
            tilReading.error = resources.getString(R.string.enter_decimal_number)
            return false
        }

    }



    override fun onDatePicked(year: Int, month: Int, day: Int) {
        val item = viewModel.item.value
        if (item != null) {

            val newTimestamp = Calendar.getInstance().apply {
                this.timeInMillis = item.timeStamp

                this[Calendar.YEAR] = year
                this[Calendar.MONTH] = month
                this[Calendar.DAY_OF_MONTH] = day
            }.timeInMillis

            item.timeStamp = newTimestamp

            setDateTime(newTimestamp)
        }
    }

    override fun onTimePicked(hour: Int, minute: Int) {
        val item = viewModel.item.value
        if (item != null) {

            val newTimestamp = Calendar.getInstance().apply {
                this.timeInMillis = item.timeStamp

                this[Calendar.HOUR_OF_DAY] = hour
                this[Calendar.MINUTE] = minute
            }.timeInMillis

            item.timeStamp = newTimestamp

            setDateTime(newTimestamp)
        }
    }

    private fun setDateTime(ts: Long){
        val cal = Calendar.getInstance().apply {
            timeInMillis = ts
        }



        //etDate.setText("${day}/${month}/${year}")
        tvDate.text =
            String.format("%02d/%02d/%04d", cal[Calendar.DAY_OF_MONTH], cal[Calendar.MONTH] + 1, cal[Calendar.YEAR])
        tvTime.text = String.format("%02d:%02d", cal[Calendar.HOUR_OF_DAY], cal[Calendar.MINUTE])
    }



}