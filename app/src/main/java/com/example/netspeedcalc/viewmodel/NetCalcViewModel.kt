package com.example.netspeedcalc.viewmodel

import androidx.lifecycle.ViewModel
import com.example.netspeedcalc.model.NetCalcUiState
import com.example.netspeedcalc.model.Protocol
import com.example.netspeedcalc.model.SizeUnit
import com.example.netspeedcalc.model.SpeedUnit
import com.example.netspeedcalc.model.StatusColorType
import java.util.Locale
import kotlin.math.ceil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetCalcViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NetCalcUiState())
    val uiState: StateFlow<NetCalcUiState> = _uiState.asStateFlow()

    init {
        recalculate()
    }

    fun onFileSizeChanged(value: String) {
        updateState { it.copy(fileSizeText = sanitizeNumericInput(value)) }
    }

    fun onSizeUnitSelected(unit: SizeUnit) {
        updateState { it.copy(selectedSizeUnit = unit) }
    }

    fun onSpeedChanged(value: String) {
        updateState { it.copy(speedValueText = sanitizeNumericInput(value)) }
    }

    fun onSpeedUnitSelected(unit: SpeedUnit) {
        updateState { it.copy(selectedSpeedUnit = unit) }
    }

    fun onOverheadChanged(percentage: Float) {
        updateState { it.copy(overheadPercentage = percentage.coerceIn(0f, 100f)) }
    }

    fun onProtocolSelected(protocol: Protocol) {
        updateState { it.copy(selectedProtocol = protocol) }
    }

    fun onStrictOverheadToggled(enabled: Boolean) {
        updateState { it.copy(isStrictOverheadEnabled = enabled) }
    }

    private fun updateState(transform: (NetCalcUiState) -> NetCalcUiState) {
        _uiState.value = transform(_uiState.value)
        recalculate()
    }

    private fun recalculate() {
        val current = _uiState.value
        val fileSize = current.fileSizeText.toDoubleOrNull() ?: 0.0
        val speedValue = current.speedValueText.toDoubleOrNull() ?: 0.0

        val totalBytes = fileSize * bytesPerSizeUnit(current.selectedSizeUnit)
        val baseBytesPerSecond = speedValue * bytesPerSecondPerSpeedUnit(current.selectedSpeedUnit)

        val effectiveOverhead = (
            current.overheadPercentage + if (current.isStrictOverheadEnabled) STRICT_OVERHEAD_BONUS else 0f
        ).coerceIn(0f, 100f)
        val usableFraction = (1.0 - effectiveOverhead / 100.0).coerceIn(0.0, 1.0)
        val effectiveBytesPerSecond = baseBytesPerSecond * usableFraction

        val seconds = if (totalBytes > 0.0 && effectiveBytesPerSecond > 0.0) {
            totalBytes / effectiveBytesPerSecond
        } else {
            null
        }

        val (diagnosis, status) = diagnose(seconds, totalBytes, effectiveBytesPerSecond)

        _uiState.value = current.copy(
            formattedTimeString = seconds?.let(::formatDuration) ?: EMPTY_TIME,
            performanceDiagnosis = diagnosis,
            statusColorType = status,
            usb64gbEquivalents = formatEquivalence(totalBytes / USB_64_GB_BYTES),
            dvdEquivalents = formatEquivalence(totalBytes / DVD_4_7_GB_BYTES)
        )
    }

    private fun diagnose(
        seconds: Double?,
        totalBytes: Double,
        effectiveBytesPerSecond: Double
    ): Pair<String, StatusColorType> {
        return when {
            totalBytes <= 0.0 -> "Ingresa un tamaño válido" to StatusColorType.YELLOW
            effectiveBytesPerSecond <= 0.0 -> "Sin velocidad útil" to StatusColorType.RED
            seconds == null -> "Sin datos válidos" to StatusColorType.YELLOW
            seconds <= 60.0 -> "Excelente" to StatusColorType.GREEN
            seconds <= 600.0 -> "Bueno" to StatusColorType.GREEN
            seconds <= 3600.0 -> "Aceptable" to StatusColorType.YELLOW
            else -> "Crítico" to StatusColorType.RED
        }
    }

    private fun formatDuration(seconds: Double): String {
        val totalSeconds = ceil(seconds).toLong().coerceAtLeast(0L)
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val remainingSeconds = totalSeconds % 60
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, remainingSeconds)
    }

    private fun formatEquivalence(value: Double): String {
        return String.format(Locale.US, "%.2f", value)
    }

    private fun bytesPerSizeUnit(unit: SizeUnit): Double {
        return when (unit) {
            SizeUnit.MB -> BYTES_PER_MB
            SizeUnit.GB -> BYTES_PER_GB
            SizeUnit.TB -> BYTES_PER_TB
        }
    }

    private fun bytesPerSecondPerSpeedUnit(unit: SpeedUnit): Double {
        return when (unit) {
            SpeedUnit.Mbps -> MEGABITS_TO_BYTES
            SpeedUnit.Gbps -> GIGABITS_TO_BYTES
        }
    }

    private fun sanitizeNumericInput(value: String): String {
        val normalized = value.replace(',', '.').filter { it.isDigit() || it == '.' }
        val firstDot = normalized.indexOf('.')
        if (firstDot == -1) return normalized
        val integerPart = normalized.substring(0, firstDot + 1)
        val decimalPart = normalized.substring(firstDot + 1).replace(".", "")
        return integerPart + decimalPart
    }

    private companion object {
        const val EMPTY_TIME = "--:--:--"
        const val STRICT_OVERHEAD_BONUS = 5f

        const val BYTES_PER_MB = 1024.0 * 1024.0
        const val BYTES_PER_GB = 1024.0 * 1024.0 * 1024.0
        const val BYTES_PER_TB = 1024.0 * 1024.0 * 1024.0 * 1024.0

        const val MEGABITS_TO_BYTES = 1_000_000.0 / 8.0
        const val GIGABITS_TO_BYTES = 1_000_000_000.0 / 8.0

        const val USB_64_GB_BYTES = 64.0 * 1024.0 * 1024.0 * 1024.0
        const val DVD_4_7_GB_BYTES = 4.7 * 1024.0 * 1024.0 * 1024.0
    }
}
