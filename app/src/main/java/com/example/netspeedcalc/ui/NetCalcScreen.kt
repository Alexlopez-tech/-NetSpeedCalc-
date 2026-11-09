package com.example.netspeedcalc.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Usb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.netspeedcalc.model.NetCalcUiState
import com.example.netspeedcalc.model.Protocol
import com.example.netspeedcalc.model.SizeUnit
import com.example.netspeedcalc.model.SpeedUnit
import com.example.netspeedcalc.model.StatusColorType
import com.example.netspeedcalc.ui.theme.NetSpeedCalcTheme
import kotlin.math.roundToInt

@Composable
fun NetCalcScreen(
    uiState: NetCalcUiState,
    onFileSizeChanged: (String) -> Unit,
    onSizeUnitSelected: (SizeUnit) -> Unit,
    onSpeedChanged: (String) -> Unit,
    onSpeedUnitSelected: (SpeedUnit) -> Unit,
    onOverheadChanged: (Float) -> Unit,
    onProtocolSelected: (Protocol) -> Unit,
    onStrictOverheadToggled: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "NetSpeedCalc",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Calcula el tiempo de transferencia de tus archivos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DataInputSection(
            fileSizeText = uiState.fileSizeText,
            selectedSizeUnit = uiState.selectedSizeUnit,
            onFileSizeChanged = onFileSizeChanged,
            onSizeUnitSelected = onSizeUnitSelected
        )

        NetworkConfigSection(
            speedValueText = uiState.speedValueText,
            selectedSpeedUnit = uiState.selectedSpeedUnit,
            onSpeedChanged = onSpeedChanged,
            onSpeedUnitSelected = onSpeedUnitSelected
        )

        ProtocolSelectorSection(
            selectedProtocol = uiState.selectedProtocol,
            isStrictOverheadEnabled = uiState.isStrictOverheadEnabled,
            onProtocolSelected = onProtocolSelected,
            onStrictOverheadToggled = onStrictOverheadToggled
        )

        OverheadSliderSection(
            overheadPercentage = uiState.overheadPercentage,
            onOverheadChanged = onOverheadChanged
        )

        ResultSummaryCard(uiState = uiState)

        StorageEquivalenceList(uiState = uiState)

        StatusBadgeIndicator(uiState = uiState)

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun DataInputSection(
    fileSizeText: String,
    selectedSizeUnit: SizeUnit,
    onFileSizeChanged: (String) -> Unit,
    onSizeUnitSelected: (SizeUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SectionTitle(text = "1. Tamaño del archivo")
        OutlinedTextField(
            value = fileSizeText,
            onValueChange = onFileSizeChanged,
            label = { Text(text = "Tamaño") },
            placeholder = { Text(text = "Ej. 1.5") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SizeUnit.entries.forEach { unit ->
                FilterChip(
                    selected = selectedSizeUnit == unit,
                    onClick = { onSizeUnitSelected(unit) },
                    label = { Text(text = unit.label) }
                )
            }
        }
    }
}

@Composable
fun NetworkConfigSection(
    speedValueText: String,
    selectedSpeedUnit: SpeedUnit,
    onSpeedChanged: (String) -> Unit,
    onSpeedUnitSelected: (SpeedUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SectionTitle(text = "2. Velocidad de red")
        OutlinedTextField(
            value = speedValueText,
            onValueChange = onSpeedChanged,
            label = { Text(text = "Velocidad") },
            placeholder = { Text(text = "Ej. 100") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SpeedUnit.entries.forEach { unit ->
                FilterChip(
                    selected = selectedSpeedUnit == unit,
                    onClick = { onSpeedUnitSelected(unit) },
                    label = { Text(text = unit.label) }
                )
            }
        }
    }
}

@Composable
fun ProtocolSelectorSection(
    selectedProtocol: Protocol,
    isStrictOverheadEnabled: Boolean,
    onProtocolSelected: (Protocol) -> Unit,
    onStrictOverheadToggled: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SectionTitle(text = "3. Protocolo y modo estricto")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Protocol.entries.forEach { protocol ->
                FilterChip(
                    selected = selectedProtocol == protocol,
                    onClick = { onProtocolSelected(protocol) },
                    label = { Text(text = protocol.label) }
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Modo estricto",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Añade un 5% adicional de overhead",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Switch(
                checked = isStrictOverheadEnabled,
                onCheckedChange = onStrictOverheadToggled
            )
        }
    }
}

@Composable
fun OverheadSliderSection(
    overheadPercentage: Float,
    onOverheadChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SectionTitle(text = "4. Overhead de red")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pérdida estimada",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${overheadPercentage.roundToInt()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = overheadPercentage,
            onValueChange = onOverheadChanged,
            valueRange = 0f..100f,
            steps = 99,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ResultSummaryCard(
    uiState: NetCalcUiState,
    modifier: Modifier = Modifier
) {
    val effectiveOverhead = (
        uiState.overheadPercentage +
            if (uiState.isStrictOverheadEnabled) STRICT_OVERHEAD_BONUS else 0f
    ).coerceIn(0f, 100f)
    val efficiency = (1f - effectiveOverhead / 100f).coerceIn(0f, 1f)

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Resumen del cálculo",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = uiState.formattedTimeString,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tiempo estimado de transferencia",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            SummaryRow(
                label = "Protocolo",
                value = uiState.selectedProtocol.label
            )
            SummaryRow(
                label = "Overhead aplicado",
                value = "${effectiveOverhead.roundToInt()}%"
            )
            SummaryRow(
                label = "Modo estricto",
                value = if (uiState.isStrictOverheadEnabled) "Activado" else "Desactivado"
            )
            Text(
                text = "Eficiencia de transferencia",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
            LinearProgressIndicator(
                progress = { efficiency },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            Text(
                text = "${(efficiency * 100).roundToInt()}% de la velocidad configurada",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StorageEquivalenceList(
    uiState: NetCalcUiState,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Equivalencia en almacenamiento",
                style = MaterialTheme.typography.titleMedium
            )
            EquivalenceRow(
                icon = Icons.Outlined.Usb,
                title = "USB de 64 GB",
                value = uiState.usb64gbEquivalents
            )
            EquivalenceRow(
                icon = Icons.Outlined.Album,
                title = "DVD de 4.7 GB",
                value = uiState.dvdEquivalents
            )
        }
    }
}

@Composable
fun StatusBadgeIndicator(
    uiState: NetCalcUiState,
    modifier: Modifier = Modifier
) {
    val containerColor = when (uiState.statusColorType) {
        StatusColorType.GREEN -> Color(0xFF2E7D32)
        StatusColorType.YELLOW -> Color(0xFFFFB300)
        StatusColorType.RED -> Color(0xFFC62828)
    }
    val contentColor = when (uiState.statusColorType) {
        StatusColorType.YELLOW -> Color(0xFF1C1B1F)
        else -> Color.White
    }
    val icon = when (uiState.statusColorType) {
        StatusColorType.GREEN -> Icons.Filled.CheckCircle
        StatusColorType.YELLOW -> Icons.Filled.Warning
        StatusColorType.RED -> Icons.Filled.Error
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Diagnóstico de rendimiento",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = uiState.performanceDiagnosis,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun EquivalenceRow(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$value unidades",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private const val STRICT_OVERHEAD_BONUS = 5f

@Preview(showBackground = true)
@Composable
private fun NetCalcScreenPreview() {
    NetSpeedCalcTheme {
        NetCalcScreen(
            uiState = NetCalcUiState(
                formattedTimeString = "00:01:23",
                performanceDiagnosis = "Excelente",
                statusColorType = StatusColorType.GREEN,
                usb64gbEquivalents = "0.02",
                dvdEquivalents = "0.21"
            ),
            onFileSizeChanged = {},
            onSizeUnitSelected = {},
            onSpeedChanged = {},
            onSpeedUnitSelected = {},
            onOverheadChanged = {},
            onProtocolSelected = {},
            onStrictOverheadToggled = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}
