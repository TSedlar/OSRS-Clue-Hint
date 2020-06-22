package me.sedlar.osrs_clue_hint.component

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
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
import me.sedlar.osrs_clue_hint.data.CrypticData
import me.sedlar.osrs_clue_hint.helper.DownloadImageTask
import me.sedlar.osrs_clue_hint.helper.LineBreakParser

class ExpandableCrypticAdapter internal constructor(
    private val context: Context,
    private val data: List<CrypticData>
) : BaseExpandableListAdapter() {

    private val dataList = HashMap<String, List<String>>()
    private val titleList = data.map { it.clue }

    private val viewMap = HashMap<String, ArrayList<View>>()

    init {
        data.forEach { cryptic ->
            dataList[cryptic.clue] = listOf(cryptic.notes, cryptic.image)
            viewMap[cryptic.clue] = ArrayList()
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
        val cryptic = data[listPosition]
        val viewList = viewMap[cryptic.clue]!!

        if (viewList.isEmpty()) {
            val layoutInflater =
                this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val noteView = layoutInflater.inflate(R.layout.list_item_cryptic, null)
            noteView.findViewById<TextView>(R.id.expandedListItemTitle)?.text = "Notes:"
            noteView.findViewById<TextView>(R.id.expandedListItem)?.let {
                it.text = LineBreakParser.parse(cryptic.notes)
            }

            val imageView = layoutInflater.inflate(R.layout.list_item_cryptic_image, null)

            imageView?.findViewById<TextView>(R.id.expandedListItemTitle)?.text = "Location:"
            imageView?.findViewById<ImageView>(R.id.expandedListItem)?.let { locImageView ->
                locImageView.setOnClickListener {
                    MainActivity.solutionDialog?.hide()
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(cryptic.image))
                    context.startActivity(browserIntent)
                }

                DownloadImageTask(locImageView, onFinish = {
                    imageView.findViewById<ProgressBar>(R.id.progressIndicator)?.visibility = View.GONE
                    locImageView.visibility = View.VISIBLE
                })
                    .execute(cryptic.image)
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
