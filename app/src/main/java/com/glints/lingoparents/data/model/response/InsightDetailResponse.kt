package com.glints.lingoparents.data.model.response

import com.glints.lingoparents.data.model.InsightCommentItem

class InsightDetailResponse{
    val status: String? = null
    val message: Message? = null

    data class Message(
        val Master_comments: List<MasterComment>,
        val Master_reports: List<MasterReportX>,
        val Trx_insight_tags: List<TrxInsightTag>,
        val content: String,
        val cover: String,
        val createdAt: String,
        val id: Int,
        val idUser_create: Int,
        val idUser_update: Any,
        val is_active: String,
        val title: String,
        val total_dislike: Int,
        val total_like: Int,
        val total_report: Int,
        val total_views: Int,
        val updatedAt: String,
        var is_liked: Int,
        var is_disliked: Int,
    )

    data class MasterComment(
        val Master_reports: List<MasterReport>,
        val Master_user: MasterUser,
        val comment: String,
        val commentable_type: String,
        val createdAt: String,
        val id: Int,
        val idUser_create: Int,
        val idUser_update: Any,
        val id_commentable: Int,
        val id_user: Int,
        val replies: Int,
        val status: String,
        val total_dislike: Int,
        val total_like: Int,
        val total_report: Int,
        val updatedAt: String,
        var is_liked: Int,
        var is_disliked: Int,
    )

    data class MasterReportX(
        val createdAt: String,
        val id: Int,
        val idUser_create: Int,
        val idUser_update: Any,
        val id_reportable: Int,
        val id_user: Int,
        val report_comment: String,
        val reportable_type: String,
        val updatedAt: String
    )

    data class TrxInsightTag(
        val Master_tag: MasterTag,
        val createdAt: String,
        val id: Int,
        val idUser_create: Int,
        val idUser_update: Any,
        val id_insight: Int,
        val id_tag: Int,
        val updatedAt: Any
    )

    data class MasterReport(
        val createdAt: String,
        val id: Int,
        val idUser_create: Int,
        val idUser_update: Any,
        val id_reportable: Int,
        val id_user: Int,
        val report_comment: String,
        val reportable_type: String,
        val updatedAt: String
    )

    data class MasterUser(
        val Master_parent: MasterParent?,
        val Master_student: MasterStudent?,
        val Master_tutor: Any,
        val id: Int,
        val role: String
    )

    data class MasterParent(
        val address: String,
        val createdAt: String,
        val date_birth: String,
        val firstname: String,
        val gender: String,
        val id: Int,
        val idUser_create: Int,
        val id_occupation: Int,
        val is_active: String,
        val lastname: String,
        val phone: String,
        val photo: String?,
        val referral_code: String,
        val updatedAt: String
    )

    data class MasterStudent(
        val address: String,
        val createdAt: String,
        val date_birth: String,
        val firstname: String,
        val gender: String,
        val id: Int,
        val idUser_create: Int,
        val id_character: Int,
        val id_level: Int,
        val id_sublevel: Int,
        val is_active: String,
        val lastname: String,
        val phone: String,
        val photo: String,
        val referral_code: String,
        val updatedAt: String
    )

    data class MasterTag(
        val createdAt: String,
        val id: Int,
        val idUser_create: Int,
        val idUser_update: Any,
        val tag_name: String,
        val updatedAt: Any
    )
}

fun InsightDetailResponse.mapToInsightCommentItems(): List<InsightCommentItem>? {
    val result = this.message?.Master_comments?.map {
        return@map InsightCommentItem(
            idComment = it.id,
            idUser = it.id_user,
            photo = it.Master_user.Master_parent?.photo,
            name = "${it.Master_user.Master_parent?.firstname} ${it.Master_user.Master_parent?.lastname}",
            comment = it.comment,
            totalLike = it.total_like,
            totalDislike = it.total_dislike,
            totalReply = it.replies,
            is_liked = it.is_liked,
            is_disliked = it.is_disliked,
        )
    }

    result?.let {
        return it
    }

    return null
}