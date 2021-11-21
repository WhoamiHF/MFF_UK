package com.example.mffuk


import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_schedule.*
import kotlinx.android.synthetic.main.lecture.view.*
import android.util.DisplayMetrics
import android.view.*
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import lecture
import java.lang.reflect.Type

class Schedule : AppCompatActivity() {
    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor
    private lateinit var lectureList: MutableList<lecture>
    private var width: Int = 0
    private var height: Int = 0
    private val menuWidthPart : Int = 20
    private val menuHeightPart : Int = 20
    private var firstLectureStart : Int = 24*60
    private var lastLectureEnd : Int = 0*60

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        editor = sharedPreferences.edit()
        lectureList = getList()

        val btnAdd: FloatingActionButton = findViewById (R.id.fabAdd)
        btnAdd.setOnClickListener {
            val intent = Intent(this, add_lecture::class.java)
            startActivity(intent)
        }
        getAvailableWidthAndHeight()
        buildSchedule()
    }

    private fun getAvailableWidthAndHeight(){
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        height = displayMetrics.heightPixels
        width = displayMetrics.widthPixels
        height = (height-getStatusBarHeight())
    }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    private fun buildDaysHeader(){
        val days = arrayOf("Po","Út","St","Čt","Pá")
        var i = 0
        for(day in days) {
            val current = TextView(this)
            current.text = day

            val start = height*0.95*i/5
            val offset = height/menuHeightPart

            current.x = 0.toFloat()
            current.y = (start+offset).toFloat()
            current.layoutParams = RelativeLayout.LayoutParams(width/menuWidthPart, height/5)
            rlSchedule.addView(current)
            i += 1
        }
    }
    private fun formatMinutes(time:String):String
    {
        return if(time.length == 1){
            0.toString().plus(time)
        } else{
            time
        }
    }

    private fun timeToXCoordinate(time: Int): Float {
        val availableWidth: Float = (width * 0.95).toFloat()
        val widthPerMinute = availableWidth / (lastLectureEnd - firstLectureStart)
        val xWithoutOffset = widthPerMinute * (time - firstLectureStart)
        return xWithoutOffset + width / menuWidthPart
    }

    private fun buildHoursHeader(difference: Int){
        var current = firstLectureStart
        while(current < firstLectureStart + difference){
            val currentView = TextView(this)

            val hours = (current/60).toString()
            var minutes = (current%60).toString()
            minutes = formatMinutes(minutes)

            currentView.text = hours.plus(":").plus(minutes)

            currentView.x = timeToXCoordinate(current)
            currentView.y = 0.toFloat()

            rlSchedule.addView(currentView)

            current += 100
        }
    }

    private fun setLecturesTimeRange(){
        for(item in lectureList){
            if(item.startTime < firstLectureStart){
                firstLectureStart = item.startTime
            }
            if(item.endTime > lastLectureEnd){
                lastLectureEnd = item.endTime
            }
        }

        if(lastLectureEnd - firstLectureStart < 3*90+20){
            when {
                firstLectureStart <= 9*60 -> {
                    lastLectureEnd += 2*90+20
                }
                lastLectureEnd >= 17*60+20 -> {
                    firstLectureStart -= 2*90+20
                }
                else -> {
                    lastLectureEnd += 90+10
                    firstLectureStart -= 90+10
                }
            }
        }
    }

    private fun buildSchedule(){
        setLecturesTimeRange()
        val difference = lastLectureEnd - firstLectureStart

        buildDaysHeader()
        buildHoursHeader(difference)

        for (item in lectureList) {
            rlSchedule.addView(buildLectureView(item))
        }
    }

    private fun buildLectureView(item:lecture) :View{
        val inflater = LayoutInflater.from(this)
        val lec = inflater.inflate(R.layout.lecture, null, false)

        lec.tvLectureLecturer.text = item.lecturer
        lec.tvLectureCode.text = item.code
        lec.tvLectureRoom.text = item.room
        lec.tvLectureSubject.text = item.subject

        val hours = (item.startTime/60).toString()
        var minutes = (item.startTime%60).toString()
        minutes = formatMinutes(minutes)
        lec.tvLectureTime.text = hours.plus(":").plus(minutes)

        lec.x = timeToXCoordinate(item.startTime)
        lec.y = (height*0.95/5*item.day+height/20).toFloat()

        val lectureEndX = timeToXCoordinate(item.endTime)
        lec.layoutParams = RelativeLayout.LayoutParams((lectureEndX -  lec.x).toInt(), height/5*95/100)

        if(item.lection){
            lec.setBackgroundResource(R.drawable.dark_blue_with_border)
        }
        else{
            lec.setBackgroundResource(R.drawable.light_blue_with_border)
        }
        return lec
    }

    private fun getList(): MutableList<lecture> {
        var arrayItems: MutableList<lecture> = mutableListOf()
        val serializedObject = sharedPreferences.getString("MffLectures", null)

        if (serializedObject != null) {
            val gson = Gson()
            val type: Type = object : TypeToken<List<lecture?>?>() {}.type
            arrayItems = gson.fromJson(serializedObject, type)
        }
        return arrayItems
    }
}