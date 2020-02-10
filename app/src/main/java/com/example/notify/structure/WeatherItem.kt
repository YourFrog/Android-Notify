package com.example.notify.structure

import java.io.FileDescriptor

data class WeatherItem (
    val icon: Int,
    val dayOfWeek: String,
    val day: Day,
    val night: Day,
    val sun: DayPhase,
    val moon: DayPhase
) {
    data class Day(
        val temperature: String?,
        val wind: String?,
        val wetness: String?,
        val description: String?
    )

    data class DayPhase(
        val up: String,
        val down: String
    )

    fun description() : String{
        day.description?.let { return@description it }

        return night.description ?: ""
    }

    fun wind(): String {
        day.wind?.let { return@wind it }

        return night.wind ?: ""
    }

    fun wetness(): String {
        day.wetness?.let { return@wetness it }

        return night.wetness ?: ""
    }

    fun temperature(): String {
        day.temperature?.let { return@temperature it }

        return night.temperature ?: ""
    }

    fun textIcon() : String {
        return when(icon) {
            1 -> "\uf019" // deszcz
            2 -> "\uf013" // pochmurnie break;
            3 -> "\uf04e" // nieliczne przelotne opady
            4 -> "\uf018" // przelotne deszcze
            5 -> "\uf01c" // lekki deszcz
            6 -> "\uf041" // 'przewaga chmur'
            7 -> "\uF000" //częściowe zachmurzenie
            8 -> "\uf00d" //bezchmurnie
            9 -> "\uf000" // przeważnie bezchmurnie
            10 -> "\uf002" // słonecznie
            11 -> "\uF00C" // przewaga słońca
            12 -> "\uf005" // lokalne burze z piorunami
            13 -> "\uf01e" // rozproszone burze z piorunami
            else ->
                "??"
        }
    }
}