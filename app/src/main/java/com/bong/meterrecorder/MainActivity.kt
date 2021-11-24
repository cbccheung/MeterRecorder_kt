package com.bong.meterrecorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bong.meterrecorder.databinding.ActivityMainBinding
import com.bong.meterrecorder.main.EditReadingDialogFragment
import com.bong.meterrecorder.main.MainAdapter
import com.bong.meterrecorder.room.viewmodels.ReadingViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rv = binding.rv
        val adapter = MainAdapter(this)
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


        val viewModel = ViewModelProvider(this).get(ReadingViewModel::class.java)
        viewModel.allItems.observe(this, {
            adapter.submitList(it)
        })


        binding.fabAdd.apply {
            setOnClickListener {
                EditReadingDialogFragment.newInstance()
                    .show(supportFragmentManager, "EditReadingDialogFragment")
            }
        }
    }



}