package com.candra.latihan_aplikasi_geofencing

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.candra.latihan_aplikasi_geofencing.databinding.ActivityMainBinding
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@SuppressLint("UnspecifiedImmutableFlag")
class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding

    private lateinit var googleMap: GoogleMap

    private val centerLat = 37.4274745
    private val centerLng = -122.169719
    private val genfenceRadius = 400.0
    // Menambahkan Geofence
    private lateinit var geofencingClient: GeofencingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.apply {
            title = "Candra Julius Sinaga"
            subtitle = "latihan Geofencing"
        }

        val mapsFragment = supportFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment

        mapsFragment.getMapAsync(this)

    }

    /*
    Kedua fungsi tersebut memerlukan pending intent yang berisi Broadcast Receiver yang akan menangkap setiap aksi pada Geofence seperti berikut:
     */
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this,GeofenceBroadcastReceiver::class.java)
        intent.action = GeofenceBroadcastReceiver.ACTION_GEOFENCE_EVENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_IMMUTABLE)
        }else{
            PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        }

        /*
        Terdapat sedikit perbedaan pada pemanggilan fungsi getBroadcast pada Android 12 (S) ke atas dengan bawahnya, yakni pada bagian flag.
         */
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        googleMap.uiSettings.isZoomControlsEnabled = true

        val standFord = LatLng(centerLat,centerLng)

        makeAMarkerBasedLocation(standFord)

        getMyLocation()
        addGeofence()

    }

    @SuppressLint("MissingPermission")
    private fun addGeofence() {
        // Menambahkan Geofence
        geofencingClient = LocationServices.getGeofencingClient(this)

        // Membuat Geofence
        /*
        Berikut adalah attribute yang bisa diatur dalam membuat Geofence:

        setRequestId : Digunakan sebagai id untuk membedakan setiap geofence.
        setCircularRegion : Berisi tiga parameter untuk menentukan area, yakni latitude, longitude, dan radius.
        setExpirationDuration : Menentukan masa kadaluarsa dari geofence yang dibuat dalam milidetik. Jika tidak ada masa kadaluarsa, bisa menggunakan konstanta Geofence.NEVER_EXPIRE.
        setTransitionTypes : Menentukan aksi apa yang ingin dibaca pada Geofence.
        setLoiteringDelay : Menentukan seberapa lama waktu yang digunakan untuk mengetahui sebuah device dikatakan tinggal (dwell) pada suatu area dalam milidetik.
        setNotificationResponsiveness : Menentukan seberapa responsif sistem memberitahu aplikasi jika terdapat suatu aksi dalam milidetik. Default-nya adalah 0. Semakin besar nilai responsif ini, maka semakin hemat pula daya baterai kita.
         */
        val geofence = Geofence.Builder()
            .setRequestId("Kampus")
            .setCircularRegion(
                centerLat,
                centerLng,
                genfenceRadius.toFloat()
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_ENTER)
            .setLoiteringDelay(5000)
            .build()


        //----------------------------------------------------------

        // Kemudian perhatikan kode untuk membuat Geofence Request berikut:
        /*
        setInitialTrigger : Menentukan trigger awal apabila device sudah di dalam area ketika Geofence ditambahkan. Pada latihan ini, kita mengaturnya sebagai ENTER.
        addGeofence : Menambahkan sebuah Geofence.
        addGeofences : Menambahkan list Geofence.
         */
        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        /*
        Pertama, Anda perlu membuat instance GeofenceClient menggunakan LocationService.
        Sebelum menambahkan Geofence, hapus terlebih dahulu Geofence yang sudah ada menggunakan fungsi removeGeofences supaya tidak terjadi duplikasi.
        Apabila sudah terhapus, tambahkan geofence dengan menggunakan fungsi addGeofences. Setelah selesai,
        beritahu melalui toast pada kedua callback yang bisa Anda dapatkan, yakni ketika berhasil dan gagal
         */
        geofencingClient.removeGeofences(geofencePendingIntent).run {
            addOnCompleteListener {

                geofencingClient.addGeofences(geofencingRequest,geofencePendingIntent).run {
                    addOnSuccessListener {
                        showToast("Geofencing added")
                    }
                    addOnFailureListener {
                        showToast("Geofencing not added: ${it.message}")
                    }
                }
            }
        }
    }

    private fun showToast(s: String) {
        Toast.makeText(this@MainActivity,s,Toast.LENGTH_SHORT).show()
    }

    private val runningQOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    private val requestBackgroundLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){ isGranted ->
            if (isGranted){
                getMyLocation()
            }

        }

    @TargetApi(Build.VERSION_CODES.Q)
    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->
        if (isGranted){
            if (runningQOrLater){
                requestBackgroundLocationPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }else{
                getMyLocation()
            }
        }
    }

    private fun getMyLocation() {
        if (checkForegroundAndBackgroundLocationPermission()){
            googleMap.isMyLocationEnabled = true
        }else{
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun checkPermission(permission: String): Boolean{
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private fun checkForegroundAndBackgroundLocationPermission(): Boolean{
        val foregroundLocationApproved = checkPermission(
            Manifest.permission.ACCESS_FINE_LOCATION)
        val backgroundPermissionApproved =
            if (runningQOrLater){
                checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }else{
                true
            }

        return foregroundLocationApproved && backgroundPermissionApproved
    }


    private fun makeAMarkerBasedLocation(latlang:LatLng){
       googleMap.apply {
           addMarker(
               MarkerOptions().position(latlang)
                   .title("Standford University")
                   .draggable(true)
           )
           moveCamera(CameraUpdateFactory.newLatLngZoom(latlang,10f))

           addCircle(
               CircleOptions()
                   .center(latlang)
                   .radius(genfenceRadius)
                   .fillColor(0x22FF0000)
                   .strokeColor(Color.RED)
                   .strokeWidth(3f)
           )
       }
    }

    // Kesimpulan
    /*
    // Background Location Permission
    Untuk menjalankan lokasi di background pada Android 10 (Q) ke atas, Anda harus meminta izin khusus, yakni ACCESS_BACKGROUND_LOCATION. Hal yang perlu diperhatikan adalah adalah request izin ini harus dilakukan setelah permission ACCESS_FINE_LOCATION terpenuhi


     */
}