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




class add_lecture : AppCompatActivity() {
    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor
    private lateinit var list: MutableList<lecture>

    // Storage Permissions
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    fun verifyStoragePermissions(activity: Activity?) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
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
        editor = sharedPreferences.edit();
        list = getList()

        var currentDay : Int = 0
        tlWeekday.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                currentDay = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        val btnCsv: Button = findViewById((R.id.btnCsv))
        btnCsv.setOnClickListener(){
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 777)
        }
        val btnAdd: Button = findViewById (R.id.btnAdd)
        btnAdd.setOnClickListener() {
            val LectureName : String = etSubject.text.toString()
            val LectureTeacher : String = etLecturer.text.toString()
            val LectureCode : String = etCode.text.toString()
            val LectureRoom : String = etRoom.text.toString()
            val LectureTalk : Boolean = sLection.isChecked
            val StartArray : List<String> = ettStart.text.split(":")
            val LectureStart : Int =  60 * StartArray[0].toInt() + StartArray[1].toInt()
            val EndArray : List<String> = ettEnd.text.split(":")
            val LectureEnd : Int =  60 * EndArray[0].toInt() + EndArray[1].toInt()

            var lec = lecture(currentDay,LectureStart,LectureEnd,LectureName,LectureTeacher,LectureTalk,LectureRoom,LectureCode)
            list.add(lec)
            setList()
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 777) {
            val btnCsv: Button = findViewById((R.id.btnCsv))
            var fileName = data?.data?.path
            fileName = fileName!!.split(":")[1]
            btnCsv.text = fileName
            try {
                verifyStoragePermissions(this)
                val lines: MutableList<String> = File(fileName).readLines() as MutableList<String>
                lines.removeFirst()
                lines.forEach { line -> println(line) }
            }
            catch(ex:Exception){
                Log.e("MMDSKJFG",ex.message.toString())
            }


        }
    }

    private fun setList() {
        val gson = Gson()
        val json: String = gson.toJson(list)

        editor.putString("MffLectures", json)
        editor.commit()
    }
}