package com.bong.meterrecorder.meter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.bong.meterrecorder.R

class ChooseMeterDialogFragment: DialogFragment() {

    private lateinit var listener: MeterChosenListener

    companion object{
        const val KEY_IDS = "KEY_IDS"
        const val KEY_METERS = "KEY_METERS"
        const val KEY_INDEX = "KEY_INDEX"

        fun newInstance(meterIds: ArrayList<Long>, meterNames: ArrayList<String>, currentIndex: Int): ChooseMeterDialogFragment {
            val args = Bundle().apply {
                putLongArray(KEY_IDS, meterIds.toLongArray())
                putStringArrayList(KEY_METERS, meterNames)
                putInt(KEY_INDEX, currentIndex)
            }

            return ChooseMeterDialogFragment().apply {
                arguments = args
            }

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is MeterChosenListener) {
            listener = context
        } else {
            listener = parentFragment as MeterChosenListener
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args = requireArguments()
        val meterIds = args.getLongArray(KEY_IDS)!! //Non null array
        val meterNames = args.getStringArrayList(KEY_METERS)
        val currentIndex = args.getInt(KEY_INDEX)


        // View Model

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.select_meter)
            .setSingleChoiceItems(meterNames?.toArray(emptyArray()), currentIndex
            ) {
                    dialog, which ->
                        listener.onMeterChosen(meterIds[which])
                        dismiss()
            }
            .setNegativeButton(R.string.cancel) {
                    dialog: DialogInterface?, which: Int ->
                        dismiss()
            }
            .setNeutralButton(R.string.manage_meters) {
                    dialog: DialogInterface?, which: Int ->
                        val intent = Intent(requireContext(), MeterActivity::class.java)
                        startActivity(intent)
                        dismiss()
            }.show()

            return dialog
        }


    interface MeterChosenListener{
        fun onMeterChosen(meterId: Long)
    }
}