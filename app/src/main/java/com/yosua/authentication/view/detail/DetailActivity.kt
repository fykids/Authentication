package com.yosua.authentication.view.detail

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.yosua.authentication.R
import com.yosua.authentication.databinding.ActivityDetailBinding
import com.yosua.authentication.model.Result
import com.yosua.authentication.view.ViewModelFactory

class DetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailBinding

    private val viewModel : DetailViewModel by viewModels{
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val storyId = intent.getStringExtra("storyId")

        if (storyId != null) {
            viewModel.getStory(storyId)

            viewModel.detailStatus.observe(this, Observer { result ->
                when (result) {
                    is Result.Loading -> {

                    }

                    is Result.Success -> {
                        val story = result.data.story
                        binding.tvPerson.text = story.name
                        binding.tvDescription.text = story.description
                        Glide.with(this)
                            .load(story.photoUrl)
                            .into(binding.imageView)
                    }

                    is Result.Error -> {
                        Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } else {
            Toast.makeText(this, "Detail tidak tersedia", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item : MenuItem) : Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                onBackPressed()
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }
}