package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.DailyRecordRepository
import com.example.ui.DailyCheckScreen
import com.example.ui.DailyCheckViewModel
import com.example.ui.theme.IvoryBackground
import com.example.ui.theme.SeniorHealthTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )

        val database = AppDatabase.getInstance(applicationContext)
        val repository = DailyRecordRepository(database.dailyRecordDao())

        setContent {
            SeniorHealthTheme {
                val viewModel: DailyCheckViewModel = viewModel(
                    factory = DailyCheckViewModel.Factory(repository)
                )
                val lifecycleOwner = LocalLifecycleOwner.current
                val context = LocalContext.current

                DisposableEffect(lifecycleOwner, context, viewModel) {
                    val lifecycleObserver = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            viewModel.refreshDateIfNeeded()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

                    val dateChangeReceiver = object : BroadcastReceiver() {
                        override fun onReceive(ctx: Context?, intent: Intent?) {
                            viewModel.refreshDateIfNeeded()
                        }
                    }
                    val dateFilter = IntentFilter().apply {
                        addAction(Intent.ACTION_DATE_CHANGED)
                        addAction(Intent.ACTION_TIME_CHANGED)
                        addAction(Intent.ACTION_TIMEZONE_CHANGED)
                    }
                    ContextCompat.registerReceiver(
                        context,
                        dateChangeReceiver,
                        dateFilter,
                        ContextCompat.RECEIVER_NOT_EXPORTED,
                    )

                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
                        context.unregisterReceiver(dateChangeReceiver)
                    }
                }

                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = IvoryBackground,
                    contentWindowInsets = WindowInsets.safeDrawing,
                ) { innerPadding ->
                    DailyCheckScreen(
                        uiState = uiState,
                        onToggleMorning = viewModel::toggleMorning,
                        onToggleLunch = viewModel::toggleLunch,
                        onToggleDinner = viewModel::toggleDinner,
                        onAddWaterCup = viewModel::addWaterCup,
                        onRemoveWaterCup = viewModel::removeWaterCup,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
