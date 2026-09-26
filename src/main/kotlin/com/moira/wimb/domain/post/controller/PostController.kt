package com.moira.wimb.domain.post.controller

import com.moira.wimb.domain.post.dto.request.PostAddRequest
import com.moira.wimb.domain.post.dto.response.PostListResponse
import com.moira.wimb.domain.post.entity.PostSearchType
import com.moira.wimb.domain.post.service.PostService
import com.moira.wimb.global.auth.LoginUser
import com.moira.wimb.global.auth.ParameterAuthCheck
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class PostController(
    private val postService: PostService
) {
    /**
     * 게시글 목록 조회 (좌측 메뉴바)
     */
    @GetMapping("/api/posts")
    fun getPostList(
        @LoginUser userId: String,
        @RequestParam searchType: String?
    ): ResponseEntity<List<PostListResponse>> {
        val response = postService.getAll(userId, searchType ?: PostSearchType.CATEGORY.name)

        return ResponseEntity.ok(response)
    }

    /**
     * 게시글 등록
     */
    @PostMapping("/api/posts")
    fun addPost(
        @LoginUser userId: String,
        @RequestBody request: PostAddRequest
    ): ResponseEntity<Nothing> {
        postService.add(userId, request)

        return ResponseEntity.ok(null)
    }

    /**
     * 게시글 삭제
     */
    @ParameterAuthCheck
    @DeleteMapping("/api/posts/{postId}")
    fun deletePost(
        @LoginUser userId: String,
        @PathVariable postId: String
    ): ResponseEntity<Nothing> {
        postService.delete(postId)

        return ResponseEntity.ok(null)
    }
}