package me.sedlar.osrs_clue_hint.data

import android.view.ViewGroup
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.LinearLayout
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import me.sedlar.osrs_clue_hint.MainActivity
import me.sedlar.osrs_clue_hint.R
import me.sedlar.osrs_clue_hint.component.*

object ClueDataHandler {

    private val STD_KEYS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val COORD_KEYS = listOf(
        "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13",
        "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26"
    )

    fun insertAnagrams(view: ViewGroup) {
        val data = MainActivity.anagramData
        val anagramList = view.findViewById<ExpandableListView>(R.id.anagramList)

        println("Inserting ${data.size} anagrams...")

        // Inserting filters
        insertFilters(data, { anagram, filter ->
            anagram.anagram.toUpperCase().startsWith(filter)
        }, view, anagramList)

        // Inserting anagrams
        anagramList?.setAdapter(ExpandableAnagramAdapter(view.context, data))
    }

    fun insertCiphers(view: ViewGroup) {
        val data = MainActivity.cipherData
        val cipherList = view.findViewById<ExpandableListView>(R.id.cipherList)

        println("Inserting ${data.size} ciphers...")

        // Inserting filters
        insertFilters(data, { cipher, filter ->
            cipher.cipher.toUpperCase().startsWith(filter)
        }, view, cipherList)

        // Inserting anagrams
        cipherList?.setAdapter(ExpandableCipherAdapter(view.context, data))
    }

    fun insertCoords(view: ViewGroup) {
        val data = MainActivity.coordData
        val coordList = view.findViewById<ExpandableListView>(R.id.coordList)
        println("Inserting ${data.size} coords...")

        // Inserting filters
        insertFilters(data, { coord, filter ->
            coord.coord.toUpperCase().startsWith(filter)
        }, view, coordList, COORD_KEYS)

        coordList?.setAdapter(ExpandableCoordAdapter(view.context, data))
    }

    fun insertCryptics(view: ViewGroup) {
        val data = MainActivity.crypticData
        val crypticList = view.findViewById<ExpandableListView>(R.id.crypticList)

        println("Inserting ${data.size} cryptics...")

        // Inserting filters
        insertFilters(data, { cryptic, filter ->
            cryptic.clue.toUpperCase().startsWith(filter)
        }, view, crypticList)

        crypticList?.setAdapter(ExpandableCrypticAdapter(view.context, data))
    }

    fun insertMaps(view: ViewGroup) {
        println("Inserting ${MainActivity.mapData.size} maps...")

        view.findViewById<ExpandableListView>(R.id.mapList)
            ?.setAdapter(ExpandableMapAdapter(view.context, MainActivity.mapData))
    }

    private fun <T> insertFilters(
        data: List<T>,
        findFilter: (T, String) -> Boolean,
        view: ViewGroup,
        listView: ExpandableListView,
        keys: List<String> = STD_KEYS.toCharArray().map { it.toString() }
    ) {
        view.findViewById<LinearLayout>(R.id.filterContainer)?.let { filterContainer ->
            keys.forEach { filter ->
                if (data.any { findFilter(it, filter) }) {
                    val btn = Button(filterContainer.context)

                    btn.text = filter
//                    btn.layoutParams = LinearLayout.LayoutParams(48, 42)
                    btn.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)

                    btn.setPadding(3)
                    (btn.layoutParams as LinearLayout.LayoutParams).setMargins(1, 1, 1, 10)

                    btn.setOnClickListener {
                        data.firstOrNull { findFilter(it, filter) }?.let { firstItem ->
                            listView.smoothScrollToPositionFromTop(data.indexOf(firstItem), 0)
                        }
                    }

                    filterContainer.addView(btn)
                }
            }
        }
    }
}