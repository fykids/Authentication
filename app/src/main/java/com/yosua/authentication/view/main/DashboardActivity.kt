package com.yosua.authentication.view.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.yosua.authentication.MainActivity
import com.yosua.authentication.databinding.ActivityDashboardBinding
import com.yosua.authentication.model.Result
import com.yosua.authentication.view.ViewModelFactory
import com.yosua.authentication.view.main.adapter.StoryAdapter

@Suppress("SENSELESS_COMPARISON")
class DashboardActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDashboardBinding
    private lateinit var storyAdapter : StoryAdapter
    private val viewModel : DashboardViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (user.token == null){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        setupRecyclerView()
        observeData()

        viewModel.getAllStories()
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter(emptyList())
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@DashboardActivity)
            setHasFixedSize(true)
            adapter = storyAdapter
        }
    }

    private fun observeData() {
        viewModel.stories.observe(this) { result ->
            when (result) {
                is Result.Loading -> {

                }

                is Result.Success -> {
                    storyAdapter = StoryAdapter(result.data.listStory.filterNotNull())
                    binding.rvStory.adapter = storyAdapter
                }

                is Result.Error -> {
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}