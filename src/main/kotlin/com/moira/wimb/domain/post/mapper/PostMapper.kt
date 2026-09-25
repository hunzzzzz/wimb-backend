package com.moira.wimb.domain.post.mapper

import com.moira.wimb.domain.post.entity.PostBase
import com.moira.wimb.domain.post.entity.PostSnippet
import com.moira.wimb.domain.post.entity.PostTag
import org.apache.ibatis.annotations.Mapper

@Mapper
interface PostMapper {
    fun insertPostBase(postBase: PostBase)
    fun insertPostSnippet(postSnippet: PostSnippet)
    fun insertPostTagBulk(tags: List<PostTag>)
}