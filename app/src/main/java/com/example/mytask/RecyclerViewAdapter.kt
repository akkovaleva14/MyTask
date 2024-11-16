package com.example.mytask

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import com.example.mytask.databinding.ItemCardBinding
import com.example.mytask.databinding.ItemHeaderBinding
import java.io.Serializable

sealed class RecyclerViewItem : Serializable {
    data class Header(val title: String) : RecyclerViewItem()
    data class Card(val imageUrl: String, var description: String) : RecyclerViewItem()
}

enum class ViewType(val type: Int) {
    HEADER(0),
    CARD(1)
}

class HeaderViewHolder(private val binding: ItemHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(header: RecyclerViewItem.Header) {
        binding.headerTextView.text = header.title
    }
}

class CardViewHolder(private val binding: ItemCardBinding, private val context: Context) :
    RecyclerView.ViewHolder(binding.root) {

    private val imageLoader: ImageLoader by lazy {
        ImageLoader.Builder(context)
            .okHttpClient {
                OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS) // Тайм-аут на подключение
                    .readTimeout(10, TimeUnit.SECONDS)    // Тайм-аут на чтение
                    .build()
            }
            .build()
    }

    fun bind(card: RecyclerViewItem.Card) {
        binding.cardTextView.text = card.description
        binding.cardImageView.load(card.imageUrl, imageLoader) {
            placeholder(R.drawable.placeholder)
            error(R.drawable.error)
            memoryCachePolicy(CachePolicy.ENABLED)
        }
    }

    fun updateDescription(newDescription: String) {
        binding.cardTextView.text = newDescription
    }
}

class RecyclerViewAdapter(
    private val onItemClick: (RecyclerViewItem) -> Unit,
    private val context: Context
) : ListAdapter<RecyclerViewItem, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is RecyclerViewItem.Header -> ViewType.HEADER.type
            is RecyclerViewItem.Card -> ViewType.CARD.type
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.HEADER.type -> HeaderViewHolder(
                ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            ViewType.CARD.type -> CardViewHolder(
                ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                context
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is RecyclerViewItem.Header -> (holder as HeaderViewHolder).bind(item)
            is RecyclerViewItem.Card -> {
                (holder as CardViewHolder).bind(item)
                holder.itemView.setOnClickListener {
                    onItemClick(item)
                }
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
        if (payloads.isNotEmpty()) {
            when (val item = getItem(position)) {
                is RecyclerViewItem.Header -> (holder as HeaderViewHolder).bind(item)
                is RecyclerViewItem.Card -> {
                    val descriptionPayload = payloads.first() as? String
                    if (descriptionPayload != null) {
                        (holder as CardViewHolder).updateDescription(descriptionPayload)
                    } else {
                        (holder as CardViewHolder).bind(item)
                    }
                }
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<RecyclerViewItem>() {
        override fun areItemsTheSame(
            oldItem: RecyclerViewItem,
            newItem: RecyclerViewItem
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: RecyclerViewItem,
            newItem: RecyclerViewItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: RecyclerViewItem, newItem: RecyclerViewItem): Any? {
            return when {
                oldItem is RecyclerViewItem.Card && newItem is RecyclerViewItem.Card &&
                        oldItem.description != newItem.description -> newItem.description

                else -> null
            }
        }
    }
}
