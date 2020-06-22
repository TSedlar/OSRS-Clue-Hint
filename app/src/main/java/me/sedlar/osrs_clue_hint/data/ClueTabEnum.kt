package me.sedlar.osrs_clue_hint.data

import me.sedlar.osrs_clue_hint.R

enum class ClueTabEnum(val titleText: String, val layoutId: Int) {
    ANAGRAM("Anagram", R.layout.fragment_anagram),
    CIPHER("Cipher", R.layout.fragment_cipher),
    COORDINATE("Coordinate", R.layout.fragment_coord),
    CRYPTIC("Cryptic", R.layout.fragment_cryptic),
    MAP("Map", R.layout.fragment_map);
}