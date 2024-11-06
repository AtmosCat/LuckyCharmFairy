package com.luckycharmfairy.presentation.mymatches.addmatches

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.luckycharmfairy.data.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.R

class ViewPagerAdapter(private val imageUrls: MutableList<String>, private val userViewModel: UserViewModel) : RecyclerView.Adapter<ViewPagerAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
//        holder.itemView.setOnLongClickListener {
//            itemClick?.onClick(it, position)
//            true
//        }

        holder.imageView.load(imageUrl) {
            placeholder(R.drawable.placeholder)
            error(R.drawable.error_image)
        }

    }

    override fun getItemCount(): Int = imageUrls.size
}
