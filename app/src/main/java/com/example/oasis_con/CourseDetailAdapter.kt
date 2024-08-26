package com.example.oasis_con

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CourseDetailAdapter(
    private var items: List<CourseDetail>,
    private val apiService: ApiService,
    private val serviceKey: String
) : RecyclerView.Adapter<CourseDetailAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.ivCourseImage)
        val name: TextView = view.findViewById(R.id.tvDetailName)
        val description: TextView = view.findViewById(R.id.tvDetailDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.subname
        holder.description.text = item.subdetailoverview
        loadDetailAndImage(item.subcontentid, holder)
    }

    private fun loadDetailAndImage(contentId: String, holder: ViewHolder) {
        apiService.getDetailCommon(serviceKey, contentId).enqueue(object : Callback<DetailCommonResponse> {
            override fun onResponse(call: Call<DetailCommonResponse>, response: Response<DetailCommonResponse>) {
                if (response.isSuccessful) {
                    val detailItem = response.body()?.response?.body?.items?.firstOrNull()
                    if (detailItem != null) {
                        holder.name.text = detailItem.title ?: holder.name.text
                        holder.description.text = detailItem.overview ?: holder.description.text

                        if (!detailItem.firstimage.isNullOrEmpty()) {
                            holder.image.visibility = View.VISIBLE
                            Glide.with(holder.image.context)
                                .load(detailItem.firstimage)
                                .into(holder.image)
                        } else {
                            holder.image.visibility = View.GONE
                        }
                    }
                } else {
                    holder.image.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<DetailCommonResponse>, t: Throwable) {
                holder.image.visibility = View.GONE
            }
        })
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<CourseDetail>) {
        items = newItems
        notifyDataSetChanged()
    }
}