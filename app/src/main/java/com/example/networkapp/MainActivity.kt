package com.example.networkapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.concurrent.write

// TODO (1: Fix any bugs)
// TODO (2: Add function saveComic(...) to save comic info when downloaded
// TODO (3: Automatically load previously saved comic when app starts)

class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView
    private val filename = "comic.json";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        titleTextView = findViewById<TextView>(R.id.comicTitleTextView)
        descriptionTextView = findViewById<TextView>(R.id.comicDescriptionTextView)
        numberEditText = findViewById<EditText>(R.id.comicNumberEditText)
        showButton = findViewById<Button>(R.id.showComicButton)
        comicImageView = findViewById<ImageView>(R.id.comicImageView)

        showButton.setOnClickListener {
            downloadComic(numberEditText.text.toString())
        }


        val file = File(filesDir, filename)

        if (file.exists()) {
            try {
                val fileInputStream = file.inputStream()
                val jsonString = fileInputStream.bufferedReader().use { it.readText() }
                val comicObject = JSONObject(jsonString)
                showComic(comicObject)
                Log.d("MainActivity", "Comic loaded from: ${file.absolutePath}")
            } catch (e: IOException) {
                Log.e("MainActivity", "Error loading comic: ${e.message}")
            }
        }

    }

    // Fetches comic from web as JSONObject
    private fun downloadComic (comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        requestQueue.add (
            JsonObjectRequest(url
                , {showComic(it)}
                , { Log.e("MainActivity", "Error downloading comic: ${it.message}")}
            )
        )
    }

    // Display a comic for a given comic JSON object
    private fun showComic (comicObject: JSONObject) {
        titleTextView.text = comicObject.getString("title")
        descriptionTextView.text = comicObject.getString("alt")
        val imageUrl = comicObject.getString("img")
        Log.d("MainActivity", "Image URL: $imageUrl")
        Picasso.get().load(comicObject.getString("img")).into(comicImageView)
        saveComic(comicObject)
    }

    // Implement this function
    private fun saveComic(comicObject: JSONObject) {

        val file = File(filesDir, filename)

        try {
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(comicObject.toString().toByteArray())
            fileOutputStream.close()
            Log.d("MainActivity", "Comic saved to: ${file.absolutePath}")
        } catch (e: IOException) {
            Log.e("MainActivity", "Error saving comic: ${e.message}")
        }
    }


}