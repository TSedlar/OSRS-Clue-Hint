package me.sedlar.osrs_clue_hint.component

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import me.sedlar.osrs_clue_hint.data.ClueDataHandler
import me.sedlar.osrs_clue_hint.data.ClueTabEnum

class CluePager(private val mContext: Context) : PagerAdapter() {
    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val clueTab = ClueTabEnum.values()[position]
        val inflater = LayoutInflater.from(mContext)
        val layout = inflater.inflate(clueTab.layoutId, collection, false) as ViewGroup

        when (clueTab) {
            ClueTabEnum.ANAGRAM -> ClueDataHandler.insertAnagrams(layout)
            ClueTabEnum.CIPHER -> ClueDataHandler.insertCiphers(layout)
            ClueTabEnum.COORDINATE -> ClueDataHandler.insertCoords(layout)
            ClueTabEnum.CRYPTIC -> ClueDataHandler.insertCryptics(layout)
            ClueTabEnum.MAP -> ClueDataHandler.insertMaps(layout)
        }

        collection.addView(layout)
        return layout
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun getCount(): Int {
        return ClueTabEnum.values().size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val clueTab = ClueTabEnum.values()[position]
        return clueTab.titleText
    }
}