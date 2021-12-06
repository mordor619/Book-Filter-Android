package com.example.bookfilter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    var authorName = ""
    var countryName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txtAuthor = findViewById<TextInputEditText>(R.id.txtAuthor)
        val txtCountry = findViewById<TextInputEditText>(R.id.txtCountry)
        val btnFilter = findViewById<Button>(R.id.btnFilter)

        val tvResultCount = findViewById<TextView>(R.id.tvResultCount)
        val tvResult1 = findViewById<TextView>(R.id.tvResult1)
        val tvResult2 = findViewById<TextView>(R.id.tvResult2)
        val tvResult3 = findViewById<TextView>(R.id.tvResult3)

        btnFilter.setOnClickListener {
            authorName = txtAuthor.text.toString()
            countryName = txtCountry.text.toString()

            val listOfFilteredBooks = mutableListOf<BookResult>()

            if(authorName.isNullOrEmpty() && countryName.isNullOrEmpty()){
                tvResultCount.text = "Fill one or more fields"
                tvResult1.text = ""
                tvResult2.text = ""
                tvResult3.text = ""
            }
            else{
                val myApp = application as MyApplication
                val httpApiService = myApp.httpApiService

                //run in network(IO) thread
                CoroutineScope(Dispatchers.IO).launch {
                    val jsonRes = httpApiService.getAllBooks()     //http req here

                    for (books in jsonRes){

                        if(countryName.isNullOrEmpty()){
                            if(books.author.equals(authorName)){
                                listOfFilteredBooks.add(books)
                            }
                        }

                        if(authorName.isNullOrEmpty()){
                            if(books.country.equals(countryName)){
                                listOfFilteredBooks.add(books)
                            }
                        }

                        if((books.author.equals(authorName)) && (books.country.equals(countryName))){
                            listOfFilteredBooks.add(books)
                        }

                    }

                    var count = listOfFilteredBooks.size

                    //find top 3 results
                    //run in gui(MAIN) thread
                    withContext(Dispatchers.Main){
                        tvResultCount.text = "Results: $count"

                        if(count >= 3){
                            val name1 = listOfFilteredBooks.get(0).title
                            tvResult1.text = "Result: $name1"

                            val name2 = listOfFilteredBooks.get(1).title
                            tvResult2.text = "Result: $name2"

                            val name3 = listOfFilteredBooks.get(2).title
                            tvResult3.text = "Result: $name3"
                        }

                        if(count == 2){
                            val name1 = listOfFilteredBooks.get(0).title
                            tvResult1.text = "Result: $name1"

                            val name2 = listOfFilteredBooks.get(1).title
                            tvResult2.text = "Result: $name2"

                            tvResult3.text = ""
                        }

                        if(count == 1){
                            val name1 = listOfFilteredBooks.get(0).title
                            tvResult1.text = "Result: $name1"

                            tvResult2.text = ""
                            tvResult3.text = ""
                        }

                    }

                }

            }


        }

    }
}