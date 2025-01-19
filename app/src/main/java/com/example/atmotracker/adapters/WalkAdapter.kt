package com.example.atmotracker.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.atmotracker.R
import com.example.atmotracker.databinding.ItemWalkBinding
import com.example.atmotracker.model.Walk
import java.util.Locale

class WalkAdapter(
    private val walks: MutableList<Walk>,
    private val onWalkRemoved: (Walk) -> Unit,
    private val sharedPreferences: SharedPreferences
) : RecyclerView.Adapter<WalkAdapter.WalkViewHolder>() {

    class WalkViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemWalkBinding.bind(view)
        val stepsData: TextView = binding.stepsData
        val caloriesData: TextView = binding.caloriesData
        val dateData: TextView = binding.dateWalkedData
        val timeSpentData: TextView = binding.timeData
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_walk, parent, false)
        return WalkViewHolder(view)
    }

    override fun onBindViewHolder(holder: WalkViewHolder, position: Int) {
        val walk = walks[position]
        holder.stepsData.text = walk.stepCount.toString()
        holder.caloriesData.text = String.format(Locale.getDefault(),"%.2f kcal", walk.caloriesBurned)
        holder.dateData.text = walk.date
        holder.timeSpentData.text = formatTime(walk.timeSpent)

        holder.itemView.setOnLongClickListener {
            showDeleteConfirmationDialog(holder.itemView.context, walk, position)
            true
        }
    }

    override fun getItemCount() = walks.size

    private fun showDeleteConfirmationDialog(context: Context, book: Walk, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Delete Walk")
            .setMessage("Are you sure you want to delete this walk data?")
            .setPositiveButton("Yes") { _, _ ->
                removeBook(position)
                onWalkRemoved(book)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun removeBook(position: Int) {
        walks.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, walks.size)
    }

    private fun formatTime(timeInSeconds: Long): String {
        val hours = timeInSeconds / 3600
        val minutes = (timeInSeconds % 3600) / 60
        val seconds = timeInSeconds % 60

        return when {
            hours > 0 -> String.format(Locale.getDefault(),"%d:%02d:%02d hours", hours, minutes, seconds)
            minutes > 1 -> String.format(Locale.getDefault(),"%d:%02d minutes", minutes, seconds)
            minutes == 1L -> String.format(Locale.getDefault(),"%d:%02d minute", minutes, seconds)
            else -> String.format(Locale.getDefault(),"%d seconds", seconds)
        }
    }
}