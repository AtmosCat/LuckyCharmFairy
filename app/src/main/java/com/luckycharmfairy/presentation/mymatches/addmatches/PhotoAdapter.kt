package com.luckycharmfairy.presentation.mymatches.addmatches

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.luckycharmfairy.luckycharmfairy.R
import com.luckycharmfairy.luckycharmfairy.databinding.RecyclerviewPhotoBinding

class PhotoAdapter :
    ListAdapter<String, PhotoAdapter.Holder>(object :
        DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }) {

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick : ItemClick? = null

    // RecyclerView 돌아갈 때 새로운 뷰 홀더 생성 및 초기화
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            RecyclerviewPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    // 홀더에 실제 데이터 할당
    override fun onBindViewHolder(holder: Holder, position: Int) {
        runCatching {
            holder.bind(getItem(position))
            holder.btnDeletePhoto.setOnClickListener {
                itemClick?.onClick(it, position)
            }
        }.onFailure { exception ->
            Log.e("PhotoAdapter", "Exception! ${exception.message}")
        }
    }

    class Holder(binding: RecyclerviewPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val photo = binding.ivPhoto
        val btnDeletePhoto = binding.btnDeletePhoto

        fun bind(item: String) {
            photo.load(item) {
                placeholder(R.drawable.placeholder)
                error(R.drawable.error_image)
                listener(
                    onSuccess = { _, result ->
                        Log.d("Coil", "Image load succeeded")
                    },
                    onError = { _, result ->
                        Log.e("Coil", "Image load failed: ${result.throwable.message}")
                    }
                )
            }
        }

    }
    fun updateData(newItems: List<String>) {
        submitList(newItems)
        notifyDataSetChanged()
    }
}