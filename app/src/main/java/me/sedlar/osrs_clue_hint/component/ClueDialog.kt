package me.sedlar.osrs_clue_hint.component

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import me.sedlar.osrs_clue_hint.R


class ClueDialog(context: Context) : Dialog(context), TabLayout.OnTabSelectedListener {

    private var pager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private var adapter: CluePager? = null

    init {
        setTitle("Clue Solutions")
        setContentView(R.layout.dialog_solutions)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        val tabListener = this

        this.window?.decorView?.findViewById<View?>(android.R.id.content)?.let { view ->
            pager = view.findViewById(R.id.pager)

            tabLayout = view.findViewById(R.id.tabLayout)

            adapter = CluePager(context)
            pager?.adapter = adapter

            tabLayout?.addOnTabSelectedListener(tabListener)
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tab?.let { pager?.currentItem = it.position }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }
}