package com.example.netspeedcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.netspeedcalc.ui.NetCalcScreen
import com.example.netspeedcalc.ui.theme.NetSpeedCalcTheme
import com.example.netspeedcalc.viewmodel.NetCalcViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NetSpeedCalcTheme {
                val viewModel: NetCalcViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NetCalcScreen(
                        uiState = uiState,
                        onFileSizeChanged = viewModel::onFileSizeChanged,
                        onSizeUnitSelected = viewModel::onSizeUnitSelected,
                        onSpeedChanged = viewModel::onSpeedChanged,
                        onSpeedUnitSelected = viewModel::onSpeedUnitSelected,
                        onOverheadChanged = viewModel::onOverheadChanged,
                        onProtocolSelected = viewModel::onProtocolSelected,
                        onStrictOverheadToggled = viewModel::onStrictOverheadToggled,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
