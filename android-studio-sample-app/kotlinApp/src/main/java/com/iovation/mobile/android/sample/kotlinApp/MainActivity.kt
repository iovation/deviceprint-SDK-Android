package com.iovation.mobile.android.sample.kotlinApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.iovation.mobile.android.FraudForceManager
import com.iovation.mobile.android.sample.kotlinApp.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val uiScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FraudForceManager.refresh(applicationContext)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener{
            blackboxRequestHandler()
        }
    }

    private fun blackboxRequestHandler() {
        binding.blackboxResult.text = ""
        binding.blackboxResult.visibility = View.GONE

        binding.blackboxResultLine.visibility = View.GONE

        binding.blackboxLabel.text = getString(R.string.printing_wait_msg)
        binding.blackboxLabel.visibility = View.VISIBLE

        uiScope.launch {
            val blackbox : String = getBlackbox()
            withContext(Dispatchers.Main) {
                printBlackbox(blackbox)
            }
        }
    }

    private fun getBlackbox() : String {
        return FraudForceManager.getBlackbox(applicationContext)
    }

    private fun printBlackbox(blackbox : String){
        binding.blackboxLabel.text = getString(R.string.blackbox_label)
        binding.blackboxLabel.visibility = View.VISIBLE

        binding.blackboxResultLine.visibility = View.VISIBLE

        binding.blackboxResult.text = blackbox
        binding.blackboxResult.visibility = View.VISIBLE

        FraudForceManager.refresh(applicationContext)
    }
}