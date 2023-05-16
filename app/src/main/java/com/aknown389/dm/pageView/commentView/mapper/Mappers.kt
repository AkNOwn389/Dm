package com.aknown389.dm.pageView.commentView.mapper

import com.aknown389.dm.models.global.PostReactions
import com.aknown389.dm.pageView.commentView.models.Data
import com.aknown389.dm.pageView.commentView.models.Reactions


fun Data.toCommentData(): Data {
    return Data(
        id = id,
        avatar = avatar,
        comments = comments,
        created = created,
        postId = postId,
        image = image,
        video = video,
        user = user,
        type = type,
        me = true,
        followed = followed,
        follower = follower,
        userFullName = userFullName,
        noOfLike = noOfLike,
        isLike = isLike,
        reactions = reactions,
        reactionType = reactionType,
    )
}
fun Reactions.toPostReaction(): PostReactions {
    return PostReactions(
        Angry = Angry, Happy = Happy, Like = Like, Love = Love, Sad = Sad, Wow = Wow
    )
}