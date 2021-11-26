package com.bong.meterrecorder.meter

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bong.meterrecorder.R
import com.bong.meterrecorder.room.viewmodels.MeterViewModel
import com.bong.meterrecorder.room.viewmodels.ViewModelUtil

class ChooseMeterDialogFragment: DialogFragment() {
    private lateinit var viewModel: MeterViewModel

    companion object{
        const val KEY_METERS = "KEY_METERS"
        const val KEY_INDEX = "KEY_INDEX"

        fun newInstance(meterNames: ArrayList<String>, currentIndex: Int): ChooseMeterDialogFragment {
            val args = Bundle().apply {
                putStringArrayList(KEY_METERS, meterNames)
                putInt(KEY_INDEX, currentIndex)
            }

            return ChooseMeterDialogFragment().apply {
                arguments = args
            }

        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args = requireArguments()
        val meterNames = args.getStringArrayList(KEY_METERS)
        val currentIndex = args.getInt(KEY_INDEX)


        // View Model
        val factory = ViewModelUtil.createFor(MeterViewModel(requireActivity().application))
        viewModel = ViewModelProvider(this, factory)[MeterViewModel::class.java]



        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.select_meter)
            .setSingleChoiceItems(meterNames?.toArray(emptyArray()), currentIndex
            ) {
                    dialog, which -> TODO("Not yet implemented")
            }
            .setNegativeButton(R.string.cancel) {
                    dialog: DialogInterface?, which: Int ->
                        dismiss();
            }
            .setNeutralButton(R.string.manage_meters) {
                    dialog: DialogInterface?, which: Int ->
                        val intent = Intent(requireContext(), MeterActivity::class.java)
                        startActivity(intent)
                        dismiss()
            }.show()

            return dialog
        }

}