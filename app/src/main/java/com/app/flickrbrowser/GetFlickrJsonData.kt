package com.app.flickrbrowser
import android.os.AsyncTask
import android.util.Log
import org.json.JSONException
import org.json.JSONObject


private const val TAG = "GetFlickrJs"


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class GetFlickrJsonData(private val listener : OnDataAvailable) : AsyncTask<String, Void , ArrayList<Photo>>() {

    interface OnDataAvailable{
            fun onDataAvailable(data : List<Photo>)
            fun onError(exception: Exception)
    }


    override fun doInBackground(vararg params: String?): ArrayList<Photo> {
        Log.d(TAG , "doInBackground : Starts")

        val photoList = ArrayList<Photo>()
        try{
            val jsonData = JSONObject(params[0])
            // it gets a json array object from the data by calling the get json array fun
            val itemsArray = jsonData.getJSONArray("items")

            // we're retrieving the json object one by one and pulling out the string values
            //keep looping until it's all the items in the array
            //we'll then end up with a list containing the details for each of the photos in the feeds

            for(i in 0 until itemsArray.length()){
            val jsonPhoto = itemsArray.getJSONObject(i)
            val title = jsonPhoto.getString("title")
            val author = jsonPhoto.getString("author")
            val authorId = jsonPhoto.getString("author_id")
            val tags = jsonPhoto.getString("tags")

            val jsonMedia = jsonPhoto.getJSONObject("media")
            val photoUrl = jsonMedia.getString("m")
                // so image will give us the url or the photo to show in the initial list,
                // and link will provide the url of the full size picture
            val link = photoUrl.replaceFirst("_m.jpg" , "_b.jpg")

                val photoObject = Photo(title , author ,authorId ,link, tags , photoUrl)
                photoList.add(photoObject)
                Log.d(TAG , "doInBackground $photoObject")
        }

        }  catch (e : JSONException){
            e.printStackTrace()
            Log.e(TAG , "doInBackground : Error Processing Json data. ${e.message}")

            cancel(true)
            listener.onError(e)


        }
         Log.d(TAG , ".doInBackground ends")
        return photoList
    }

    override fun onPostExecute(result: ArrayList<Photo>) {
        Log.d(TAG , "onPostExecute : Starts")
        super.onPostExecute(result)
        // will send the list of photo back to the listener
        // when doInBackground finishes , the onPostExecute function will be calling

        listener.onDataAvailable(result)
        Log.d(TAG , "onPostExecute : Ends")

    }





}