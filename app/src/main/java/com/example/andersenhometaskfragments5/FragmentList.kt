package com.example.andersenhometaskfragments5

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class FragmentList : Fragment(R.layout.list_fragment), BackPressedListener,
    DialogFragmentDelete.DeleteButtonClickedListener {

    private var index = 0
    private lateinit var infoClickListener: InfoClickListener
    private lateinit var listFragment: MutableList<Contact>
    private lateinit var newList: MutableList<Contact>
    private lateinit var rvContact: RecyclerView
    private lateinit var contactAdapter: AdapterContacts
    private lateinit var svName: SearchView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is InfoClickListener) infoClickListener = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listFragment = requireArguments().getParcelableArrayList(KEY_LIST)!!
        index = requireArguments().getInt(KEY_INDEX)
        newList = mutableListOf()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvContact = requireView().findViewById(R.id.rvContacts)
        rvContact.layoutManager = LinearLayoutManager(requireContext())
        contactAdapter = AdapterContacts(listFragment, infoClickListener, childFragmentManager)
        rvContact.adapter = contactAdapter
        rvContact.addItemDecoration(MyItemDecoration())
        svName = requireView().findViewById(R.id.search_name)
        svName.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(p0: String?): Boolean {
                contactAdapter.filter.filter(p0)
                return false
            }
        })
    }

    class AdapterContacts(
        private var listAdapter: MutableList<Contact>,
        private val infoClickListener: InfoClickListener,
        private val childFM: FragmentManager
    ) : RecyclerView.Adapter<AdapterContacts.MyViewHolder>(), Filterable {

        private val picasso = Picasso.get()
        var contactFilterList: MutableList<Contact> = listAdapter

        class MyViewHolder(
            itemView: View,
            infoClickListener: InfoClickListener,
            childFM: FragmentManager,
            list: MutableList<Contact>
        ) : RecyclerView.ViewHolder(itemView) {
            var tvFirstName: TextView? = null
            var tvLastName: TextView? = null
            var tvNumber: TextView? = null
            var ivImage: ImageView? = null

            init {
                tvFirstName = itemView.findViewById(R.id.tvFirstNameItem)
                tvLastName = itemView.findViewById(R.id.tvLastNameItem)
                tvNumber = itemView.findViewById(R.id.tvNumberItem)
                ivImage = itemView.findViewById(R.id.ivContactItem)
                itemView.run {
                    setOnClickListener {
                        infoClickListener.onInfoClicked(list, absoluteAdapterPosition)
                    }
                    setOnLongClickListener {
                        DialogFragmentDelete.newInstance(
                            tvFirstName!!.text.toString(),
                            tvLastName!!.text.toString(),
                            absoluteAdapterPosition
                        ).show(childFM, DialogFragmentDelete.DIALOG_FRAGMENT_DELETE_TAG)
                        true
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
            return MyViewHolder(itemView, infoClickListener, childFM, listAdapter)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.tvFirstName?.text = contactFilterList[position].firstName
            holder.tvLastName?.text = contactFilterList[position].lastName
            holder.tvNumber?.text = contactFilterList[position].phoneNumber
            picasso.load(contactFilterList[position].pathImage).into(holder.ivImage)
        }

        override fun getItemCount() = contactFilterList.size

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(p0: CharSequence?): FilterResults {
                    val charSearch = p0.toString()
                    contactFilterList = if (charSearch.isEmpty()) {
                        listAdapter
                    } else {
                        val resultList: MutableList<Contact> = mutableListOf()
                        for (row in listAdapter) {
                            if (row.firstName?.lowercase()?.contains(charSearch.lowercase()) == true
                                || row.lastName?.lowercase()?.contains(charSearch.lowercase()) == true
                            ) resultList.add(row)
                        }
                        resultList
                    }
                    val filterResults = FilterResults()
                    filterResults.values = contactFilterList
                    return filterResults
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                    contactFilterList = p1?.values as MutableList<Contact>
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(KEY_LIST, ArrayList<Parcelable>(listFragment))
        outState.putInt(KEY_INDEX, index)
    }

    override fun onBackPressedClicked(): Boolean = false

    interface InfoClickListener {
        fun onInfoClicked(
            list: MutableList<Contact>,
            index: Int
        )
    }

    override fun onDeleteButtonClicked(index: Int) {
        newList = listFragment.toMutableList()
        newList.removeAt(index)
        val contactDiffUtilCallback = ContactDiffUtilCallback(listFragment, newList)
        val contactDiffResult = DiffUtil.calculateDiff(contactDiffUtilCallback)
        contactDiffResult.dispatchUpdatesTo(contactAdapter)
        listFragment.removeAt(index)
    }

    class ContactDiffUtilCallback(
        private val oldList: MutableList<Contact>,
        private val newList: MutableList<Contact>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldContact = oldList[oldItemPosition]
            val newContact = newList[newItemPosition]
            return (oldContact == newContact)
        }
    }

    class MyItemDecoration : RecyclerView.ItemDecoration() {

        private var paint = Paint()

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            if (parent.getChildAdapterPosition(view) != 0) {
                outRect.top = view.resources.getDimension(R.dimen.rv_margin_10).toInt()
                outRect.bottom = view.resources.getDimension(R.dimen.rv_margin_10).toInt()
            }
        }

        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            super.onDraw(c, parent, state)

            paint.color = Color.BLACK

            val childCount = parent.childCount
            var divTop: Float
            var divLeft: Float
            var divBottom: Float
            var divRight: Float

            for (i in 0 until childCount) {
                val view = parent.getChildAt(i)
                val index = parent.getChildAdapterPosition(view)
                if (index == 0) {
                    continue
                }
                divTop = view.top - view.resources.getDimension(R.dimen.div_height_2)
                divLeft = parent.paddingLeft.toFloat()
                divBottom = view.top.toFloat()
                divRight = (parent.width - parent.paddingRight).toFloat()
                c.drawRect(divLeft, divTop, divRight, divBottom, paint)
            }
        }
    }

    companion object {

        private const val KEY_LIST = "KEY_LIST"
        private const val KEY_INDEX = "KEY_INDEX"
        const val FRAGMENT_LIST_TAG = "FRAGMENT_LIST_TAG"

        fun newInstance(list: MutableList<Contact>, index: Int) = FragmentList().also {
            it.arguments = Bundle().apply {
                putParcelableArrayList(KEY_LIST, ArrayList<Parcelable>(list))
                putInt(KEY_INDEX, index)
            }
        }
    }

}