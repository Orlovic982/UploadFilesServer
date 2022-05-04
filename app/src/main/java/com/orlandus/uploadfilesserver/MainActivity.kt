package com.orlandus.uploadfilesserver

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.orlandus.uploadfilesserver.databinding.ActivityMainBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : AppCompatActivity(), UploadRequestBody.UploadCallback {

    lateinit var binding: ActivityMainBinding
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView.setOnClickListener {
            openImageChooser()
        }

        binding.buttonUpload.setOnClickListener {
            uploadImage()
        }


    }


    private fun uploadImage() {
        if (selectedImageUri == null) {
            binding.layoutRoot.snackbar("Select image firts")
        }
        val parcelFileDescripter = contentResolver.openFileDescriptor(selectedImageUri!!, "r", null) ?: return
        val file = File(cacheDir, contentResolver.getFileName(selectedImageUri!!))
        val inputStream = FileInputStream(parcelFileDescripter.fileDescriptor)
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        binding.progressBar.progress = 0
        val body = UploadRequestBody(file, "image", this)

        MyApi().uploadImage(
            MultipartBody.Part.createFormData("image", file.name, body),
            RequestBody.create(MediaType.parse("multipart/form-data"), "Image from my device")
        ).enqueue(object : Callback<UploadResponse> {
            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                binding.progressBar.progress=100
                binding.layoutRoot.snackbar(response.body()?.message.toString())
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                binding.layoutRoot.snackbar(t.message!!)
            }

        })

    }


    private fun openImageChooser() {

        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png")

            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, REQUEST_CODE_IMAGE_PICKER)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_IMAGE_PICKER -> {
                    selectedImageUri = data?.data
                    binding.imageView.setImageURI(selectedImageUri)
                }
            }
        }
    }


    companion object {
        private const val REQUEST_CODE_IMAGE_PICKER = 100

        const val REQUEST_CODE_PICK_IMAGE = 101
    }

    override fun onProgressUpdate(percentage: Int) {

        binding.progressBar.progress=percentage

    }


}