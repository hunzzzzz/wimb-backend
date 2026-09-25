package com.moira.wimb.domain.category.mapper

import com.moira.wimb.domain.category.dto.response.PostCategoryResponse
import com.moira.wimb.domain.category.entity.PostCategory
import org.apache.ibatis.annotations.Mapper

@Mapper
interface PostCategoryMapper {
    fun selectAll(userId: String): List<PostCategoryResponse>
    fun selectAllIds(userId: String): List<Long>
    fun selectCnt(userId: String): Long
    fun selectLastSortOrder(userId: String): Int?

    fun selectIdAndUserIdChk(postCategoryId: Long, userId: String): Boolean
    fun selectNameAndUserIdChk(name: String, userId: String): Boolean
    fun selectNameAndUserIdChk2(name: String, userId: String, postCategoryId: Long): Boolean

    fun insert(postCategory: PostCategory)

    fun updateName(userId: String, postCategoryId: Long, newName: String)
    fun updateSortOrder(userId: String, postCategoryId: Long, newSortOrder: Int)
    fun updateSortOrderWhenDelete(userId: String, deletingPostCategoryId: Long)

    fun delete(userId: String, postCategoryId: Long)
}