package com.example.notify.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.notify.R
import com.example.notify.databinding.ActivityScopeShareBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

class ScopeShareActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScopeShareBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scope_share)

        intent.getStringExtra("code").let { code ->
            val bitMatrix = MultiFormatWriter().encode(code, BarcodeFormat.QR_CODE, 200, 200)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            binding.image.setImageBitmap(bitmap)
        }
    }
}
