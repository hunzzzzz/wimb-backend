package com.moira.wimb.domain.category.controller

import com.moira.wimb.domain.category.dto.request.PostCategoryAddRequest
import com.moira.wimb.domain.category.dto.request.PostCategoryNameUpdateRequest
import com.moira.wimb.domain.category.dto.request.PostCategoryOrderUpdateRequest
import com.moira.wimb.domain.category.dto.response.PostCategoryResponse
import com.moira.wimb.domain.category.service.PostCategoryService
import com.moira.wimb.global.auth.LoginUser
import com.moira.wimb.global.auth.ParameterAuthCheck
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class PostCategoryController(
    private val postCategoryService: PostCategoryService
) {
    /**
     * 카테고리 목록 조회
     */
    @GetMapping("/api/categories")
    fun getCategoryList(
        @LoginUser userId: String
    ): ResponseEntity<List<PostCategoryResponse>> {
        val list = postCategoryService.getCategoryList(userId)

        return ResponseEntity.status(HttpStatus.OK).body(list)
    }

    /**
     * 카테고리 추가
     */
    @PostMapping("/api/categories")
    fun addCategory(
        @LoginUser userId: String,
        @RequestBody request: PostCategoryAddRequest
    ): ResponseEntity<Nothing> {
        postCategoryService.addCategory(userId, request)

        return ResponseEntity.status(HttpStatus.CREATED).body(null)
    }

    /**
     * 카테고리 이름 수정
     */
    @ParameterAuthCheck
    @PatchMapping("/api/categories/{postCategoryId}/name")
    fun updateCategoryName(
        @LoginUser userId: String,
        @PathVariable postCategoryId: Long,
        @RequestBody request: PostCategoryNameUpdateRequest
    ): ResponseEntity<Nothing> {
        postCategoryService.updateCategoryName(userId, postCategoryId, request)

        return ResponseEntity.status(HttpStatus.OK).body(null)
    }

    /**
     * 카테고리 순서 변경
     */
    @PatchMapping("/api/categories/order")
    fun updateCategoryOrder(
        @LoginUser userId: String,
        @RequestBody request: PostCategoryOrderUpdateRequest
    ): ResponseEntity<Nothing> {
        postCategoryService.updateCategoryOrder(userId, request)

        return ResponseEntity.status(HttpStatus.OK).body(null)
    }

    /**
     * 카테고리 삭제
     */
    @ParameterAuthCheck
    @DeleteMapping("/api/categories/{postCategoryId}")
    fun deleteCategory(
        @LoginUser userId: String,
        @PathVariable postCategoryId: Long,
    ): ResponseEntity<Nothing> {
        postCategoryService.deleteCategory(userId, postCategoryId)

        return ResponseEntity.status(HttpStatus.OK).body(null)
    }
}