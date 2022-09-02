package com.example.movieappdemo

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private var movieTitleText: TextView? = null
    private var yearReleasedText: TextView? = null


    private val apiUrl = "https://www.omdbapi.com/?t="
    private val apiKey = "apikey=b8deeb4b"
    private var titleRequest: String = null.toString()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Textviews for retrieved data to be displayed
        movieTitleText = findViewById(R.id.movie_title)
        yearReleasedText = findViewById(R.id.year_released)

        //Retrieve text from searchbar (user input)
        val searchText = findViewById<EditText>(R.id.SearchText)

        //Set up Button to listen for user search request
        val btnSearch = findViewById<Button>(R.id.search_btn)
        btnSearch.setOnClickListener {
            if (searchText.text.isNotEmpty()) {
                titleRequest = searchText.text.toString().replace(" ", "+")
                val queue = Volley.newRequestQueue(this)
                val url = buildRequest(titleRequest)

                val stringRequest = StringRequest(Request.Method.GET, url, { response ->
                    val jsonObj = JSONObject(response)
                    val map = jsonObj.toMap()

                    if (map["Response"]!! == "True") {
                        movieTitleText?.text = map["Title"].toString()
                        yearReleasedText?.text = map["Year"].toString()
                        /*if (map["Poster"] != ("N/A")) {

                        }*/
                    } else {
                        movieTitleText?.text = "No Movie Found."
                        yearReleasedText?.text = "N/A"
                    }
                },
                    { movieTitleText?.text = "FAILED" })

                queue.add(stringRequest)
            } else {
                //Nothing to do here, no search provided.
            }
        }
    }

    //create the request URL ->
    //title of movie searched is appended to the URL
    private fun buildRequest(searchKey: String): String {
        return ("$apiUrl$searchKey&$apiKey")
    }

    //Convert JSON string to kotlin Map
    private fun JSONObject.toMap(): Map<String, *> = keys().asSequence().associateWith { s ->
        when (val value = this[s])
        {
            is JSONArray ->
            {
                val map = (0 until value.length()).associate { Pair(it.toString(), value[it]) }
                JSONObject(map).toMap().values.toList()
            }
            is JSONObject -> value.toMap()
            JSONObject.NULL -> null
            else            -> value
        }
    }
}