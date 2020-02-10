package com.example.notify.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.notify.R
import com.example.notify.databinding.ActivityScopeListBinding
import com.example.notify.repository.ScopeRepository
import com.example.notify.storage.AccessStorage
import io.reactivex.schedulers.Schedulers
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.multidex.MultiDex
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notify.ui.adapter.ScopeListAdapter
import com.google.zxing.integration.android.IntentIntegrator
import android.widget.Toast


class ScopeListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScopeListBinding

    private val scopeRepository by lazy {
        ScopeRepository(
            AccessStorage(this)
        )
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scope_list)

        refreshList()

        binding.scopeAdd.setOnClickListener(this::addScope)
        binding.scopeAddFromEmpty.setOnClickListener(this::addScope)

        binding.scopeScan.setOnClickListener(this::scan)
    }

    fun scan(view: View) {
        val integrator = IntentIntegrator(this)
        integrator.setOrientationLocked(true)
        integrator.setBeepEnabled(true)
        integrator.setTimeout(8000)

        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this,    "Cancelled",Toast.LENGTH_LONG).show();
            } else {
                val code = result.contents.replace(" ", "")

                scopeRepository
                    .join(code)
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        if( it ) {
                            refreshList()
                        }
                    }, {
                        val z = 0
                    })
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    fun addScope(view: View) {
        val taskEditText = EditText(this)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Dodawnie nowej przestrzeni")
            .setMessage("Proszę podaj nazwę")
            .setView(taskEditText)
            .setPositiveButton("Dodaj", DialogInterface.OnClickListener { dialog, which ->
                val name = taskEditText.text.toString()

                scopeRepository
                    .add(name)
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        if( it ) {
                            refreshList()
                        }
                        val z = 0
                    }, {
                        val j = 0
                    })
            })
            .setNegativeButton("Anuluj", null)
            .create()
        dialog.show()
    }

    fun refreshList() {
        scopeRepository
            .getList()
            .subscribeOn(Schedulers.io())
            .subscribe({
                runOnUiThread {
                    binding.emptyScopes.visibility = View.GONE
                }

                it?.content?.let {
                    runOnUiThread {
                        binding.emptyScopes.visibility = if( it.items.isEmpty() ) { View.VISIBLE } else { View.GONE }
                        binding.scopeList.visibility = if( !it.items.isEmpty() ) { View.VISIBLE } else { View.GONE }

                        val adapter = ScopeListAdapter(this, it.items)

                        adapter.setClickListener(object : ScopeListAdapter.ItemClickListener {
                            override fun onItemClick(view: View, position: Int) {
                                val scope = adapter.getItem(position)

                                Intent(this@ScopeListActivity, ScopeDetailsActivity::class.java).apply {
                                    this.putExtra("id", scope.id_scope)

                                    this@ScopeListActivity.startActivity(this)
                                }
                            }

                            override fun onIconClick(view: View, position: Int) {
                                val scope = adapter.getItem(position)

                                scopeRepository
                                    .share(scope.id_scope)
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({ code ->
                                        Intent(this@ScopeListActivity, ScopeShareActivity::class.java).apply {
                                            this.putExtra("code", code)

                                            this@ScopeListActivity.startActivity(this)
                                        }
                                    }, {
                                        val z = 0
                                    })
                            }
                        })

                        binding.scopeRecycler.setLayoutManager(LinearLayoutManager(this))
                        binding.scopeRecycler.setAdapter(adapter)
                    }

                }
            }, {
                val i = 0
            })
    }
}
