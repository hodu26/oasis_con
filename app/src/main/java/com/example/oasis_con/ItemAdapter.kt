import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.oasis_con.CourseDetailActivity
import com.example.oasis_con.Item
import com.example.oasis_con.R

class ItemAdapter(private var items: List<Item>) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.ivFirstImage)
        val title: TextView = view.findViewById(R.id.tvTitle)
        val address: TextView = view.findViewById(R.id.tvAddress)
        val distance: TextView = view.findViewById(R.id.tvDistance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_api_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.address.text = "${item.addr1} ${item.addr2}"
        holder.distance.text = "거리: %.2f km".format(item.distance)

        // 이미지 로드
        Glide.with(holder.image.context)
            .load(item.firstimage)
            .into(holder.image)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, CourseDetailActivity::class.java)
            intent.putExtra("contentId", item.contentid)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<Item>) {
        items = newItems
        notifyDataSetChanged()
    }
}