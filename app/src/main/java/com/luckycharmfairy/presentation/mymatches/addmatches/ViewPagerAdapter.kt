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
        val btnClose: ImageView = itemView.findViewById(R.id.viewpager_btn_close) // 버튼 초기화
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
        holder.itemView.setOnLongClickListener {
            itemClick?.onClick(it, position)
            true
        }

        holder.imageView.load(imageUrl) {
            placeholder(R.drawable.placeholder)
            error(R.drawable.error_image)
        }

        // btnClose 클릭 리스너 설정
        holder.btnClose.setOnClickListener {
            val adapterPosition = holder.adapterPosition // 현재 어댑터에서의 위치 가져오기
            if (adapterPosition != RecyclerView.NO_POSITION) { // 유효한 위치인지 확인
                imageUrls.removeAt(adapterPosition)
                userViewModel.temporaryImageUrls.value!!.removeAt(position)
                notifyItemRemoved(adapterPosition)
                notifyItemRangeChanged(adapterPosition, imageUrls.size) // 남은 아이템의 위치 업데이트
            }
        }
    }

    override fun getItemCount(): Int = imageUrls.size
}
