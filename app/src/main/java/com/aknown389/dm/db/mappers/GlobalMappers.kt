package com.aknown389.dm.db.mappers

import com.aknown389.dm.db.local.HomeFeedDataEntity
import com.aknown389.dm.db.local.ImageDataModelEntity
import com.aknown389.dm.db.local.UserProfileDetailsDataEntities
import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.models.homepostmodels.PostDataModel
import com.aknown389.dm.models.profileGalleryModels.ImageDataModel
import com.aknown389.dm.models.profileModel.UserProfileData


fun UserProfileDetailsDataEntities.toUserProfiledata():UserProfileData{
    return UserProfileData(
        bgimg = backGroundImage!!,
        bio = bio!!,
        email = email!!,
        first_name = firstName!!,
        followers = followers!!,
        following = following!!,
        id = id,
        last_name = lastName!!,
        location = location!!,
        name = name!!,
        profileimg = profileImage!!,
        user = username,
        username = username,
        post_lenght = postLength!!
    )
}
fun UserProfileData.toUserProfileDetailsDataEntities():UserProfileDetailsDataEntities{
    return UserProfileDetailsDataEntities(
        id = id,
        backGroundImage = bgimg,
        bio = bio,
        email = email,
        firstName = first_name,
        followers = followers,
        following = following,
        lastName = last_name,
        location = location,
        name = name,
        profileImage = profileimg,
        username = username,
        postLength = post_lenght,
    )
}

fun mapListImageDataModelToListImageDataModelEntity(input:List<ImageUrl>): List<ImageDataModelEntity>{
    return input.map { image ->
        ImageDataModelEntity(
            id = image.id!!,
            noOfLike = image.noOfLike!!,
            noOfComment = image.noOfComment!!,
            image = image.original!!,
            isLike = image.isLike!!,
            width = image.width,
            height = image.height,
            imgW500 = image.imgW500,
            imgW1000 = image.imgW1000,
            imgW250 = image.imgW250,
            reactionType = image.reactionType,
            reactions = image.reactions
        )
    }
}

fun mapListImageDataModelEntityToListImageDataModel(input: List<ImageDataModelEntity>):List<ImageUrl>{
    return input.map { entity ->
        ImageUrl(
            id = entity.id,
            noOfLike = entity.noOfLike,
            noOfComment = entity.noOfComment,
            original = entity.image,
            imgW250 = entity.imgW250,
            imgW500 = entity.imgW500,
            imgW1000 = entity.imgW1000,
            isLike = entity.isLike,
            width = entity.width,
            height = entity.height,
            reactionType = entity.reactionType,
            reactions = entity.reactions
        )
    }
}

fun mapPostDataModelListToHomeFeedDataEntityList(input: List<PostDataModel>): List<HomeFeedDataEntity> {
    return input.map { postDataModel ->
        HomeFeedDataEntity(
            id = postDataModel.id,
            noOfComment = postDataModel.NoOfcomment,
            noOfLike = postDataModel.NoOflike,
            createdAt = postDataModel.created_at,
            creator = postDataModel.creator,
            creatorFullName = postDataModel.creator_full_name,
            description = postDataModel.description,
            imageUrl = postDataModel.image_url!!,
            mediaType = postDataModel.media_type,
            title = postDataModel.title,
            videosUrl = postDataModel.videos_url!!,
            yourAvatar = postDataModel.your_avatar,
            creatorAvatar = postDataModel.creator_avatar,
            isLike = postDataModel.is_like,
            me = postDataModel.me,
            privacy = postDataModel.privacy,
            videos = postDataModel.videos,
            playbackUrl = postDataModel.playback_url,
            urlW1000 = postDataModel.url_w1000,
            urlW250 = postDataModel.url_w250,
            width = postDataModel.width,
            height = postDataModel.height,
            reactions = postDataModel.reactions!!,
            videoPlayerPlaybackPosition = 0
        )
    }
}
fun mapHomeFeedDataEntityListToPostDataModelList(input: List<HomeFeedDataEntity>): List<PostDataModel> {
    return input.map { homeFeedentity ->
        PostDataModel(
            id = homeFeedentity.id,
            NoOfcomment = homeFeedentity.noOfComment,
            NoOflike = homeFeedentity.noOfLike,
            created_at =homeFeedentity.createdAt,
            creator = homeFeedentity.creator,
            creator_full_name =homeFeedentity.creatorFullName,
            description = homeFeedentity.description,
            image_url = homeFeedentity.imageUrl,
            media_type =homeFeedentity.mediaType,
            title = homeFeedentity.title,
            videos_url = homeFeedentity.videosUrl,
            your_avatar = homeFeedentity.yourAvatar,
            creator_avatar = homeFeedentity.creatorAvatar,
            is_like = homeFeedentity.isLike,
            me = homeFeedentity.me,
            privacy = homeFeedentity.privacy,
            videos = homeFeedentity.videos,
            playback_url = homeFeedentity.playbackUrl,
            url_w1000 = homeFeedentity.urlW1000,
            url_w250 = homeFeedentity.urlW250,
            width = homeFeedentity.width,
            height = homeFeedentity.height,
            reactions = homeFeedentity.reactions,
            videoPlayerPlaybackPosition = 0
        )
    }
}

fun PostDataModel.toHomeFeedDataEntity(): HomeFeedDataEntity {
    return HomeFeedDataEntity(
        id = id,
        noOfComment = NoOfcomment,
        noOfLike = NoOflike,
        createdAt = created_at,
        creator = creator,
        creatorFullName = creator_full_name,
        description = description,
        imageUrl = image_url,
        mediaType = media_type,
        title = title,
        videosUrl = videos_url,
        yourAvatar = your_avatar,
        creatorAvatar = creator_avatar,
        isLike = is_like,
        me = me,
        privacy = privacy,
        videos = videos,
        playbackUrl = playback_url,
        urlW1000 = url_w1000,
        urlW250 = url_w250,
        width = width,
        height = height,
        reactions = reactions!!,
        videoPlayerPlaybackPosition = 0
    )
}

fun HomeFeedDataEntity.toPostDataModel(): PostDataModel{
    return PostDataModel(
        id = id,
        NoOfcomment = noOfComment,
        NoOflike = noOfLike,
        created_at =createdAt,
        creator = creator,
        creator_full_name =creatorFullName,
        description = description,
        image_url =imageUrl,
        media_type =mediaType,
        title = title,
        videos_url = videosUrl,
        your_avatar = yourAvatar,
        creator_avatar = creatorAvatar,
        is_like = isLike,
        me = me,
        privacy = privacy,
        videos = videos,
        playback_url = playbackUrl,
        url_w1000 = urlW1000,
        url_w250 = urlW250,
        width = width,
        height = height,
        reactions = reactions,
        videoPlayerPlaybackPosition = 0
    )
}