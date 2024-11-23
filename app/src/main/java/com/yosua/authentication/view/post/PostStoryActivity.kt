package com.yosua.authentication.view.post

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.yosua.authentication.databinding.ActivityPostStoryBinding
import com.yosua.authentication.model.Result
import com.yosua.authentication.model.utils.getImageUri
import com.yosua.authentication.model.utils.reduceFileImage
import com.yosua.authentication.model.utils.uriToFile
import com.yosua.authentication.view.ViewModelFactory
import com.yosua.authentication.view.main.DashboardActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class PostStoryActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPostStoryBinding
    private val viewModel : PostStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private var currentImageUri : Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted : Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadImage() }

        viewModel.uploadStatus.observe(this, Observer { status ->
            when (status) {
                is Result.Loading -> {
                    binding.progressIndicator.visibility = android.view.View.VISIBLE
                }

                is Result.Success -> {
                    binding.progressIndicator.visibility = android.view.View.GONE
                    startActivity(Intent(this, DashboardActivity::class.java))
                    Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                }

                is Result.Error -> {
                    binding.progressIndicator.visibility = android.view.View.GONE
                    Toast.makeText(this, "Error: ${status.error}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri : Uri? ->
        uri?.let {
            currentImageUri = it
            viewModel.setImageUri(it)
            showImage()
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        if (intent.resolveActivity(packageManager) != null) {
            launcherIntentCamera.launch(currentImageUri!!)
        } else {
            Toast.makeText(this, "Kamera tidak tersedia", Toast.LENGTH_SHORT).show()
        }
    }


    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.imagePost.setImageURI(it)
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(
                uri,
                this
            ).reduceFileImage() // Pastikan `reduceFileImage` melakukan kompresi file yang benar
            Log.d("Image File", "showImage: ${imageFile.path}")

            val description =
                binding.descEditText.text.toString() // Pastikan mendapatkan teks yang benar dari EditText

            // RequestBody untuk deskripsi (text)
            val descriptionBody = description.toRequestBody("text/plain".toMediaType())

            // RequestBody untuk gambar (file)
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())

            // Membuat MultipartBody.Part untuk gambar
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            // Mengirim request melalui ViewModel untuk upload
            viewModel.uploadImage(multipartBody, descriptionBody)
        } ?: Toast.makeText(this, "Gambar tidak ada", Toast.LENGTH_SHORT).show()
    }


    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}