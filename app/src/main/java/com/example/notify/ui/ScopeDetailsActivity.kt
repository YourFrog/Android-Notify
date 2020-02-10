package com.example.notify.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.notify.R
import android.graphics.Bitmap
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.databinding.DataBindingUtil
import com.example.notify.databinding.ActivityScopeDetailsBinding
import com.example.notify.databinding.ActivityScopeListBinding
import com.google.zxing.MultiFormatWriter


class ScopeDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScopeDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scope_details)

    }
}
