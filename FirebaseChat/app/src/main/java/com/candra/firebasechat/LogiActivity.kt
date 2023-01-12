package com.candra.firebasechat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.candra.firebasechat.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LogiActivity: AppCompatActivity()
{

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configure Google Sign Im
        /*
        Pada bagian Builder, Anda dapat mengatur jenis metode login yang digunakan. DEFAULT_SIGN_IN dipilih jika login hanya untuk keperluan aplikasi biasa. Selain itu, terdapat pilihan DEFAULT_GAMES_SIGN_IN jika untuk aplikasi permainan.
default_web_client_id merupakan resource string yang otomatis dibuat ketika terdapat google-service.json. Berkas ini berisi client_id dari oauth_client untuk berkomunikasi dengan server Google.
requestEmail digunakan untuk menentukan data spesifik yang ingin diambil, yakni email, selain itu juga terdapat requestProfile dan requestId.
         */
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this@LogiActivity,gso)

        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.btnLoginGoogle.setOnClickListener {
            sigIn()
        }

        supportActionBar?.apply {
            title = "Candra Julius Sinaga"
            subtitle = getString(R.string.app_name)
        }

    }

    private fun sigIn() {
        val sigInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(sigInIntent)
    }


    /*
    Pada kode di atas kita menggunakan ActivityResultLauncher dengan parameter signInIntent untuk menampilkan pop up berisi pilihan akun Google. Apabila sudah dipilih, Anda bisa mendapatkan idToken dari GoogleSignInAccount.
    */
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
        if (it.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken)
            }catch (e: ApiException){

            }
        }
    }

    /*
    Dengan memanfaatkan idToken yang didapatkan sebelumnya, Anda bisa mendapatkan AuthCredential. Credential ini digunakan oleh FirebaseAuth untuk login menggunakan akun Google menggunakan method signInWithCredential. Selanjutnya, addOnCompleteListener akan dipanggil dan melakukan pengecekan apakah berhasil atau tidak. Ketika berhasil, Anda akan mendapatkan akun user dalam bentuk FirebaseUser.
     */
    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken,null)
        auth.signInWithCredential(credential).addOnCompleteListener(this){
            if (it.isSuccessful){
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithCredential:success")
                val user = auth.currentUser
                updateUI(user)
            }else{
                updateUI(null)
            }
        }
    }

    /*
    Apabila FirebaseUser tidak bernilai null alias terdapat akun Google yang login, langkah selanjutnya yaitu pindah ke halaman MainActivity. Kode ini juga dipanggil pada method onStart supaya ketika sudah pernah login, aplikasi tidak perlu menampilkan halaman login, tetapi langsung ke halaman utama.
    Dengan cara yang sama, pengecekan seperti ini juga dilakukan pada MainActivity untuk memastikan status autentikasi user. Apabila data autentikasi hilang (misal karena clear cache), aplikasi akan kembali ke halaman login.
     */
    private fun updateUI(user: FirebaseUser?) {
        if (user != null){
            startActivity(Intent(this@LogiActivity,MainActivity::class.java))
            finish()
        }
    }


    companion object{
        const val TAG = "Login"
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(user = currentUser)
    }
}