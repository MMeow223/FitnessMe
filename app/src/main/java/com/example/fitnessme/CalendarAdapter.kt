package com.example.fitnessme

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat


class CalendarAdapter(val calendarList: ArrayList<Calendar>) :
    RecyclerView.Adapter<CalendarAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var nameTextView: TextView = itemView.findViewById(R.id.textView1)
        private var timeTextView: TextView = itemView.findViewById(R.id.textView2)
        private var locationTextView: TextView = itemView.findViewById(R.id.textView3)

        fun bind(calendar: Calendar) {
//            val mSimpleDateFormat = SimpleDateFormat("HH:mm")
//            val mStartTime = mSimpleDateFormat.parse(calendar.from.toDate().time.toString())
//            val mEndTime = mSimpleDateFormat.parse(calendar.to.toDate().time.toString())

            // convert timestamp to time format
            // check if is am or pm


            val mSimpleDateFormat = SimpleDateFormat("HH:mm")
            val mStartTime = mSimpleDateFormat.format(calendar.from.toDate())
            val mEndTime = mSimpleDateFormat.format(calendar.to.toDate())
            // am or pm
            val mStartTimeAmPm = if (mStartTime.substring(0, 2).toInt() > 12) "PM" else "AM"
            val mEndTimeAmPm = if (mEndTime.substring(0, 2).toInt() > 12) "PM" else "AM"

            //shrot hand if else
            nameTextView.text = calendar.name
            timeTextView.text = "$mStartTime $mStartTimeAmPm to $mEndTime $mEndTimeAmPm"
            locationTextView.text = calendar.location
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_recycler_view_item, parent, false)
        return RecipeViewHolder(view)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return calendarList.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(calendarList[position])

        var cardView = holder.itemView.findViewById<CardView>(R.id.editInfoCardView)

        cardView.setOnClickListener {

            // Event start and end time with date
            val startTime = calendarList[position].from
            val endTime = calendarList[position].to

            // Parsing the date and time
            val mSimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            var passStartTime = mSimpleDateFormat.parse(mSimpleDateFormat.format(startTime.toDate()))
            var passEndTime = mSimpleDateFormat.parse(mSimpleDateFormat.format(endTime.toDate()))


//            val mEndTime = mSimpleDateFormat.parse(endTime.toDate().toString())

            val mSimpleDateFormat2 = SimpleDateFormat("HH:mm")
            val mStartTime2 = mSimpleDateFormat2.format(startTime.toDate())
            val mEndTime2 = mSimpleDateFormat2.format(endTime.toDate())
            // am or pm
            val mStartTimeAmPm = if (mStartTime2.substring(0, 2).toInt() > 12) "PM" else "AM"
            val mEndTimeAmPm = if (mEndTime2.substring(0, 2).toInt() > 12) "PM" else "AM"




            var fromtotimedisplay = " from $mStartTime2$mStartTimeAmPm to $mEndTime2$mEndTimeAmPm"

            val mIntent = Intent(Intent.ACTION_EDIT)
            mIntent.type = "vnd.android.cursor.item/event"
            mIntent.putExtra("beginTime", passStartTime.time)
            mIntent.putExtra("time", true)
            mIntent.putExtra("rule", "FREQ=YEARLY")
            mIntent.putExtra("endTime", passEndTime.time)
            mIntent.putExtra("title", calendarList[position].name)
            mIntent.putExtra("description",
                "${calendarList[position].name} $fromtotimedisplay at ${calendarList[position].location}"
            )
            mIntent.putExtra("eventLocation",  calendarList[position].location)
            startActivity(holder.itemView.context, mIntent, null)

        }

    }
}
