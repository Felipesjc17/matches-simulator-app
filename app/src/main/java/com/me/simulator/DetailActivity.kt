package com.me.simulator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.me.simulator.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setando toolbar
        setSupportActionBar(binding.toolbar)
        //verifica se null e seta como true permitindo as ações da toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}