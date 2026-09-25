package com.moira.wimb.domain.post.service

import com.moira.wimb.domain.post.dto.request.PostAddRequest
import com.moira.wimb.domain.post.entity.PostBase
import com.moira.wimb.domain.post.entity.PostSnippet
import com.moira.wimb.domain.post.entity.PostTag
import com.moira.wimb.domain.post.mapper.PostMapper
import com.moira.wimb.global.utility.CommonUtils
import com.moira.wimb.global.utility.CommonVariables.POST_ID_PREFIX
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostService(
    private val postMapper: PostMapper
) {
    /**
     * 게시글 저장
     */
    @Transactional
    fun add(userId: String, request: PostAddRequest) {
        // 1. 유효성 검사

        // 2. PostBase 저장
        val postId = CommonUtils.createRandomId(POST_ID_PREFIX)
        val postBase = PostBase.create(postId, userId, request)
        postMapper.insertPostBase(postBase)

        // 3. 각 유형별 게시글 저장
        if (request.snippet != null) {
            val postSnippet = PostSnippet.create(postId, request.snippet)
            postMapper.insertPostSnippet(postSnippet)
        }

        // 4. PostTag 저장
        if (request.tags != null) {
            val tags = mutableListOf<PostTag>()
            request.tags.forEach { tags.add(PostTag.create(postId, it)) }

            postMapper.insertPostTagBulk(tags)
        }
    }
}