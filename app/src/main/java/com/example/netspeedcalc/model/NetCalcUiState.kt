package com.example.netspeedcalc.model

enum class SizeUnit(val label: String) {
    MB("MB"),
    GB("GB"),
    TB("TB")
}

enum class SpeedUnit(val label: String) {
    Mbps("Mbps"),
    Gbps("Gbps")
}

enum class Protocol(val label: String) {
    HTTP_HTTPS("HTTP/HTTPS"),
    FTP("FTP"),
    P2P("P2P")
}

enum class StatusColorType {
    GREEN,
    YELLOW,
    RED
}

data class NetCalcUiState(
    val fileSizeText: String = "1",
    val selectedSizeUnit: SizeUnit = SizeUnit.GB,
    val speedValueText: String = "100",
    val selectedSpeedUnit: SpeedUnit = SpeedUnit.Mbps,
    val overheadPercentage: Float = 10f,
    val selectedProtocol: Protocol = Protocol.HTTP_HTTPS,
    val isStrictOverheadEnabled: Boolean = false,
    val formattedTimeString: String = "--:--:--",
    val performanceDiagnosis: String = "Sin calcular",
    val statusColorType: StatusColorType = StatusColorType.YELLOW,
    val usb64gbEquivalents: String = "0.00",
    val dvdEquivalents: String = "0.00"
)
