package com.example.notify.structure

data class NextHourItem (
    val icon: Int,
    val hour: String,
    val temperature: String
){
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