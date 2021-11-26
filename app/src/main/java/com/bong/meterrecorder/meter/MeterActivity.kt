package com.bong.meterrecorder.meter

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bong.meterrecorder.R
import com.bong.meterrecorder.databinding.ActivityMeterBinding
import com.bong.meterrecorder.room.viewmodels.MeterViewModel
import com.bong.meterrecorder.room.viewmodels.ViewModelUtil

class MeterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Binding
        val binding = ActivityMeterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rv = binding.rv
        val adapter = MeterAdapter(this)
        val tvEmpty = binding.tvEmpty

        // Set up recycler view
        with(rv){
            val layoutManager = LinearLayoutManager(this@MeterActivity)
            this.layoutManager = layoutManager
            addItemDecoration(
                DividerItemDecoration(context,
                    layoutManager.orientation
                )
            )
            this.adapter = adapter
        }


        val factory = ViewModelUtil.createFor(MeterViewModel(application))
        val viewModel = ViewModelProvider(this, factory)[MeterViewModel::class.java]
        viewModel.allItems.observe(this, {
            adapter.submitList(it)

            // Empty text visibility
            tvEmpty.visibility = if(it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        })

        adapter.itemClickListener = object: MeterAdapter.OnItemIdClickListener {
            override fun onItemIdClicked(position: Int, viewId: Int) {
                val item = adapter.currentList[position]
                if(viewId == R.id.action_edit){
                    // Edit
                    EditMeterDialogFragment.newInstance(item.id)
                        .show(supportFragmentManager, "EditMeterDialogFragment")
                } else if(viewId == R.id.action_delete){
                    viewModel.delete(item)
                }


            }
        }

        binding.fabAdd.apply {
            setOnClickListener {
                EditMeterDialogFragment.newInstance()
                    .show(supportFragmentManager, "EditMeterDialogFragment")
            }
        }
    }



}