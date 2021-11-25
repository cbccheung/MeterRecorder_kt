package com.bong.meterrecorder.meter

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bong.meterrecorder.R
import com.bong.meterrecorder.databinding.ViewEditMeterBinding
import com.bong.meterrecorder.room.entities.Meter
import com.bong.meterrecorder.room.viewmodels.SingleMeterViewModel
import com.bong.meterrecorder.room.viewmodels.ViewModelUtil
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*


class EditMeterDialogFragment: DialogFragment() {
    private lateinit var viewModel: SingleMeterViewModel

    private lateinit var tilReading : TextInputLayout
    private lateinit var etReading : TextInputEditText


    companion object{

        const val KEY_ID = "KEY_ID"
        const val NEW_ID = 0L   // New id

        fun newInstance(id: Long? = null): EditMeterDialogFragment {
            val args = Bundle().apply {
                putLong(KEY_ID, id?:NEW_ID)
            }

            val fragment = EditMeterDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args = requireArguments()
        val id = args.getLong(KEY_ID)

        // View Model
        val factory = ViewModelUtil.createFor(SingleMeterViewModel(requireActivity().application, id))
        viewModel = ViewModelProvider(this, factory)[SingleMeterViewModel::class.java]


        val view = ViewEditMeterBinding.inflate(layoutInflater)
        view.let {
            etReading = it.etName.apply {
                addTextChangedListener(
                    afterTextChanged = {
                        isMeterValid()
                    }
                )
            }

            tilReading = it.tilName

        }



        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(if(id == NEW_ID) R.string.new_meter else R.string.edit_meter)
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
                    if (!isMeterValid()) {
                        return@run
                    }

                    val oldItem = viewModel.item.value
                    if(oldItem == null){
                        return@run
                    }

                    val item = Meter(
                        id = id,
                        name = etReading.text.toString()
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

        viewModel.item.observe(this, {
            etReading.setText(
                it.name
            )
        })


        return dialog
    }


    private fun isMeterValid(): Boolean{

        if(etReading.text.toString().isEmpty()){
            tilReading.error = resources.getString(R.string.enter_name)
            return false
        }

        tilReading.error = null
        return true

    }


}