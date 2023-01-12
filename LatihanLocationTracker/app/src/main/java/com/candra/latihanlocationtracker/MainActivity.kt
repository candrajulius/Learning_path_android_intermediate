package com.candra.latihanlocationtracker

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.candra.latihanlocationtracker.databinding.ActivityMainBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(),OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMainBinding

    // Digunakan untuk membuat instance dari FusedLocationProviderClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var isTracking = false

    // Inisiasi semua list latLng
    private var allLatlng = ArrayList<LatLng>()

    // Untuk menginisiasi LatLngBounds
    private var boundsBuilder =  LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.maps) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Membuat intance dari FusedLocationProvider
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        getMyLastLocation()
        createLocationRequest()
        createLocationCallback()

        binding.btnStart.setOnClickListener {
            if (!isTracking) {
                clearMaps()
                updateTrackingStatus(true)
                startLocationUpdates()
            } else {
                updateTrackingStatus(false)
                stopLocationUpdates()
            }
        }
    }

    private fun startLocationUpdates() {
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (exception: SecurityException) {
            Log.e(TAG, "Error : " + exception.message)
        }
    }


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }
                else -> {
                    // No location access granted.
                }
            }
        }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun createLocationCallback() {

        /*
        Fungsi onLocationResult merupakan callback yang dipanggil ketika aplikasi bisa mendapatkan data lokasi.
        Perlu diketahui bahwa variable locations dari LocationResult ini berupa List yang berisi Location.
        Untuk itu, lakukanlah perulangan untuk mendapatkan setiap data lokasi sejak awal.
        Jika Anda hanya ingin mendapatkan data lokasi terakhir,
        panggillah locationResult.lastLocation.
         */
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    Log.d(TAG, "onLocationResult: " + location.latitude + ", " + location.longitude)

                    // PolyLine
                    /*
                    PolylineOptions merupakan object yang digunakan untuk mengatur konfigurasi dari Polyline yang akan dibuat.

                    Berikut adalah beberapa attribute yang bisa Anda atur:
                    color : Mengatur warna garis.
                    width : Mengatur lebar garis.
                    add : Menambahkan sebuah titik LatLng.
                    addAll : Menambahkan langsung banyak List LatLng.
                    Parameter untuk fungsi tersebut berupa LatLng. Oleh karena itu, kita membuat variabel lastLatLng untuk mengubah tipe Location menjadi LatLng.

                     */
                    val lastlatLng = LatLng(location.latitude,location.longitude)

                    // draw polyline
                    allLatlng.add(lastlatLng)

                    mMap.addPolyline(
                        PolylineOptions().color(Color.CYAN)
                            .width(10f)
                            .addAll(allLatlng)
                    )

                    // set boundaries

                    /*
                    Untuk mengumpulkan setiap titik yang ingin terlihat, gunakan fungsi include pada LatLngBounds.Builder.
                    Setelah terkumpul, ia akan menjadi parameter pertama pada fungsi newLatLngBounds, sedangkan parameter kedua berupa integer adalah padding.
                    Padding merupakan jarak antara semua titik dengan pinggir peta sehingga tampilan tidak terlalu mepet.
                     */

                    boundsBuilder.include(lastlatLng)
                    val bounds: LatLngBounds = boundsBuilder.build()
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,64))
                    //-------------------------------------------
                }
            }
        }
    }

    private fun updateTrackingStatus(newStatus: Boolean) {
        isTracking = newStatus
        if (isTracking) {
            binding.btnStart.text = getString(R.string.stop_running)
        } else {
            binding.btnStart.text = getString(R.string.start_running)
        }
    }

    private fun createLocationRequest() {
        // Untuk mengatur konfigurasi saat request lokasi, Menggunakan Object LocationRequest
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = TimeUnit.SECONDS.toMillis(1)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        /*
        Berikut ini adalah beberapa hal yang dapat diatur dalam LocationRequest:

        interval : Mengetahui berapa interval untuk mengambil data lokasi kembali dalam milidetik.
        maxWaitTime : Mengatur waktu maksimal untuk update lokasi dalam milidetik.
        priority : Mengatur prioritas untuk menentukan sumber data. Semakin tinggi akan semakin akurat dan menghabiskan baterai.

 LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = TimeUnit.SECONDS.toMillis(1)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY

         */


        //-----------------------------------------------------------------------------------

        // Kemudian untuk memeriksa apakah kondisi tersebut sudah terpenuhi atau tidak, kita memanggil fungsi checkLocationSettings dari SettingClient
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getMyLastLocation()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Toast.makeText(this@MainActivity, sendEx.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

        /*
        Ketika berhasil, kita bisa mulai untuk mengambil data lokasi. Namun jika gagal, kita bisa menyerahkannya ke sistem untuk menyelesaikan masalah tersebut jika termasuk pada ResolvableApiException dengan memanfaatkan IntentSenderRequest.
         */


        //------------------------------------------------------------------------------------------


    }

    private fun getMyLastLocation() {
        if     (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            // Untuk mengetahui lokasi terakhir
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    showStartMarker(location)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            //---------------------------------------

        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun showStartMarker(location: Location) {
        val startLocation = LatLng(location.latitude, location.longitude)
        mMap.addMarker(
            MarkerOptions()
                .position(startLocation)
                .title(getString(R.string.start_point))
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 17f))
    }


    // Callback terjadi apabila tidak diaktifkan GPSnya
    private val resolutionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            when (result.resultCode) {
                RESULT_OK ->
                    Log.i(TAG, "onActivityResult: All location settings are satisfied.")
                RESULT_CANCELED ->
                    Toast.makeText(
                        this@MainActivity,
                        "Anda harus mengaktifkan GPS untuk menggunakan aplikasi ini!",
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }


    override fun onResume() {
        super.onResume()
        if (isTracking) {
            startLocationUpdates()
        }
    }

    private fun clearMaps(){
        mMap.clear()
        allLatlng.clear()
        boundsBuilder = LatLngBounds.Builder()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    companion object {
        private const val TAG = "MapsActivity"
    }

}

// Kesimpulan
/*
    Perlu diketahui bahwa Location ini bisa jadi bernilai null apabila:

    GPS pada device dimatikan, hal ini karena ketika GPS dimatikan, cache berupa data lokasi juga dihapus.
    Device tidak pernah menyimpan data lokasi, hal ini bisa terjadi pada device baru atau device yang baru saja di-reset.
    Google Play Service pada device tersebut ter-restart. Untuk mengatasinya, kita bisa menggunakan location update untuk meminta data lokasi sendiri.

 */