package com.moira.wimb.domain.post.controller

import com.moira.wimb.domain.post.dto.request.PostAddRequest
import com.moira.wimb.domain.post.service.PostService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PostController(
    private val postService: PostService
) {
    @PostMapping("/api/posts")
    fun addPost(@RequestBody request: PostAddRequest): ResponseEntity<Nothing> {
        val userId = "urElgaShb2SC"

        postService.add(userId, request)

        return ResponseEntity.ok(null)
    }
}