package com.example.mffuk


import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_schedule.*
import kotlinx.android.synthetic.main.lecture.view.*
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Button
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
    private lateinit var list: MutableList<lecture>

    fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        editor = sharedPreferences.edit()
        list = getList()

        val btnAdd: FloatingActionButton = findViewById (R.id.fabAdd)
        btnAdd.setOnClickListener() {
            val intent = Intent(this, add_lecture::class.java)
            startActivity(intent)
        }

        buildSchedule()
    }

    fun buildDaysHeader(height:Int,width:Int){
        val days = arrayOf("Po","Út","St","Čt","Pá")
        var i = 0
        for(day in days) {
            val current = TextView(this)
            current.text = day
            current.x = 0.toFloat()
            current.y = (height*0.95/5*i+height/20).toFloat()
            current.layoutParams = RelativeLayout.LayoutParams(width/20, height/5)
            rlSchedule.addView(current)
            i = i + 1
        }
    }

    fun buildHoursHeader(first_lecture_start: Int,difference: Int,width: Int){
        var current = first_lecture_start
        while(current < first_lecture_start + difference){
            val currentView = TextView(this)
            currentView.text = (current/60).toString().plus(":").plus(current%60).toString()
            currentView.x = (width*0.95/difference * (current-first_lecture_start)+width/20).toFloat()
            currentView.y = 0.toFloat()
            rlSchedule.addView(currentView)
            current = current + 100
        }
    }
    fun buildSchedule(){

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels



        val first_lecture_start = 9*60
        val last_lecture_end = 19*60
        val difference = last_lecture_end - first_lecture_start
        val availableHeight = (height-getStatusBarHeight())

        buildDaysHeader(availableHeight,width)
        buildHoursHeader(first_lecture_start,difference,width)

        for (item in list) {
            rlSchedule.addView(buildLectureView(item,width,difference,first_lecture_start,availableHeight))
        }
    }

    fun buildLectureView(item:lecture,width : Int,difference : Int,first_lecture_start:Int,availableHeight:Int) :View{
        val inflater = LayoutInflater.from(this)
        val lec = inflater.inflate(R.layout.lecture, null, false);
        lec.tvLectureLecturer.text = item.lecturer
        lec.tvLectureCode.text = item.code
        lec.tvLectureRoom.text = item.room
        lec.tvLectureSubject.text = item.subject
        lec.tvLectureTime.text = (item.startTime/60).toString().plus(":").plus(item.startTime%60)
        val start = item.startTime-first_lecture_start
        lec.x = (width*0.95/(difference)*start+width/20).toFloat()
        lec.y = (availableHeight*0.95/5*item.day+availableHeight/20).toFloat()
        if(item.lection){
            lec.setBackgroundResource(R.drawable.dark_blue_with_border)
        }
        else{
            lec.setBackgroundResource(R.drawable.light_blue_with_border)
        }
        val duration = item.endTime - item.startTime
        lec.layoutParams = RelativeLayout.LayoutParams(width/difference*duration, availableHeight/5*95/100)
        return lec
    }

    fun getList(): MutableList<lecture> {
        var arrayItems: MutableList<lecture> = mutableListOf()
        val serializedObject = sharedPreferences.getString("MffLectures", null)

        if (serializedObject != null) {
            val gson = Gson()
            val type: Type = object : TypeToken<List<lecture?>?>() {}.type
            arrayItems = gson.fromJson<MutableList<lecture>>(serializedObject, type)
        }
        return arrayItems
    }
}