package com.example.mffuk

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CompoundButton
import android.widget.Switch
import androidx.appcompat.widget.SwitchCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.mffuk.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker




enum class markerTypes {
    KOLEJ,MENZA,MISTO,BUDOVA
}
data class PlaceToMark(
    var longtitude : Double = 50.115928,
    var latitude : Double = 50.115928,
    var type : markerTypes = markerTypes.KOLEJ,
    var text : String = "Marker"
)
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var markers : MutableList<PlaceToMark> = mutableListOf()
    private var kolejMarkers: MutableList<Marker> = ArrayList()
    private var menzaMarkers: MutableList<Marker> = ArrayList()
    private var mistoMarkers: MutableList<Marker> = ArrayList()
    private var budovaMarkers: MutableList<Marker> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val sBudovy: SwitchCompat = findViewById (R.id.sBudovy)
        val sKoleje: SwitchCompat = findViewById (R.id.sKoleje)
        val sMenzy: SwitchCompat = findViewById (R.id.sMenzy)
        val sMista: SwitchCompat = findViewById (R.id.sMista)
        sBudovy.setOnClickListener(){
            showOrHideMarkers(markerTypes.BUDOVA,sBudovy.isChecked)
        }
        sKoleje.setOnClickListener(){
            showOrHideMarkers(markerTypes.KOLEJ,sKoleje.isChecked)
        }
        sMenzy.setOnClickListener(){
            showOrHideMarkers(markerTypes.MENZA,sMenzy.isChecked)
        }
        sMista.setOnClickListener(){
            showOrHideMarkers(markerTypes.MISTO,sMista.isChecked)
        }
    }

    private fun showMarkers(type: markerTypes){
       for(item in markers){
           if(item.type == type){
               addMarker(item)
           }
       }
    }

    private fun hideMarkers(type: markerTypes){
        var collec : MutableList<Marker> = mutableListOf()
        when(type){
            markerTypes.KOLEJ -> collec = kolejMarkers
            markerTypes.MENZA -> collec = menzaMarkers
            markerTypes.MISTO -> collec = mistoMarkers
            markerTypes.BUDOVA -> collec = budovaMarkers
        }
        for(item in collec){
            item.remove()
        }
    }
    private fun showOrHideMarkers(type: markerTypes,isChecked :Boolean){
        if(isChecked){
            showMarkers(type)
        }
        else{
            hideMarkers(type)
        }
    }

    private fun fillMarkers() {
        markers = mutableListOf(
            //Koleje
            PlaceToMark(50.0196875, 14.4966875,markerTypes.KOLEJ,"Koleje Jižní město"),
            PlaceToMark(50.115928, 14.444372,markerTypes.KOLEJ,"Kolej 17. listopadu"),
            PlaceToMark(50.0880625, 14.3540625,markerTypes.KOLEJ,"Kolej Na Větrníku"),
            PlaceToMark(50.0521803, 14.5386033,markerTypes.KOLEJ,"Kolej Hostivař"),
            PlaceToMark(50.0867269, 14.4353214,markerTypes.KOLEJ,"Kolej Jednota"),
            PlaceToMark(50.0705750, 14.4326660,markerTypes.KOLEJ,"Kolej Budeč"),
            PlaceToMark(50.0804486, 14.4469256,markerTypes.KOLEJ,"Kolej Švehlova"),
            PlaceToMark(50.0851008, 14.3494097,markerTypes.KOLEJ,"Kolej Hvězda"),
            PlaceToMark(50.0873786, 14.3711392,markerTypes.KOLEJ,"Kolej Kajetánka"),
            PlaceToMark(50.0881961, 14.3844325,markerTypes.KOLEJ,"Kolej Komenského"),
            //Menzy
            PlaceToMark(50.0917128, 14.4174903,markerTypes.MENZA,"Menza Právnická"),
            PlaceToMark(50.0804253, 14.4167431,markerTypes.MENZA,"Menza Arnošta z Pardubic"),
            PlaceToMark(50.0691650, 14.4246947,markerTypes.MENZA,"Menza Albertov"),
            PlaceToMark(50.0705750, 14.4325556,markerTypes.MENZA,"Menza Budeč"),
            PlaceToMark(50.0873786, 14.3711392,markerTypes.MENZA, "Menza Kajetánka"),
            PlaceToMark(50.0931275, 14.3350714,markerTypes.MENZA,"Menza Sport"),
            PlaceToMark(50.1166386, 14.4424075,markerTypes.MENZA,"Menza Troja"),

            //Budovy
            PlaceToMark(50.0883450, 14.4032036,markerTypes.BUDOVA,"Malá strana"),
            PlaceToMark(50.1152572, 14.4488403,markerTypes.BUDOVA,"Holešovice"),
            PlaceToMark(50.0695381, 14.4283381,markerTypes.BUDOVA, "Karlov"),
            //Mista
            PlaceToMark(50.0828519, 14.3976569,markerTypes.MISTO,"Petřínské sady")
        )
    }

    private fun addMarker(item: PlaceToMark){
        val place = LatLng(item.longtitude, item.latitude)
        val markerOptions : MarkerOptions = MarkerOptions().position(place).title(item.text)

        when(item.type){
            markerTypes.KOLEJ -> markerOptions.icon(
                BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
            markerTypes.MENZA -> markerOptions.icon(
                BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_RED))
            markerTypes.MISTO -> markerOptions.icon(
                BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            markerTypes.BUDOVA -> markerOptions.icon(
                BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        }

        val marker = mMap.addMarker(markerOptions)
        when(item.type){
            markerTypes.KOLEJ -> marker?.let { kolejMarkers.add(it) }
            markerTypes.MENZA -> marker?.let { menzaMarkers.add(it) }
            markerTypes.MISTO -> marker?.let { mistoMarkers.add(it) }
            markerTypes.BUDOVA -> marker?.let { budovaMarkers.add(it) }
        }
    }
    private fun showMarkers() {
        for(item in markers) {
            addMarker(item)
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        fillMarkers()
        showMarkers()
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(50.0835494, 14.4341414), 11f))
    }
}