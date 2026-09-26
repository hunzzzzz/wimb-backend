package com.moira.wimb.domain.post.service

import com.moira.wimb.domain.post.dto.request.PostAddRequest
import com.moira.wimb.domain.post.dto.response.PostListResponse
import com.moira.wimb.domain.post.entity.*
import com.moira.wimb.domain.post.mapper.PostMapper
import com.moira.wimb.global.exception.CommonException
import com.moira.wimb.global.exception.ErrorCode
import com.moira.wimb.global.utility.CommonUtils
import com.moira.wimb.global.utility.CommonVariables.POST_ID_PREFIX
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostService(
    private val postMapper: PostMapper
) {
    /**
     * 게시글 목록 조회
     */
    @Transactional(readOnly = true)
    fun getAll(userId: String, searchType: String): List<PostListResponse> {
        // 1. 유효성 검사
        if (!CommonUtils.isValidEnum<PostSearchType>(searchType)) {
            throw CommonException(ErrorCode.INVALID_POST_SEARCH_TYPE)
        }

        // 2. 조회
        return postMapper.selectAll(userId, searchType)
    }

    /**
     * 게시글 저장
     */
    @Transactional
    fun add(userId: String, request: PostAddRequest) {
        // 1. 유효성 검사
        if (!CommonUtils.isValidEnum<PostType>(request.type)) {
            throw CommonException(ErrorCode.INVALID_POST_TYPE)
        }
        if (!CommonUtils.isValidEnum<PostVisibility>(request.visibility)) {
            throw CommonException(ErrorCode.INVALID_POST_VISIBLITY)
        }

        // 2. PostBase 저장
        val postId = CommonUtils.createRandomId(POST_ID_PREFIX)
        val postBase = PostBase.create(postId, userId, request)
        postMapper.insertPostBase(postBase)

        // 3. 각 유형별 게시글 저장
        if (request.snippet != null) {
            val postSnippet = PostSnippet.create(postId, request.snippet)
            postMapper.insertPostSnippet(postSnippet)
        }
        if (request.normal != null) {
            val postNormal = PostNormal.create(postId, request.normal)
            postMapper.insertPostNormal(postNormal)
        }

        // 4. PostTag 저장
        if (request.tags != null) {
            val tags = mutableListOf<PostTag>()
            request.tags.forEach { tags.add(PostTag.create(postId, it)) }

            postMapper.insertPostTagBulk(tags)
        }
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    fun delete(postId: String) {
        // 1. PostBase의 status를 DELETED로 변경
        postMapper.updatePostStatusDeleted(postId)
    }
}