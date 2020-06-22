package me.sedlar.osrs_clue_hint.data

import com.opencsv.CSVReader
import me.sedlar.osrs_clue_hint.MainActivity
import me.sedlar.osrs_clue_hint.R
import java.io.InputStreamReader

object ClueDataParser {

    fun parseAnagramData(): List<AnagramData> {
        val list = ArrayList<AnagramData>()

        MainActivity.mainActivityContext?.let { ctx ->
            ctx.resources.openRawResource(R.raw.anagrams).use { input ->
                val reader = CSVReader(InputStreamReader(input))
                reader.readAll().forEach { line ->
                    list.add(
                        AnagramData(
                            line[0], // anagram
                            line[1], // solution
                            line[2], // location
                            line[3] // answer
                        )
                    )
                }
            }
        }

        return list.sortedBy { it.anagram }
    }

    fun parseCipherData(): List<CipherData> {
        val list = ArrayList<CipherData>()

        MainActivity.mainActivityContext?.let { ctx ->
            ctx.resources.openRawResource(R.raw.ciphers).use { input ->
                val reader = CSVReader(InputStreamReader(input))
                reader.readAll().forEach { line ->
                    list.add(
                        CipherData(
                            line[0], // cipher
                            line[1], // solution
                            line[2], // location
                            line[3] // answer
                        )
                    )
                }
            }
        }

        return list.sortedBy { it.cipher }
    }

    fun parseCoordData(): List<CoordData> {
        val list = ArrayList<CoordData>()

        MainActivity.mainActivityContext?.let { ctx ->
            ctx.resources.openRawResource(R.raw.coords).use { input ->
                val reader = CSVReader(InputStreamReader(input))
                reader.readAll().forEach { line ->
                    list.add(
                        CoordData(
                            line[0], // coord
                            line[1], // shorthand
                            line[2], // requirement
                            line[3], // fight
                            line[4], // image
                            line[5], // mapImage
                            line[6] // notes
                        )
                    )
                }
            }
        }

        return list.sortedBy { it.coord }
    }

    fun parseCrypticData(): List<CrypticData> {
        val list = ArrayList<CrypticData>()

        MainActivity.mainActivityContext?.let { ctx ->
            ctx.resources.openRawResource(R.raw.cryptics).use { input ->
                val reader = CSVReader(InputStreamReader(input))
                reader.readAll().forEach { line ->
                    list.add(
                        CrypticData(
                            line[0], // clue
                            line[1], // notes
                            line[2] // image
                        )
                    )
                }
            }
        }

        return list.sortedBy { it.clue }
    }

    fun parseMapData(): List<MapData> {
        val list = ArrayList<MapData>()

        MainActivity.mainActivityContext?.let { ctx ->
            ctx.resources.openRawResource(R.raw.maps).use { input ->
                val reader = CSVReader(InputStreamReader(input))
                reader.readAll().forEach { line ->
                    list.add(
                        MapData(
                            line[0], // map
                            line[1], // location
                            line[2] // image
                        )
                    )
                }
            }
        }

        return list
    }
}

data class AnagramData(val anagram: String, val solution: String, val location: String, val answer: String)

data class CipherData(val cipher: String, val solution: String, val location: String, val answer: String)

data class CoordData(
    val coord: String,
    val shorthand: String,
    val requirement: String,
    val fight: String,
    val image: String,
    val mapImage: String,
    val notes: String
)

data class CrypticData(val clue: String, val notes: String, val image: String)

data class MapData(val map: String, val notes: String, val image: String)