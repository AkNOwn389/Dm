package com.aknown389.dm.db.mappers


import com.aknown389.dm.db.local.NewsDataEntities
import com.aknown389.dm.models.news.Data

fun Data.toNewsDataEntities(): NewsDataEntities {
    return NewsDataEntities(
        id = id.toString(),
        author = author,
        avatar = avatar,
        content = content,
        description = description,
        name = name,
        news_id = news_id,
        noOfComment = noOfComment,
        noOfLike = noOfComment,
        noOfShare = noOfShare,
        publishedAt = publishedAt,
        title = title,
        url = url,
        urlToImage = urlToImage,
        newsType = newsType,
        is_like = is_like,
        reactions = reactions,
        reactionType = reactionType
    )
}

fun NewsDataEntities.toData():Data{
    return Data(
        id = id,
        author = author,
        avatar = avatar,
        content = content,
        description = description,
        name = name,
        news_id = news_id,
        noOfComment = noOfComment,
        noOfLike = noOfLike,
        noOfShare = noOfShare,
        publishedAt = publishedAt,
        title = title,
        url = url,
        urlToImage = urlToImage,
        newsType = newsType,
        is_like = is_like,
        reactions = reactions,
        reactionType = reactionType
    )
}