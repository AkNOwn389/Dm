package com.aknown389.dm.pageView.photoView.utilities

import android.annotation.SuppressLint
import android.view.View
import com.aknown389.dm.databinding.ActivityPhotoViewBinding
import com.aknown389.dm.db.local.NewsDataEntities
import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.models.postmodel.LikesPostResponseModel
import com.aknown389.dm.pageView.newsView.NewsGlobalSetter
import com.aknown389.dm.pageView.newsView.NewsViewHolder
import com.aknown389.dm.pageView.photoView.models.Parcel
import com.aknown389.dm.utils.ReactionTypes

object Setter {
    fun setDefaultReaction(holder: ActivityPhotoViewBinding?, currentItem: ImageUrl){
        when(currentItem.reactionType){
            ReactionTypes.LIKE -> if (holder != null) {
                holder.likePost?.setCurrentImageResource(com.aknown389.dm.reactionTesting.FbReactions.reactions[0])
            }
            ReactionTypes.LOVE -> if (holder != null) {
                holder.likePost?.setCurrentImageResource(com.aknown389.dm.reactionTesting.FbReactions.reactions[1])
            }
            ReactionTypes.HAPPY -> if (holder != null) {
                holder.likePost?.setCurrentImageResource(com.aknown389.dm.reactionTesting.FbReactions.reactions[2])
            }
            ReactionTypes.AMAZE -> if (holder != null) {
                holder.likePost?.setCurrentImageResource(com.aknown389.dm.reactionTesting.FbReactions.reactions[3])
            }
            ReactionTypes.SAD -> if (holder != null) {
                holder.likePost?.setCurrentImageResource(com.aknown389.dm.reactionTesting.FbReactions.reactions[4])
            }
            ReactionTypes.ANGRY -> if (holder != null) {
                holder.likePost?.setCurrentImageResource(com.aknown389.dm.reactionTesting.FbReactions.reactions[5])
            }
        }
    }

    fun afterReactionReasponse(
        holder: ActivityPhotoViewBinding,
        currentItem: LikesPostResponseModel,
        dataModel: ImageUrl
    ) {
        dataModel.noOfLike = currentItem.postLikes
        if (dataModel.isLike == true) {
            when (dataModel.reactionType) {
                ReactionTypes.LIKE -> dataModel.reactions?.Like =
                    dataModel.reactions?.Like?.minus(1)

                ReactionTypes.LOVE -> dataModel.reactions?.Love =
                    dataModel.reactions?.Love?.minus(1)

                ReactionTypes.HAPPY -> dataModel.reactions?.Happy =
                    dataModel.reactions?.Happy?.minus(1)

                ReactionTypes.AMAZE -> dataModel.reactions?.Wow = dataModel.reactions?.Wow?.minus(1)
                ReactionTypes.SAD -> dataModel.reactions?.Sad = dataModel.reactions?.Sad?.minus(1)
                ReactionTypes.ANGRY -> dataModel.reactions?.Angry =
                    dataModel.reactions?.Angry?.minus(1)
            }
            when (currentItem.reaction) {
                ReactionTypes.LIKE -> dataModel.reactions?.Like = dataModel.reactions?.Like?.plus(1)
                ReactionTypes.LOVE -> dataModel.reactions?.Love = dataModel.reactions?.Love?.plus(1)
                ReactionTypes.HAPPY -> dataModel.reactions?.Happy =
                    dataModel.reactions?.Happy?.plus(1)

                ReactionTypes.AMAZE -> dataModel.reactions?.Wow = dataModel.reactions?.Wow?.plus(1)
                ReactionTypes.SAD -> dataModel.reactions?.Sad = dataModel.reactions?.Sad?.plus(1)
                ReactionTypes.ANGRY -> dataModel.reactions?.Angry =
                    dataModel.reactions?.Angry?.plus(1)
            }
        } else {
            when (currentItem.reaction) {
                ReactionTypes.LIKE -> dataModel.reactions?.Like = dataModel.reactions?.Like?.plus(1)
                ReactionTypes.LOVE -> dataModel.reactions?.Love = dataModel.reactions?.Love?.plus(1)
                ReactionTypes.HAPPY -> dataModel.reactions?.Happy =
                    dataModel.reactions?.Happy?.plus(1)

                ReactionTypes.AMAZE -> dataModel.reactions?.Wow = dataModel.reactions?.Wow?.plus(1)
                ReactionTypes.SAD -> dataModel.reactions?.Sad = dataModel.reactions?.Sad?.plus(1)
                ReactionTypes.ANGRY -> dataModel.reactions?.Angry =
                    dataModel.reactions?.Angry?.plus(1)
            }
        }


        dataModel.reactionType = currentItem.reaction
        dataModel.isLike = true
        iconSetterBaseOnLike(holder = holder, currentItem = dataModel)
    }

    fun afterUnReactResponseOn(
        holder: ActivityPhotoViewBinding,
        currentItem: LikesPostResponseModel,
        dataModel: ImageUrl
    ) {
        dataModel.noOfLike = currentItem.postLikes
        when (dataModel.reactionType) {
            ReactionTypes.LIKE -> dataModel.reactions?.Like = dataModel.reactions?.Like?.minus(1)
            ReactionTypes.LOVE -> dataModel.reactions?.Love = dataModel.reactions?.Love?.minus(1)
            ReactionTypes.HAPPY -> dataModel.reactions?.Happy = dataModel.reactions?.Happy?.minus(1)
            ReactionTypes.AMAZE -> dataModel.reactions?.Wow = dataModel.reactions?.Wow?.minus(1)
            ReactionTypes.SAD -> dataModel.reactions?.Sad = dataModel.reactions?.Sad?.minus(1)
            ReactionTypes.ANGRY -> dataModel.reactions?.Angry = dataModel.reactions?.Angry?.minus(1)
        }
        dataModel.reactionType = null
        dataModel.isLike = false
        iconSetterBaseOnLike(holder = holder, currentItem = dataModel)
    }

    @SuppressLint("SetTextI18n")
    fun iconSetterBaseOnLike(holder: ActivityPhotoViewBinding, currentItem: ImageUrl) {
        if (currentItem.reactions?.Like == 0) {
            holder.icLike.visibility = View.GONE
        } else {
            holder.icLike.visibility = View.VISIBLE
        }
        if (currentItem.reactions?.Love == 0) {
            holder.icLove.visibility = View.GONE
        } else {
            holder.icLove.visibility = View.VISIBLE
        }
        if (currentItem.reactions?.Happy == 0) {
            holder.icHappy.visibility = View.GONE
        } else {
            holder.icHappy.visibility = View.VISIBLE
        }
        if (currentItem.reactions?.Wow == 0) {
            holder.icWow.visibility = View.GONE
        } else {
            holder.icWow.visibility = View.VISIBLE
        }
        if (currentItem.reactions?.Sad == 0) {
            holder.icSad.visibility = View.GONE
        } else {
            holder.icSad.visibility = View.VISIBLE
        }
        if (currentItem.reactions?.Angry == 0) {
            holder.icAngry.visibility = View.GONE
        } else {
            holder.icAngry.visibility = View.VISIBLE
        }
        if (currentItem.reactions?.Like == 0
            && currentItem.reactions.Happy == 0
            && currentItem.reactions.Love == 0
            && currentItem.reactions.Wow == 0
            && currentItem.reactions.Sad == 0
            && currentItem.reactions.Angry == 0
        ) {
            holder.NoOfLikes.visibility = View.GONE
        } else {
            holder.NoOfLikes.visibility = View.VISIBLE
            val num = currentItem.reactions
            val noOfAllLike =
                num?.Like!! + num.Love!! + num.Happy!! + num.Wow!! + num.Sad!! + num.Angry!!
            val noToDisplay = if (noOfAllLike > 1000) {
                ((noOfAllLike / 1000).toDouble().toString() + "k")
            } else {
                noOfAllLike.toString()
            }
            currentItem.noOfLike = noOfAllLike
            holder.NoOfLikes.text = noToDisplay
        }
        if (currentItem.noOfComment == 0) {
            holder.NoOfComments.visibility = View.GONE
        } else {
            holder.NoOfComments.visibility = View.VISIBLE
            holder.NoOfComments.text = currentItem.noOfComment.toString() + " comments"
        }
    }
}