package me.sedlar.osrs_clue_hint.component

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import me.sedlar.osrs_clue_hint.MainActivity
import me.sedlar.osrs_clue_hint.R
import me.sedlar.osrs_clue_hint.data.MapData
import me.sedlar.osrs_clue_hint.helper.DownloadImageTask
import me.sedlar.osrs_clue_hint.helper.LineBreakParser

class ExpandableMapAdapter internal constructor(
    private val context: Context,
    private val data: List<MapData>
) : BaseExpandableListAdapter() {

    private val dataList = HashMap<String, List<String>>()
    private val titleList = data.map { it.notes }

    private val groupMap = HashMap<String, View>()
    private val viewMap = HashMap<String, ArrayList<View>>()

    init {
        data.forEach { map ->
            dataList[map.notes] = listOf(map.notes, map.image)
            viewMap[map.notes] = ArrayList()
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
        val map = data[listPosition]
        val viewList = viewMap[map.notes]!!

        if (viewList.isEmpty()) {
            val layoutInflater =
                this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val noteView = layoutInflater.inflate(R.layout.list_item_map, null)
            noteView.findViewById<TextView>(R.id.expandedListItemTitle)?.text = "Notes:"
            noteView.findViewById<TextView>(R.id.expandedListItem)?.let {
                it.text = LineBreakParser.parse(map.notes)
            }

            val imageView = layoutInflater.inflate(R.layout.list_item_map_image, null)

            imageView?.findViewById<TextView>(R.id.expandedListItemTitle)?.text = "Location:"
            imageView?.findViewById<ImageView>(R.id.expandedListItem)?.let { locImageView ->
                locImageView.setOnClickListener {
                    MainActivity.solutionDialog?.hide()
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(map.image))
                    context.startActivity(browserIntent)
                }

                DownloadImageTask(locImageView, onFinish = {
                    imageView.findViewById<ProgressBar>(R.id.progressIndicator)?.visibility = View.GONE
                    locImageView.visibility = View.VISIBLE
                })
                    .execute(map.image)
            }

            viewList.addAll(arrayOf(noteView, imageView))
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
        val map = data[listPosition]

        if (!groupMap.containsKey(map.notes)) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater.inflate(R.layout.list_group_image, null)

            val listTitleTextView = view!!.findViewById<ImageView>(R.id.listTitle)

            DownloadImageTask(listTitleTextView, onFinish = {
                view.findViewById<ProgressBar>(R.id.progressIndicator)?.visibility = View.GONE
                listTitleTextView.visibility = View.VISIBLE
            })
                .execute(map.map)

            groupMap[map.notes] = view
        }

        return groupMap[map.notes]!!
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }
}
