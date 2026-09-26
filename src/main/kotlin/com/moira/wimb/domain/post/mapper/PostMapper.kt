package com.moira.wimb.domain.post.mapper

import com.moira.wimb.domain.post.dto.response.PostListResponse
import com.moira.wimb.domain.post.entity.PostBase
import com.moira.wimb.domain.post.entity.PostNormal
import com.moira.wimb.domain.post.entity.PostSnippet
import com.moira.wimb.domain.post.entity.PostTag
import org.apache.ibatis.annotations.Mapper

@Mapper
interface PostMapper {
    fun selectIdAndUserIdChk(postId: String, userId: String): Boolean
    fun selectCategoryUsingChk(categoryId: Long): Boolean

    fun selectAll(userId: String, searchType: String?): List<PostListResponse>

    fun insertPostBase(postBase: PostBase)
    fun insertPostSnippet(postSnippet: PostSnippet)
    fun insertPostNormal(postNormal: PostNormal)
    fun insertPostTagBulk(tags: List<PostTag>)

    fun updatePostStatusDeleted(postId: String)
}