package com.candra.latihangooglemaps

import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.jar.Manifest
import kotlin.math.log

class MainActivity : AppCompatActivity(),OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.apply {
            title = "Candra Julius Sinaga"
            subtitle = getString(R.string.app_name)
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.maps1) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap1: GoogleMap) {
        googleMap = googleMap1

        googleMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true

            showMarker()

//            googleMap.setOnMapClickListener {
//                setOnClikcMap(it)
//            }

            googleMap.setOnPoiClickListener {
                setOnPoiClickListener(it)
            }

            getMyLocation()
        }

        setMapStyle()
    }

    private fun setMapStyle() {
        try {
            val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.map_style))
            if (!success){
                Log.e("TAG", "setMapStyle: Failed", )
            }
        }catch (exception: Resources.NotFoundException){
            Log.e("TAG", "setMapStyle: ",exception )
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED){
            googleMap.isMyLocationEnabled = true
        }else{
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){
        if (it){
            getMyLocation()
        }
    }
    // Menambahkan Aksi Klik pada Maps
    private fun setOnClikcMap(latLng: LatLng) {
        googleMap.addMarker(
            MarkerOptions().position(latLng).title("New Marker").snippet("Lat: ${latLng.latitude} Long: ${latLng.longitude}")
                .icon(vectorToBitmap(R.drawable.ic_android_black_24dp, Color.parseColor("#3DDC84")))
        )
    }

    private fun setOnPoiClickListener(it: PointOfInterest) {
        val poiMarker = googleMap.addMarker(
            MarkerOptions().position(it.latLng).title(it.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                .draggable(true)
        )
        poiMarker?.showInfoWindow()
    }

    /*
    Fungsi ini digunakan untuk mengconversi vektor ke bitmap, Memberikan warna tint pada suatu vektor
     */
    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor{
        val vectorDrawable = ResourcesCompat.getDrawable(resources,id,null)

        if (vectorDrawable == null){
            Log.e("data", "vectorToBitmap: ", )
            return BitmapDescriptorFactory.defaultMarker()
        }

        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0,0,canvas.width,canvas.height)
        DrawableCompat.setTint(vectorDrawable,color)
        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    private fun showMarker(){
        val dicodingSpace = LatLng(-6.8957643, 107.6338462)
        googleMap.addMarker(
            MarkerOptions().position(dicodingSpace).title("Dicoding Space").snippet("Batik Kumeli No.50")
        )

        // Untuk mengarahkan kamera
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dicodingSpace,15f))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId){

            R.id.normal_type -> {
                googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }

            R.id.satellite_type -> {
                googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }

            R.id.terrain_type -> {
                googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }

            R.id.hybrid_type -> {
                googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }

            else -> super.onOptionsItemSelected(item)

        }

    }

    /*
    Kesimpulan

        // Pengaturan UI pada Maps
        Terdapat banyak pengaturan yang bisa Anda implementasikan pada Google Maps. Beberapa di antaranya adalah:

        zoomControlsEnabled : Mengaktifkan zoom control pada pojok kanan bawah.
        indoorLevelPickerEnabled : Mengaktifkan fitur untuk menampilkan detail lantai.
        compassEnabled : Mengaktifkan kompas pada pojok kiri atas.
        mapToolbarEnabled : Mengaktifkan toolbar untuk navigasi dan aplikasi Google Maps.
        scrollGesturesEnabled, setTiltGesturesEnabled, setZoomGesturesEnabled, setRotateGesturesEnabled, dan setAllGesturesEnabled : Mengaktifkan beberapa atau semua gerakan gestur tangan seperti scroll untuk memindahkan tampilan peta, tilt untuk memiringkan peta (efek 3D), pinch untuk zoom, dan rotate untuk memutar arah peta.

        // Mengubah Tipe dan Jenis Maps
        Kemudian untuk mengubah style maps, kode yang digunakan adalah:
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
        Fungsi loadRawResourceStyle digunakan untuk mengambil resource dari folder raw. Kemudian, hasil dari fungsi setMapStyle ini mengembalikan boolean, apabila ia bernilai true maka proses pengubahan style berhasil.

        // Menampilkan dan Melakukan Kustomisasi Marker
        Untuk menambahkan marker, kita menggunakan fungsi addMarker dengan parameter berupa MarkerOptions seperti berikut:
        Property yang wajib ada pada MarkerOptions adalah position. Position ini berupa LatLng yang merupakan kelas khusus untuk menampung data Latitude dan Longitude. Selain position, property lain yang bisa Anda atur adalah:
        Title : Teks yang muncul pada Info Window ketika marker dipilih.
        Snippet : Teks tambahan yang muncul di bawah title.
        Icon : Bitmap yang digunakan untuk menggantikan default marker.
        Anchor : Bagian gambar yang diletakkan pada posisi koordinat. Secara default, ada di bawah tengah gambar.
        Alpha : Menentukan seberapa transparan marker yang ditampilkan. Dapat diisi dengan angka 0-1.0.
        Draggable : Menentukan apakah marker dapat di-drag (digeser) atau tidak.
        Visible : Menentukan apakah marker dapat dilihat atau tidak.

        Pada bagian property icon, kita memerlukan object BitmapDescriptor untuk menentukan bagaimana suatu ikon dibuat. Berikut adalah beberapa cara yang dapat dilakukan:
        defaultMarker : Menggunakan bentuk penanda default yang bisa Anda ubah warnanya. Contohnya adalah
        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)
        fromResource : Menggunakan bitmap dari folder resource.
        fromAsset : Menggunakan bitmap dari folder asset.
        fromPath : Menggunakan bitmap dari path spesifik.
        fromBitmap : Menggunakan langsung dari gambar bitmap.


        // Mengatur Camera
        Fungsi ini berguna untuk mengarahkan kamera ke suatu posisi. Ada dua fungsi yang bisa Anda gunakan untuk mengatur camera, yaitu:
        moveCamera : Memindahkan kamera ke suatu posisi tanpa animasi.
        animateCamera : Memindahkan kamera ke suatu posisi dengan animasi.
        Kedua fungsi tersebut membutuhkan parameter berupa CameraUpdate yang dapat mengatur tampilan maps ketika kamera diubah. Beberapa fungsi yang ada antara lain:
        newLatLng : Menampilkan bagian tengah maps ke suatu titik koordinat.
        newLatLngZoom : Memperbesar maps ke suatu titik koordinat dengan zoom level yang bisa ditentukan.
        newLatLngBounds : Memperbesar maps agar semua titik koordinat yang ditentukan terlihat. Cocok digunakan untuk menampilkan banyak posisi yang ingin ditampilkan pada suatu area, misal lokasi rumah sakit, pom bensin, dsb.

        // Menambahkan Aksi Klik pada Maps
        Dengan listener ini, Anda akan memperoleh object LatLng yang bisa digunakan untuk aksi selanjutnya, seperti menampilkan marker, mengirim data ke server, dsb.
        Sebenarnya ada beberapa listener yang bisa Anda pilih untuk menangani ketika suatu maps atau item dipilih. Beberapa contoh yang biasa dipakai adalah:
        setOnMapClickListener : Menangani aksi ketika map diklik.
        setOnMapLongClickListener : Menangani aksi ketika map diklik lama.
        setOnPoiClickListener : Menangani aksi ketika titik POI dipilih.
        setOnMarkerClickListener : Menangani aksi ketika marker dipilih.
        setOnInfoWindowClickListener : Menangani aksi ketika info window dipilih.

        // Menampilkan My Location
        Untuk menampilkan lokasi, Anda perlu cek terlebih dahulu apakah permission untuk lokasi sudah diizinkan atau belum. Anda dapat melakukan pengecekan menggunakan kode checkSelfPermission seperti berikut:
        Apabila permission sudah diizinkan dan mengembalikan nilai True, Anda dapat mengaktifkan tombol My Location menggunakan kode berikut:
        Namun, jika belum diizinkan, Anda perlu request permission terlebih dahulu menggunakan kode requestPermissions berikut:
        Ketika aksi dari pengguna sudah dilakukan, aplikasi akan memanggil callback pada registerForActivityResult seperti ini:
     */
}