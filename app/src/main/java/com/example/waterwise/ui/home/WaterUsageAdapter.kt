import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.waterwise.R
import com.example.waterwise.ui.home.WaterUsageRecord

class WaterUsageAdapter(private val waterUsageRecords: List<WaterUsageRecord>) :
    RecyclerView.Adapter<WaterUsageAdapter.WaterUsageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaterUsageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_water_usage, parent, false)
        return WaterUsageViewHolder(view)
    }

    override fun onBindViewHolder(holder: WaterUsageViewHolder, position: Int) {
        val record = waterUsageRecords[position]
        holder.bind(record)
    }

    override fun getItemCount(): Int = waterUsageRecords.size

    class WaterUsageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.date_text_view)
        private val usageTextView: TextView = itemView.findViewById(R.id.usage_text_view)
        private val dayTextView: TextView = itemView.findViewById(R.id.day_text_view)

        fun bind(record: WaterUsageRecord) {
            dateTextView.text = record.date
            usageTextView.text = "${record.usage} L" // Convert Double to String with unit
            dayTextView.text = record.dayOfWeek
        }
    }


}
