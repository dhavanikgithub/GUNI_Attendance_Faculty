package com.example.guniattendancefaculty.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.guniattendancefaculty.data.entity.Classes
import com.example.guniattendancefaculty.databinding.ItemListClassBinding

class ClassAdapter : RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Classes>() {
        override fun areItemsTheSame(oldItem: Classes, newItem: Classes): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Classes, newItem: Classes): Boolean {
            return oldItem.uid == newItem.uid
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var classes: List<Classes>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    inner class ClassViewHolder(binding: ItemListClassBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvClassName: TextView = binding.tvClassName
        val tvClassType: TextView = binding.tvClassType
        val ivEdit: AppCompatImageButton = binding.ivEdit
        val ivDelete: AppCompatImageButton = binding.ivDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val binding = ItemListClassBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ClassViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val clas = classes[position]
        holder.apply {
            tvClassName.text = clas.className
            tvClassType.text = clas.type

            ivEdit.setOnClickListener {
                onEditClickListener?.let {
                    it(clas)
                }
            }

            ivDelete.setOnClickListener {
                onDeleteClickListener?.let {
                    it(clas)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return classes.size
    }

    private var onEditClickListener: ((Classes) -> Unit)? = null

    fun setOnEditClickListener(listener: (Classes) -> Unit) {
        onEditClickListener = listener
    }

    private var onDeleteClickListener: ((Classes) -> Unit)? = null

    fun setOnDeleteClickListener(listener: (Classes) -> Unit) {
        onDeleteClickListener = listener
    }
}