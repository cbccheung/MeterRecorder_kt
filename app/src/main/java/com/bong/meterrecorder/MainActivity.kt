package com.bong.meterrecorder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bong.meterrecorder.databinding.ActivityMainBinding
import com.bong.meterrecorder.main.EditReadingDialogFragment
import com.bong.meterrecorder.main.MainAdapter
import com.bong.meterrecorder.main.MainAdapter.OnItemIdClickListener
import com.bong.meterrecorder.main.MainViewModel
import com.bong.meterrecorder.meter.ChooseMeterDialogFragment
import com.bong.meterrecorder.meter.MeterActivity
import com.bong.meterrecorder.room.entities.Meter
import com.bong.meterrecorder.room.viewmodels.ViewModelUtil
import com.bong.meterrecorder.util.SettingsHelper

class MainActivity : AppCompatActivity(), ChooseMeterDialogFragment.MeterChosenListener{
    private lateinit var settingsHelper: SettingsHelper
    private lateinit var viewModel: MainViewModel
    private lateinit var _meters: List<Meter>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rv = binding.rv
        val adapter = MainAdapter(this)
        val tvEmpty = binding.tvEmpty

        settingsHelper = SettingsHelper(this)

        // Set up recycler view
        with(rv){
            val layoutManager = LinearLayoutManager(this@MainActivity)
            this.layoutManager = layoutManager
            addItemDecoration(
                DividerItemDecoration(context,
                    layoutManager.orientation
            ))
            this.adapter = adapter
        }



        val factory = ViewModelUtil.createFor(MainViewModel(application))
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        viewModel.meters.observe(this, {
            _meters = it

            /*
            // Empty results
            if(it.isEmpty()){
                return@observe
            }
            */
            val selectedMeterId = settingsHelper.meterId

            var selectedMeter = it.find { a -> a.id == selectedMeterId }
            if(selectedMeter == null){
                selectedMeter = it[0]
            }

            viewModel.setMeterId(selectedMeter.id)
        })

        viewModel.meter.observe(this, {
            title = it.name
        })

        viewModel.readings.observe(this, {
            adapter.submitList(it)

            // Empty text visibility
            tvEmpty.visibility = if(it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        })

        adapter.itemClickListener = object: OnItemIdClickListener {
            override fun onItemIdClicked(position: Int, viewId: Int) {
                val item = adapter.currentList[position]
                if(viewId == R.id.action_edit){
                    val meter = getCurrentMeter()
                    if(meter == null){
                        return
                    }
                    // Edit
                    EditReadingDialogFragment.newInstance(meterId = meter.id, id = item.reading.id)
                        .show(supportFragmentManager, "EditReadingDialogFragment")
                } else if(viewId == R.id.action_delete){
                    viewModel.delete(item.reading)
                }


            }
        }

        binding.fabAdd.apply {
            setOnClickListener {
                val meter = getCurrentMeter()
                if(meter == null){
                    return@setOnClickListener
                }

                EditReadingDialogFragment.newInstance(meterId = meter.id)
                    .show(supportFragmentManager, "EditReadingDialogFragment")
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_change_meter) {
            val meterIds = arrayListOf<Long>()
            val meterNames = arrayListOf<String>()

            var currentIndex = 0
            val currentMeterId = settingsHelper.meterId

            _meters.forEachIndexed { index, meter ->
                meterIds.add(meter.id)
                meterNames.add(meter.name)
                if(currentMeterId == meter.id){
                    currentIndex = index
                }
            }

            ChooseMeterDialogFragment.newInstance(
                meterIds = meterIds,
                meterNames = meterNames,
                currentIndex = currentIndex
            ).show(supportFragmentManager, "Choose")

        } else if(item.itemId == R.id.action_manage_meter){
            val intent = Intent(this, MeterActivity::class.java)
            startActivity(intent)
        }
        return true
    }

    override fun onMeterChosen(meterId: Long) {

        val meter = _meters.find { a -> a.id == meterId }

        if(meter == null){
            // Meter id not found
            Toast.makeText(this, resources.getString(R.string.meter_not_found), Toast.LENGTH_SHORT).show()
            return
        }

        settingsHelper.meterId = meterId
        viewModel.setMeterId(meterId)
    }


    //region Helper methods
    private fun getCurrentMeter(): Meter? {
        return viewModel.meter.value
    }
    //endregion
}