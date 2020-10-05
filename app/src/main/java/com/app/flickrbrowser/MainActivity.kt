package com.app.flickrbrowser

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.content_main.*
import learnprogramming.academy.R

private const val TAG = "MainActivity"

// 1-the download started by creating a GetRawData Object , in OnCreate
// 2- whn the Download complete , onDownloadComplete fun gets called
//3- That kicks off parsing the data , by creating a GetFlickrJsonData instance
//4-when the parsing complete , the onDataAvailable fun gets called

// coordinator layout is using as a top layout - when you want child views to interact
// so that can do neat things like make the toolbar fold up when scrolling , then fold down again
// when yo've scrolled all the way back up


@Suppress("SameParameterValue")
class MainActivity : BaseActivity(), GetRawData.OnDownloadComplete,
    GetFlickrJsonData.OnDataAvailable, RecyclerItemClickListener.OnRecyclerClickListener {


    private val flickrRecyclerViewAdapter = FlickrRecyclerViewAdapter(ArrayList())

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activateToolbar(false)


      recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.addOnItemTouchListener(RecyclerItemClickListener(this ,recycler_view , this))
        recycler_view.adapter = flickrRecyclerViewAdapter




        Log.d(TAG, "onCreate ends")
    }

    override fun onItemClick(view: View, position: Int) {
        Log.d(TAG , "onItemClick : called")
        Toast.makeText(this , "Normal tap at position $position" , Toast.LENGTH_SHORT).show()



    }

    override fun onItemLongClick(view: View, position: Int) {
        Log.d(TAG , "onItemLongClick")
//        Toast.makeText(this , "" , Toast.LENGTH_SHORT).show()
        val photo = flickrRecyclerViewAdapter.getPhoto(position)
        if(photo != null){
            val intent = Intent(this , PhotoDetailsActivity::class.java)
            // putExtra to tell which photo it should display
            intent.putExtra(PHOTO_TRANSFER , photo)
            startActivity(intent)
        }
    }

    private fun createUri(baseURL : String, searchCriteria : String, lang : String, matchAll : Boolean) : String{

        Log.d(TAG , ".createUri starts")

        // we starting off parsing the base url on line 45 , that's create a uri object , which I've called
        // we need to build a params on top of that

        // builder using to build Upon command on line 48
        return Uri.parse(baseURL).
        buildUpon().
        appendQueryParameter("tags", searchCriteria).
        appendQueryParameter("tagmode", if (matchAll) "ALL" else "ANY").
        appendQueryParameter("lang", lang).
        appendQueryParameter("format", "json").
        appendQueryParameter("nojsoncallback", "1").
        build().toString()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.d(TAG, "onCreateOptionsMenu called")
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.d(TAG, "onOptionsItemSelected called")
        return when (item.itemId) {
            R.id.action_search -> {
                startActivity(Intent(this , SearchActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onDownloadComplete(data: String, status: DownloadStatus) {
        if (status == DownloadStatus.OK) {
            Log.d(TAG, "onDownloadComplete called")

            val getFlickrJsonData = GetFlickrJsonData(this)
            getFlickrJsonData.execute(data)
        } else {
            // download failed
            Log.d(TAG, "onDownloadComplete failed with status $status. Error message is: $data")
        }
    }

    override fun onDataAvailable(data: List<Photo>) {
        Log.d(TAG, ".onDataAvailable called ")
        flickrRecyclerViewAdapter.loadNewData(data)
        Log.d(TAG, ".onDataAvailable ends")
    }

    override fun onError(exception: Exception) {
        Log.e(TAG, "onError called with ${exception.message}")
    }


    override fun onBackPressed() {
        super.onBackPressed()

        Log.d(TAG , "onBackPressed : Called")
        val sharedPerf = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        sharedPerf.edit().putString(FLICKR_QUERY , " ").apply()

    }

    override fun onResume() {

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val queryResult = sharedPref.getString(FLICKR_QUERY , " ")

        if (queryResult!!.isNotEmpty()){

            val url = createUri("https://api.flickr.com/services/feeds/photos_public.gne" ,queryResult, "en-us" , true )
            val getRawData = GetRawData(this)
            getRawData.execute(url)
        }

        super.onResume()
    }

}
