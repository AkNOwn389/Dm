package com.aknown389.dm.pageView.postViewPage.mappers

import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.models.global.VideoUrl
import com.aknown389.dm.pageView.postViewPage.models.ToDisplayDataModel

fun VideoUrl.toDisplayDataModel():ToDisplayDataModel{
    return ToDisplayDataModel(
        media_type = 2,
        ImageOrVideoId = id.toString(),
        videoUrl1000 = w1000,
        videoUrl250 = w250,
        videoUrl500 = w500,
        videoUrlOriginal = original,
        thumbnail = thumbnail,
        heigth = height,
        width = width,
    )
}

fun ImageUrl.toDisplayDataModel():ToDisplayDataModel{
    return ToDisplayDataModel(
        NoOfcomment = noOfComment,
        NoOflike = noOfLike,
        media_type = 1,
        imageUrlOriginal = original,
        imageUrl1000 = imgW1000,
        imageUrl500 = imgW500,
        imageUrl250 = imgW250,
        ImageOrVideoId = id,
        reactions = reactions,
        reactionType = reactionType,
        heigth = height,
        width = height,
        isLike = isLike,
    )
}

fun ToDisplayDataModel.toImageUrl():ImageUrl{
    return ImageUrl(
        id=ImageOrVideoId,
        original = imageUrlOriginal,
        noOfLike = NoOflike,
        noOfComment = NoOfcomment,
        isLike = isLike,
        width = width,
        height = heigth,
        reactions = reactions,
        reactionType = reactionType,
        imgW1000 = imageUrl1000,
        imgW500 = imageUrl500,
        imgW250 = imageUrl250,
    )
}

fun ToDisplayDataModel.toVideoUrl():VideoUrl{
    return VideoUrl(
        ImageOrVideoId,
        videoUrlOriginal,
        videoUrl1000,
        videoUrl500,
        videoUrl250,
        thumbnail,
        playbackUrl,
        width,
        heigth,
    )
}