package me.sedlar.osrs_clue_hint.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import me.sedlar.osrs_clue_hint.R
import me.sedlar.osrs_clue_hint.data.AnagramData
import me.sedlar.osrs_clue_hint.helper.LineBreakParser
import java.util.*

class ExpandableAnagramAdapter internal constructor(
    private val context: Context,
    private val data: List<AnagramData>
) : BaseExpandableListAdapter() {

    private val dataList = HashMap<String, List<String>>()
    private val titleList = data.map { it.anagram }
    private val viewMap = HashMap<String, ArrayList<View>>()

    init {
        data.forEach { anagram ->
            dataList[anagram.anagram] = listOf(anagram.solution, anagram.location, anagram.answer)
            viewMap[anagram.anagram] = ArrayList()
        }
    }

    override fun getChild(listPosition: Int, expandedListPosition: Int): Any {
        return this.dataList[this.titleList[listPosition]]!![expandedListPosition]
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    override fun getChildView(
        listPosition: Int,
        expandedListPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val anagram = data[listPosition]
        val viewList = viewMap[anagram.anagram]!!

        if (viewList.isEmpty()) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val solution = layoutInflater.inflate(R.layout.list_item_anagram, null)
            val location = layoutInflater.inflate(R.layout.list_item_anagram, null)
            val answer = layoutInflater.inflate(R.layout.list_item_anagram, null)

            solution.findViewById<TextView>(R.id.expandedListItemTitle)?.text = "Solution:"
            solution.findViewById<TextView>(R.id.expandedListItem)?.text = LineBreakParser.parse(anagram.solution)

            location.findViewById<TextView>(R.id.expandedListItemTitle)?.text = "Location:"
            location.findViewById<TextView>(R.id.expandedListItem)?.text = LineBreakParser.parse(anagram.location)

            answer.findViewById<TextView>(R.id.expandedListItemTitle)?.text = "Challenge Answer:"
            answer.findViewById<TextView>(R.id.expandedListItem)?.text = LineBreakParser.parse(anagram.answer)

            viewList.addAll(arrayOf(solution, location, answer))
        }

        return viewList[expandedListPosition]
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return this.dataList[this.titleList[listPosition]]!!.size
    }

    override fun getGroup(listPosition: Int): Any {
        return this.titleList[listPosition]
    }

    override fun getGroupCount(): Int {
        return this.titleList.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    @SuppressLint("InflateParams")
    override fun getGroupView(listPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val listTitle = getGroup(listPosition) as String
        if (view == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.list_group, null)
        }
        val listTitleTextView = view!!.findViewById<TextView>(R.id.listTitle)
        listTitleTextView.setTypeface(null, Typeface.BOLD)
        listTitleTextView.text = LineBreakParser.parse(listTitle)
        return view
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }
}
