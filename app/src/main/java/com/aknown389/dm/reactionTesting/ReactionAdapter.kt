package com.aknown389.dm.reactionTesting

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.aknown389.dm.R
import com.bumptech.glide.Glide

/*
* MIT License
*
* Copyright (c) 2018 AmrDeveloper (Amr Hesham)
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.

* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/
/**
 * Custom ArrayAdapter used to handle the reactions list inside the reactions dialog layout
 */
class ReactionAdapter(context: Context, val reactions: ArrayList<Reaction>) :
    ArrayAdapter<Reaction>(context, R.layout.react_dialog_item, reactions) {
    @SuppressLint("ClickableViewAccessibility", "Recycle")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val vi = LayoutInflater.from(context)
            view = vi.inflate(R.layout.react_dialog_item, parent, false)
        }
        val reaction = getItem(position)
        if (reaction != null) {
            val slideUp = AnimationUtils.loadAnimation(context, com.udevel.widgetlab.R.anim.abc_slide_in_bottom)
            slideUp.duration = 500
            val imageView = view as ImageView?
            imageView?.id = reaction.reactIconId
            Glide.with(context)
                .load(reaction.reactIconId)
                .into(imageView!!)
            imageView.startAnimation(slideUp)
        }
        return view!!
    }
}