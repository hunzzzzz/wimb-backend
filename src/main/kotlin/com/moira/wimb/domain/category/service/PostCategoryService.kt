package com.moira.wimb.domain.category.service

import com.moira.wimb.domain.category.dto.request.PostCategoryAddRequest
import com.moira.wimb.domain.category.dto.request.PostCategoryNameUpdateRequest
import com.moira.wimb.domain.category.dto.request.PostCategoryOrderUpdateRequest
import com.moira.wimb.domain.category.dto.response.PostCategoryResponse
import com.moira.wimb.domain.category.entity.PostCategory
import com.moira.wimb.domain.category.mapper.PostCategoryMapper
import com.moira.wimb.domain.post.mapper.PostMapper
import com.moira.wimb.global.exception.CommonException
import com.moira.wimb.global.exception.ErrorCode
import com.moira.wimb.global.utility.CommonVariables.POST_CATEGORY_MAX_COUNT
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostCategoryService(
    private val postCategoryMapper: PostCategoryMapper,
    private val postMapper: PostMapper
) {
    /**
     * 카테고리 목록 조회
     */
    @Transactional(readOnly = true)
    fun getCategoryList(userId: String): List<PostCategoryResponse> {
        // 1. 조회
        return postCategoryMapper.selectAll(userId)
    }

    /**
     * 카테고리 추가
     */
    @Transactional
    fun addCategory(userId: String, request: PostCategoryAddRequest) {
        // 1. 유효성 검사
        if (postCategoryMapper.selectNameAndUserIdChk(request.name, userId)) {
            throw CommonException(ErrorCode.CATEGORY_NAME_EXISTS)
        }
        if (postCategoryMapper.selectCnt(userId) >= POST_CATEGORY_MAX_COUNT) {
            throw CommonException(ErrorCode.CATEGORY_COUNT_EXCEEDED)
        }

        // 2. sortOrder 조회
        val sortOrder = (postCategoryMapper.selectLastSortOrder(userId) ?: 0) + 1

        // 3. 카테고리 저장
        val postCategory = PostCategory.create(userId, sortOrder, request)
        postCategoryMapper.insert(postCategory)
    }

    /**
     * 카테고리 이름 수정
     */
    @Transactional
    fun updateCategoryName(userId: String, postCategoryId: Long, request: PostCategoryNameUpdateRequest) {
        // 1. 유효성 검사
        if (postCategoryMapper.selectNameAndUserIdChk2(request.newName, userId, postCategoryId)) {
            throw CommonException(ErrorCode.CATEGORY_NAME_EXISTS)
        }

        // 2. name 변경
        postCategoryMapper.updateName(userId, postCategoryId, request.newName)
    }

    /**
     * 카테고리 순서 변경
     */
    @Transactional
    fun updateCategoryOrder(userId: String, request: PostCategoryOrderUpdateRequest) {
        // 1. 현재 Category 목록 조회
        val categoryIds = postCategoryMapper.selectAllIds(userId)

        // 2. 유효성 검사
        if (
            request.orderedIds.size != categoryIds.size
            || categoryIds.toSet() != request.orderedIds.toSet()
        ) {
            throw CommonException(ErrorCode.INVALID_CATEGORY_ORDER)
        }

        // 3. 요청 순서대로 sortOrder 수정
        request.orderedIds.forEachIndexed { index, categoryId ->
            val newSortOrder = index + 1
            postCategoryMapper.updateSortOrder(userId, categoryId, newSortOrder)
        }
    }

    /**
     * 카테고리 삭제
     */
    @Transactional
    fun deleteCategory(userId: String, postCategoryId: Long) {
        // 1. 유효성 검사
        if (postMapper.selectCategoryUsingChk(postCategoryId)) {
            throw CommonException(ErrorCode.CANNOT_DELETE_USING_CATEGORY)
        }

        // 2. 삭제할 카테고리보다 sortOrder가 큰 카테고리들의 sortOrder를 1씩 감소
        postCategoryMapper.updateSortOrderWhenDelete(userId, postCategoryId)

        // 3. PostCategory 삭제
        postCategoryMapper.delete(userId, postCategoryId)
    }
}