package com.app.flickrbrowser

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import learnprogramming.academy.R

private const val TAG = "RecyclerViewAdapt"

// the adapter takes the data from the data source , in our case the list of photo
// Package the data view holder , the view holder send to the recycler view whenever requests more data
// the recycler view will send back the view , as they scroll off the screen
// it will be job of the adapter to fetch the thumbnail as the list is scroll up and down
// the thumbnail being downloaded

class FlickrImageViewHolder(view: View) : RecyclerView.ViewHolder(view){

    var thumbnail : ImageView = view.findViewById(R.id.thumbnail)
    var title: TextView = view.findViewById(R.id.title)

}

class FlickrRecyclerViewAdapter(private var photoList : List<Photo>) : RecyclerView.Adapter<FlickrImageViewHolder>() {

    // this fun to retrieve the details for the photo from its position
    override fun getItemCount(): Int {
        Log.d(TAG , "getItemCount : called")
        return if(photoList.isNotEmpty()) photoList.size else 1
    }

    fun loadNewData(newPhotos : List<Photo>){
        // this fun takes a new list as a parameter and Store it in the photo list field
        photoList = newPhotos
        // notifyDataSetChanged() fun tells the recyclerview adapter that the data has changed so that it can refresh
        notifyDataSetChanged()
    }


    fun getPhoto(position: Int): Photo?{
        return if(photoList.isNotEmpty())photoList[position] else null

    }

    // called by the recyclerview when it's wants new data to be stored in a view holder
    override fun onBindViewHolder(holder: FlickrImageViewHolder, position: Int) {

   // Picasso to cache the photo
        // Picasso will download the image from the URL on a background thread and it puts it into the image
        // more info please visit https://square.github.io/picasso/

        // Called by the layout  Manager when it wants new data in an existing view




        if(photoList.isEmpty()){
            holder.thumbnail.setImageResource(R.drawable.placeholder)
            holder.title.setText(R.string.empty_photo)
        }
        else{
            val photoItem = photoList[position]

            Picasso.get().load(photoItem.image).
            error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(holder.thumbnail)


            holder.title.text = photoItem.title

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlickrImageViewHolder {
        // inflate view from the browser.xml layout to and return that view
        // called by layout manager when it needs new view

        Log.d(TAG , "onCreateViewHolder : Requested new view")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.browse, parent , false)

        return FlickrImageViewHolder(view)
    }

}