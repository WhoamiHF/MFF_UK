package com.example.mffuk

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import com.google.android.material.tabs.TabLayout
import lecture
import java.lang.reflect.Type
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_add_lecture.*
import kotlinx.android.synthetic.main.lecture.*
import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.math.log
import androidx.core.app.ActivityCompat

import android.content.pm.PackageManager

import android.app.Activity
import android.graphics.Color
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_schedule.*
import java.lang.Integer.parseInt

class add_lecture : AppCompatActivity() {
    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor
    private lateinit var list: MutableList<lecture>

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    fun verifyStoragePermissions(activity: Activity?) {
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_lecture)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        editor = sharedPreferences.edit()
        list = getList()

        val btnDone: Button = findViewById (R.id.btnBack)
        btnDone.setOnClickListener() {
            val intent = Intent(this, Schedule::class.java)
            startActivity(intent)
        }

        val btnCsv: Button = findViewById((R.id.btnCsv))
        btnCsv.setOnClickListener(){
            verifyStoragePermissions(this)
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 777)
        }

        val btnDelete: Button = findViewById(R.id.btnRemove)
        btnDelete.setOnClickListener(){
                editor.remove("MffLectures")
                editor.commit()
        }

        val btnAdd: Button = findViewById (R.id.btnAdd)
        btnAdd.setOnClickListener() {
            addNewLecture()
        }
    }

    private fun checkHours(hours : Int) :Boolean{
        return hours in 0..23
    }

    private fun checkMinutes(minutes : Int) :Boolean{
        return minutes in 0..59
    }

    private fun checkLectureTime(startArray : List<String>,endArray: List<String>) : Boolean{
        if(startArray.size != 2 || endArray.size != 2){
            return false
        }

        try {
            val startHours = parseInt(startArray[0])
            val startMinutes = parseInt(startArray[1])
            val endHours = parseInt(endArray[0])
            val endMinutes = parseInt(endArray[1])

            if(!checkHours(startHours) || !checkMinutes(startMinutes) || !checkHours(endHours) || !checkMinutes(endMinutes)){
                return false
            }
        } catch (e: NumberFormatException) {
            return false
        }

        return true
    }
    private fun addNewLecture(){
        val startArray : List<String> = ettStart.text.split(":")
        val endArray : List<String> = ettEnd.text.split(":")
        if(!checkLectureTime(startArray,endArray)){
            ettStart.setTextColor(Color.parseColor("#ff0000"))
            ettStart.setHintTextColor(Color.parseColor("#ff0000"))
            ettEnd.setTextColor(Color.parseColor("#ff0000"))
            ettEnd.setHintTextColor(Color.parseColor("#ff0000"))
            return
        }
        val lectureName : String = etSubject.text.toString()
        val lectureTeacher : String = etLecturer.text.toString()
        val lectureCode : String = etCode.text.toString()
        val lectureRoom : String = etRoom.text.toString()
        val lectureTalk : Boolean = sLection.isChecked

        val lectureStart : Int =  60 * startArray[0].toInt() + startArray[1].toInt()
        val lectureEnd : Int =  60 * endArray[0].toInt() + endArray[1].toInt()

        val lec = lecture(tlWeekday.selectedTabPosition,lectureStart,lectureEnd,lectureName,lectureTeacher,lectureTalk,lectureRoom,lectureCode)
        list.add(lec)

        clearLecture()
        setList()
    }
    private fun clearLecture(){
        etSubject.text.clear()
        etLecturer.text.clear()
        etCode.text.clear()
        etRoom.text.clear()
        sLection.isChecked = false
        ettStart.text.clear()
        ettEnd.text.clear()
        ettStart.setTextColor(Color.parseColor("#000000"))
        ettStart.setHintTextColor(Color.parseColor("#000000"))
        ettEnd.setTextColor(Color.parseColor("#000000"))
        ettEnd.setHintTextColor(Color.parseColor("#000000"))
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 777) {
            var fileName = data?.data?.path
            fileName = fileName!!.split(":")[1]
            try {
                val lines: MutableList<String> = File(fileName).readLines() as MutableList<String>
                lines.removeFirst()
                lines.forEach { line -> addLectureFromCsvLine(line) }
                setList()
            }
            catch(ex:Exception){
                Log.e("MMDSKJFG",ex.message.toString())
            }
        }
    }

    private fun addLectureFromCsvLine(line :String){
        val tokens : List<String> =  line.split(";")
        list.add(lecture(
            tokens[4].toInt()-1,
            tokens[5].toInt(),
            tokens[5].toInt()+ tokens[7].toInt(),
            tokens[3],
            tokens[12],
            tokens[1][tokens[1].length-2]=='p',
            tokens[6],
            tokens[2],
        ))
    }

    private fun getList(): MutableList<lecture> {
        var arrayItems: MutableList<lecture> = mutableListOf()
        val serializedObject = sharedPreferences.getString("MffLectures", null)

        if (serializedObject != null) {
            val gson = Gson()
            val type: Type = object : TypeToken<List<lecture?>?>() {}.type
            arrayItems = gson.fromJson<MutableList<lecture>>(serializedObject, type)
        }
        return arrayItems
    }

    private fun setList() {
        val gson = Gson()
        val json: String = gson.toJson(list)

        editor.putString("MffLectures", json)
        editor.commit()
    }
}