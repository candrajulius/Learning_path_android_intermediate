package com.candra.firebasechat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.candra.firebasechat.adapter.FirebaseMessageAdapter
import com.candra.firebasechat.databinding.ActivityMainBinding
import com.candra.firebasechat.model.Message
import com.candra.firebasechat.service.MyFirebaseMessagingService
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private var firebaseUser: FirebaseUser? = null
    private lateinit var db: FirebaseDatabase
    private lateinit var mainAdapter: FirebaseMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = Firebase.auth


        firebaseUser = auth.currentUser

        supportActionBar?.let {
            it.title = "Candra Julius Sinaga"
            it.subtitle = getString(R.string.app_name)
        }


        if (firebaseUser == null){
            startActivity(Intent(this@MainActivity,LogiActivity::class.java))
            finish()
            return
        }

        // Inisiasi database
        db = Firebase.database

        // Menentukan lokasi dengan data dengan menggunakan fungsi child pada DatabaseReference
        val messageRef = db.reference.child(MESSAGE_CHILD)

        binding.sendButton.setOnClickListener {
            val friendlyMessage = Message(
                binding.messageEditText.text.toString(),
                firebaseUser?.displayName.toString(),
                firebaseUser?.photoUrl.toString(),
                Date().time
            )

            // untuk menambah atau mengganti data, kita menggunakan fungsi setValue.
            /*
            Pada dasarnya, Anda dapat mengirimkan data dengan tipe data seperti berikut.
            String
            Long
            Double
            Boolean
            Map<String, Object>
            List<Object>
             */

            /*
            Kemudian, Anda dapat membaca hasil dari suatu proses dengan mengimplementasikan interface CompletionListener melalui lambda. Sehingga Anda dapat mengetahui apakah prosesnya terdapat error atau tidak.
             */
            /*
            Fungsi ini digunakan untuk membuat auto-generated key di dalam child messages. Sehingga, ketika kita memanggil fungsi setValue lagi, value yang sebelumnya tidak tergantikan, tetapi akan dibuat pada key baru yang berbeda. Jika kita perhatikan pada Firebase Console, hasilnya akan terlihat seperti ini.
             */
            messageRef.push().setValue(friendlyMessage){error,_ ->
                if (error != null){
                    Toast.makeText(this@MainActivity,getString(R.string.send_error) + error.message,Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@MainActivity,getString(R.string.send_success),Toast.LENGTH_SHORT).show()
                }
            }
            binding.messageEditText.setText("")
        }

        val manager = LinearLayoutManager(this)
        manager.stackFromEnd = true
        binding.messageRecyclerView.layoutManager = manager

        /*
        Pada fungsi setQuery kita menentukan data apa yang diambil dengan menggunakan DatabaseReference seperti yang kita buat sebelumnya, kemudian pada parameter kedua diisi dengan kelas model untuk memparsing data. Dengan menggunakan cara ini RecyclerView akan otomatis membaca setiap perubahan data pada Firebase Realtime Database.
         */
        val option = FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(messageRef,Message::class.java)
            .build()


        mainAdapter = FirebaseMessageAdapter(option,firebaseUser?.displayName)
        binding.messageRecyclerView.adapter = mainAdapter

//        currentToken()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        mainAdapter.startListening()
    }

    override fun onPause() {
        mainAdapter.stopListening()
        super.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.sign_out_menu -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /*
    Untuk keluar dan menghapus data autentikasi cukup panggil method signOut dan arahkan aplikasi ke halaman login.
     */
    private fun signOut() {
        auth.signOut()
        startActivity(Intent(this,LogiActivity::class.java))
        finish()
    }

    companion object{
        const val MESSAGE_CHILD = "messages"
        const val TAG = "Data"
    }

    fun currentToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful){
                Log.d(TAG, "currentToken: failed ${task.exception}")
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result

            Log.d(TAG, "currentToken: $token")
        })
    }
}