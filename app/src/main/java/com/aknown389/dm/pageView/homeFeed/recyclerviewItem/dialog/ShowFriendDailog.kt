package com.aknown389.dm.pageView.homeFeed.recyclerviewItem.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.aknown389.dm.R
import com.aknown389.dm.models.homepostmodels.PostDataModel
import com.aknown389.dm.pageView.homeFeed.utility.HomeFeedCardViewAdapter
import com.aknown389.dm.pageView.homeFeed.utility.HomeFeedRecyclerViewHolder
import com.google.android.material.bottomsheet.BottomSheetDialog

class ShowFriendDailog(
    private val context: Context,
    private val parent: ViewGroup,
    private val holder: HomeFeedRecyclerViewHolder,
    private val token:String,
    private val postListdata:ArrayList<PostDataModel>,
    private val currentItem: PostDataModel,
    private val adapterContext: HomeFeedCardViewAdapter
) {

    init {
        val dialog = BottomSheetDialog(context, R.style.BottomSheetTheme)
        val view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_home_feed_cardview, parent, false)
        val favorite: TextView? = view.findViewById(R.id.addtoFavorite)
        val unfollow: TextView? = view.findViewById(R.id.unfollow)
        val hidePosts: TextView? = view.findViewById(R.id.hideposts)
        val report: TextView? = view.findViewById(R.id.report)
        dialog.setContentView(view)
        dialog.show()
        favorite?.text = "Add ${currentItem.creator_full_name} to favorite"
        unfollow?.text = "Unfollow ${currentItem.creator_full_name}"
        hidePosts?.text = "Hide all ${currentItem.creator_full_name} posts"
    }
}