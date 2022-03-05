package com.example.andersenhometaskfragments5

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
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

    private lateinit var infoClickListener: InfoClickListener
    private lateinit var list: MutableList<Contact>
    private lateinit var newList: MutableList<Contact>
    private lateinit var rvContact: RecyclerView
    private lateinit var contactAdapter: AdapterContacts
    private lateinit var svName: SearchView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        infoClickListener = context as InfoClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        list = requireArguments().getParcelableArrayList(KEY_LIST)!!
        newList = mutableListOf()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvContact = requireView().findViewById(R.id.rvContacts)
        rvContact.layoutManager = LinearLayoutManager(requireContext())
        contactAdapter = AdapterContacts(list, infoClickListener, childFragmentManager)
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
        var list: MutableList<Contact>,
        private val infoClickListener: InfoClickListener,
        private val childFM: FragmentManager
    ) : RecyclerView.Adapter<AdapterContacts.MyViewHolder>(), Filterable {

        private val picasso = Picasso.get()
        var contactFilterList: MutableList<Contact> = list

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
                        infoClickListener.onInfoClicked(list, position)
                    }
                    setOnLongClickListener {
                        DialogFragmentDelete.newInstance(
                            tvFirstName!!.text.toString(),
                            tvLastName!!.text.toString(),
                            position
                        ).show(childFM, DialogFragmentDelete.DIALOG_FRAGMENT_DELETE_TAG)
                        true
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
            return MyViewHolder(itemView, infoClickListener, childFM, list)
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
                        list
                    } else {
                        val resultList: MutableList<Contact> = mutableListOf()
                        for (row in list) {
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

                override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                    contactFilterList = p1?.values as MutableList<Contact>
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(KEY_LIST, ArrayList<Parcelable>(list))
    }

    override fun onBackPressedClicked(): Boolean = false

    interface InfoClickListener {
        fun onInfoClicked(
            list: MutableList<Contact>,
            index: Int
        )
    }

    override fun onDeleteButtonClicked(index: Int) {
        newList = list.toMutableList()
        newList.removeAt(index)
        val contactDiffUtilCallback = ContactDiffUtilCallback(list, newList)
        val contactDiffResult = DiffUtil.calculateDiff(contactDiffUtilCallback)
        contactAdapter.list = newList
        contactDiffResult.dispatchUpdatesTo(contactAdapter)
        list = newList
        contactAdapter.contactFilterList = newList
    }

    class ContactDiffUtilCallback(
        private val oldList: MutableList<Contact>,
        private val newList: MutableList<Contact>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldContact = oldList[oldItemPosition]
            val newContact = newList[newItemPosition]
            return (oldContact == newContact)
        }
    }

    class MyItemDecoration : RecyclerView.ItemDecoration() {

        private var dividerHeight = DIV_HEIGHT
        private var paint = Paint()

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            if (parent.getChildAdapterPosition(view) != 0) {
                outRect.top = RV_MARGIN_10
                outRect.bottom = RV_MARGIN_10
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
                divTop = view.top - dividerHeight
                divLeft = parent.paddingLeft.toFloat()
                divBottom = view.top.toFloat()
                divRight = (parent.width - parent.paddingRight).toFloat()
                c.drawRect(divLeft, divTop, divRight, divBottom, paint)
            }
        }
    }

    companion object {

        private const val KEY_LIST = "KEY_LIST"
        const val FRAGMENT_LIST_TAG = "FRAGMENT_LIST_TAG"
        private const val DIV_HEIGHT = 2F
        private const val RV_MARGIN_10 = 10

        fun newInstance(list: MutableList<Contact>) = FragmentList().also {
            it.arguments = Bundle().apply {
                putParcelableArrayList(KEY_LIST, ArrayList<Parcelable>(list))
            }
        }
    }

}