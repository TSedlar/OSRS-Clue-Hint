package me.sedlar.osrs_clue_hint.component

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import me.sedlar.osrs_clue_hint.MainActivity
import me.sedlar.osrs_clue_hint.R
import me.sedlar.osrs_clue_hint.data.CoordData
import me.sedlar.osrs_clue_hint.helper.DownloadImageTask
import me.sedlar.osrs_clue_hint.helper.LineBreakParser

class ExpandableCoordAdapter internal constructor(
    private val context: Context,
    private val data: List<CoordData>
) : BaseExpandableListAdapter() {

    private val dataList = HashMap<String, List<String>>()
    private val titleList = data.map { it.coord }

    private val viewMap = HashMap<String, ArrayList<View>>()

    init {
        data.forEach { coord ->
            dataList[coord.coord] = listOf(coord.requirement, coord.fight, coord.notes, coord.image, coord.mapImage)
            viewMap[coord.coord] = ArrayList()
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
        val coord = data[listPosition]
        val viewList = viewMap[coord.coord]!!

        if (viewList.isEmpty()) {
            val layoutInflater =
                this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val requirementView = layoutInflater.inflate(R.layout.list_item_coord, null)
            requirementView.findViewById<TextView>(R.id.expandedListItemTitle)?.text = "Requirements:"
            requirementView.findViewById<TextView>(R.id.expandedListItem)?.let {
                it.text = LineBreakParser.parse(coord.requirement)
            }

            val fightView = layoutInflater.inflate(R.layout.list_item_coord, null)
            fightView.findViewById<TextView>(R.id.expandedListItemTitle)?.text = "Fight:"
            fightView.findViewById<TextView>(R.id.expandedListItem)?.let {
                it.text = LineBreakParser.parse(coord.fight)
            }

            val imageView = layoutInflater.inflate(R.layout.list_item_coord_image, null)

            imageView?.findViewById<TextView>(R.id.expandedListItemTitle)?.text = "Location:"
            imageView?.findViewById<ImageView>(R.id.expandedListItem)?.let { locImageView ->
                locImageView.setOnClickListener {
                    MainActivity.solutionDialog?.hide()
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(coord.image))
                    context.startActivity(browserIntent)
                }

                DownloadImageTask(locImageView, onFinish = {
                    imageView.findViewById<ProgressBar>(R.id.progressIndicator)?.visibility = View.GONE
                    locImageView.visibility = View.VISIBLE
                })
                    .execute(coord.image)
            }

            val mapImageView = layoutInflater.inflate(R.layout.list_item_coord_image, null)

            mapImageView?.findViewById<TextView>(R.id.expandedListItemTitle)?.text = "Map Location:"
            mapImageView?.findViewById<ImageView>(R.id.expandedListItem)?.let { locMapImageView ->
                locMapImageView.setOnClickListener {
                    MainActivity.solutionDialog?.hide()
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(coord.mapImage))
                    context.startActivity(browserIntent)
                }

                DownloadImageTask(locMapImageView, onFinish = {
                    mapImageView.findViewById<ProgressBar>(R.id.progressIndicator)?.visibility = View.GONE
                    locMapImageView.visibility = View.VISIBLE
                })
                    .execute(coord.mapImage)
            }

            val noteView = layoutInflater.inflate(R.layout.list_item_coord, null)
            noteView.findViewById<TextView>(R.id.expandedListItemTitle)?.text = "Notes:"
            noteView.findViewById<TextView>(R.id.expandedListItem)?.let {
                it.text = LineBreakParser.parse(coord.notes)
            }

            viewList.addAll(arrayOf(requirementView, fightView, imageView, mapImageView, noteView))
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
