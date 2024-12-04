package com.yosua.authentication.view.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.yosua.authentication.MainActivity
import com.yosua.authentication.R
import com.yosua.authentication.databinding.ActivityDashboardBinding
import com.yosua.authentication.model.remote.response.ListStoryItem
import com.yosua.authentication.view.ViewModelFactory
import com.yosua.authentication.view.detail.DetailActivity
import com.yosua.authentication.view.main.adapter.StoryAdapter
import com.yosua.authentication.view.post.PostStoryActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

        setSupportActionBar(binding.toolbar)

        viewModel.getSession().observe(this) { user ->
            if (user.token.isEmpty()) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                viewModel.getSession()
            }
        }

        setupRecyclerView()
        observeData()

//        viewModel.getAllStories()
        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this, PostStoryActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter() { story ->
            onStoryClicked(story)
        }

        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@DashboardActivity)
            setHasFixedSize(true)
            adapter = storyAdapter
        }
    }

    private fun observeData() {
//        viewModel.stories.observe(this) { result ->
//            when (result) {
//                is Result.Loading -> {
//
//                }
//
//                is Result.Success -> {
//                    storyAdapter = StoryAdapter(result.data.listStory.filterNotNull()) { story ->
//                        onStoryClicked(story)
//                    }
//                    binding.rvStory.adapter = storyAdapter
//                }
//
//                is Result.Error -> {
//                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
        lifecycleScope.launch {
            viewModel.stories.collectLatest { pagingData ->
                storyAdapter.submitData(pagingData)
            }
        }
    }

    private fun onStoryClicked(story : ListStoryItem) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("storyId", story.id)
        }
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu : Menu?) : Boolean {
        menuInflater.inflate(R.menu.menu_setting, menu) // Memuat file menu_main.xml
        return true
    }

    // Menangani klik menu
    override fun onOptionsItemSelected(item : MenuItem) : Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                // Menangani aksi klik Settings
                viewModel.logout()
                return true
            }

            R.id.action_maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}