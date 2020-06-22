package me.sedlar.osrs_clue_hint.helper

import android.os.Build
import android.text.Html
import android.text.Spanned

object LineBreakParser {

    fun parse(string: String) : Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(string, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(string)
        }
    }
}