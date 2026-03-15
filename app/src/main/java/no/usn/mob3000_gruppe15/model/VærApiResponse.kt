package no.usn.mob3000_gruppe15.model


data class VærApiResponse (
    val properties: Properties
)

data class Properties (
    val timeseries: List<Timeseries>
)

data class Timeseries(
    val data: Data
)

data class Data(
    val instant: Instant,
    val next_1_hours: NextHours?
)

data class Instant(
    val details: Details
)

data class Details(
    val air_temperature: Double,
    val wind_speed: Double,
    val wind_from_direction: Double
)

data class NextHours(
    val summary: Summary
)

data class Summary(
    val symbol_code: String
)