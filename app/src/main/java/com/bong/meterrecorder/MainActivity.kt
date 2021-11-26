package com.bong.meterrecorder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
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

class MainActivity : AppCompatActivity() {
    private lateinit var _meters: List<Meter>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rv = binding.rv
        val adapter = MainAdapter(this)
        val tvEmpty = binding.tvEmpty

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
        val viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        viewModel.meters.observe(this, {
            _meters = it

            /*
            // Empty results
            if(it.isEmpty()){
                return@observe
            }
            */

            viewModel.setMeterId(it[0].id)
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
                    // Edit
                    EditReadingDialogFragment.newInstance(item.id)
                        .show(supportFragmentManager, "EditReadingDialogFragment")
                } else if(viewId == R.id.action_delete){
                    viewModel.delete(item)
                }


            }
        }

        binding.fabAdd.apply {
            setOnClickListener {
                EditReadingDialogFragment.newInstance()
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
            ChooseMeterDialogFragment.newInstance(
                meterNames = ArrayList(_meters),
                currentIndex = 0
            )
        } else if(item.itemId == R.id.action_manage_meter){
            val intent = Intent(this, MeterActivity::class.java)
            startActivity(intent)
        }
        return true
    }

}