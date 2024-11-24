package com.yosua.authentication.view.detail

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.yosua.authentication.databinding.ActivityDetailBinding
import com.yosua.authentication.model.Result
import com.yosua.authentication.view.ViewModelFactory

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailBinding

    private val viewModel : DetailViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Detail Story")

        val storyId = intent.getStringExtra("storyId")
        if (storyId.isNullOrEmpty()) {
            Toast.makeText(this, "ID cerita tidak valid", Toast.LENGTH_SHORT).show()
            finish() // Tutup activity jika ID tidak valid
            return
        }

        // Mengambil data melalui ViewModel
        viewModel.getStory(storyId)

        // Observasi perubahan data pada ViewModel
        viewModel.detailStatus.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    // Tampilkan loading jika sedang memuat data
                }

                is Result.Success -> {
                    val story = result.data.story
                    binding.tvPerson.text = story.name
                    binding.tvDescription.text = story.description
                    Glide.with(this@DetailActivity)
                        .load(story.photoUrl)
                        .into(binding.imageView)
                }

                is Result.Error -> {
                    Toast.makeText(this@DetailActivity, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item : MenuItem) : Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}