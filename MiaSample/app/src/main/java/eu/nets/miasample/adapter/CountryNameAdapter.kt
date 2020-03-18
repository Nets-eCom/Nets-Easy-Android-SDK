package eu.nets.miasample.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import eu.nets.miasample.R
import eu.nets.miasample.model.CountryNameCode

/**
 * *****Copyright (c) 2020 Nets Denmark A/S*****
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

class CountryNameAdapter(val context: Context, var countryList: List<CountryNameCode>) : BaseAdapter() {

    val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var view: View? = convertView
        val viewHolder: ViewHolder

        if (view == null) {
            view = mInflater.inflate(R.layout.view_drop_down_menu, null)
            viewHolder = ViewHolder(view)
            viewHolder.countryName.gravity = Gravity.START
        } else {
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.countryName.text = countryList.get(position).country
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
            view = mInflater.inflate(R.layout.view_drop_down_menu, null)
            viewHolder = ViewHolder(view)
            viewHolder.countryName.gravity = Gravity.CENTER
        } else {
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.countryName.text = countryList.get(position).country
        view?.tag = viewHolder

        return view!!
    }

    override fun getCount(): Int {
        return countryList.count()
    }

    private class ViewHolder(row: View?) {

        val countryName: TextView = row?.findViewById(R.id.countryName) as TextView

    }

    override fun getItem(position: Int): Any? {

        return countryList.get(position)

    }

    override fun getItemId(position: Int): Long {

        return 0

    }

}