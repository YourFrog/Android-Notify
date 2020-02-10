package com.example.notify.structure

class WeatherList(
    val today: WeatherItem,
    val nextDays: List<WeatherItem>,
    val nextHours: List<NextHourItem>
) {
}