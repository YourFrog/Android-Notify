package com.example.notify.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.notify.R
import com.example.notify.databinding.ActivityMainBinding
import com.example.notify.repository.SecurityRepository
import com.example.notify.service.AlarmService
import com.example.notify.storage.AccessStorage
import com.example.notify.structure.response.BaseResponse
import com.example.notify.structure.response.Register
import com.example.notify.structure.response.SignIn
import io.reactivex.schedulers.Schedulers

infix fun Any?.ifNull(block: () -> Unit) {
    if (this == null) block()
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val accessStorage = AccessStorage(this)

    @SuppressLint("ShortAlarm")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val ishintent = Intent(this, AlarmService::class.java)
        val pintent = PendingIntent.getService(this, 0, ishintent, 0)
        val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.cancel(pintent)
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 5000, pintent)

//        findViewById<Button>(R.id.button).setOnClickListener {
//            AlarmService().onStartCommand(null, 0, 0)
//        }

//
//        binding.overlay.visibility = View.VISIBLE
//
//        val login = when {
//            accessStorage.getLogin() == null -> {
//                val deviceUID = Settings.Secure.getString(this@MainActivity.getContentResolver(), Settings.Secure.ANDROID_ID) ?: ""
//                accessStorage.setLogin(deviceUID)
//
//                deviceUID
//            }
//            else -> accessStorage.getLogin() ?: ""
//        }
//
//        accessStorage.getPassword()?.let { password ->
//            signIn(
//                login,
//                password
//            )
//            return@onCreate
//        }
//
//        SecurityRepository()
//            .register(login)
//            .subscribeOn(Schedulers.io())
//            .subscribe({
//                var toastMessage: String? = null
//
//                when {
//                    it?.error is BaseResponse.Error -> {
//                        toastMessage = when(it.error?.code) {
//                            1000 -> "Błędne wywołanie"
//                            1001 -> "Konto zarejestrowane"
//                            else -> "Nieznany błąd"
//                        }
//                    }
//                    it?.content is Register.Success -> {
//                        it?.content?.let {
//                            val pass = String(Base64.decode(it.hash, Base64.DEFAULT))
//
//                            accessStorage.setPassword(pass)
//
//                            signIn(login, pass)
//                            return@subscribe
//                        }
//
//                        toastMessage = "Utworzono konto"
//                    }
//                }
//
//                runOnUiThread {
//                    binding.overlay.visibility = View.GONE
//                    toastMessage?.let { message ->
//                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
//                    }
//                }
//            }, {
//                val i = 0
//            })
    }

    fun signIn(login: String, pass: String) {
        SecurityRepository()
            .signIn(login, pass)
            .subscribeOn(Schedulers.io())
            .subscribe({
                var toastMessage: String? = null

                when {
                    it?.content is SignIn.Success -> {
                        val session = String(Base64.decode(it.content.hash, Base64.DEFAULT))
                        accessStorage.setSession(session)

                        Intent(this, ScopeListActivity::class.java).apply {
//                            this.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                            this@MainActivity.startActivity(this)
//                            this@MainActivity.finish()
                        }
                    }
                }
                runOnUiThread {
                    binding.overlay.visibility = View.GONE
                }
            }, {
                val i = 0
            })
    }
}
