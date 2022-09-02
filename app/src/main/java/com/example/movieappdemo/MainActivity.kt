package com.example.movieappdemo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private var movieTitleText: TextView? = null
    private var yearReleasedText: TextView? = null
    private var posterDisplay: ImageView? = null


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
        posterDisplay = findViewById(R.id.poster_display)

        //Retrieve text from searchbar (user input)
        val searchText = findViewById<EditText>(R.id.SearchText)

        //Set up Button to listen for user search request
        val btnSearch = findViewById<Button>(R.id.search_btn)
        //Search button ->
        //  If valid movie title is provided, will return available title, year, poster. ->
        //  API GET request made to OMDB API to fetch results ->
        btnSearch.setOnClickListener {
            hideKeyboard()
            if (searchText.text.isNotEmpty()) {
                titleRequest = searchText.text.toString().replace(" ", "+")
                val queue = Volley.newRequestQueue(this)
                val url = buildRequest(titleRequest)

                val stringRequest = StringRequest(Request.Method.GET, url, { response ->
                    val jsonObj = JSONObject(response)
                    val jsonMapped = jsonObj.toMap()

                    //Check if the response contains a valid movie
                    //Valid response will populate necessary fields
                    if (jsonMapped["Response"]!! == "True") {
                        movieTitleText?.text = jsonMapped["Title"].toString()
                        yearReleasedText?.text = jsonMapped["Year"].toString()
                        if (jsonMapped["Poster"] != ("N/A")) {
                            Picasso.get().load(jsonMapped["Poster"].toString()).into(posterDisplay)
                        } else {
                            posterDisplay?.setImageResource(R.drawable.ic_launcher_foreground)
                        }
                    } else {
                        movieTitleText?.text = "No Movie Found."
                        yearReleasedText?.text = "N/A"
                        posterDisplay?.setImageResource(R.drawable.ic_launcher_foreground)
                    }
                },
                    //API error --- bad request, unable to determine if valid search or not.
                    {
                        movieTitleText?.text = "Unable to retrieve title"
                        yearReleasedText?.text = "Unable to retrieve year"
                        posterDisplay?.setImageResource(R.drawable.ic_launcher_foreground)
                    })


                queue.add(stringRequest)
            } else {
                //Nothing to do here yet, no search provided.
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


    //Allows to call 'hideKeyboard()' programmatically close users keyboard.
    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }
    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }
    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}