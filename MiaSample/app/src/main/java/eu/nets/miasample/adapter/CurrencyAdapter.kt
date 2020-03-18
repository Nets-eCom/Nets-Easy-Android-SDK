package eu.nets.miasample.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import eu.nets.miasample.R
import kotlinx.android.synthetic.main.spinner_preview_item.view.*
import kotlinx.android.synthetic.main.spinner_dropdown_item.view.*

/**
 *  *****Copyright (c) 2020 Nets Denmark A/S*****
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy  of this software
 * and associated documentation files (the "Software"), to deal  in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is  furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
class CurrencyAdapter(context: Context, textViewResId: Int, private var items: List<String>) : ArrayAdapter<String>(context, textViewResId, items) {

    /**
     * Configure the spinner preview item
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var view: View? = convertView
        val viewHolder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.spinner_preview_item, null)
            viewHolder = ViewHolder()
            viewHolder.spinnerItem = view.currencyPreview
            viewHolder.spinnerItem.gravity = Gravity.START
        } else {
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.spinnerItem.text = getItem(position)
        viewHolder.spinnerItem.gravity = Gravity.CENTER
        view?.tag = viewHolder

        return view!!
    }

    /**
     * Configure the spinner dropdown item
     */
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View? = convertView
        val viewHolder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.spinner_dropdown_item, null)
            viewHolder = ViewHolder()
            viewHolder.spinnerItem = view.currencyItem
            viewHolder.spinnerItem.gravity = Gravity.CENTER
        } else {
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.spinnerItem.text = getItem(position)
        viewHolder.spinnerItem.gravity = Gravity.CENTER
        view?.tag = viewHolder

        return view!!
    }

    override fun getCount(): Int {
        return items.count()
    }

    fun getPositionForItem(id: String): Int {
        for (item: String in items) {
            if (item == id) return items.indexOf(item)
        }
        return 0
    }

    inner class ViewHolder {
        lateinit var spinnerItem: TextView
    }
}

