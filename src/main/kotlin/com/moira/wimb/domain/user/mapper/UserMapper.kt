package com.moira.wimb.domain.user.mapper

import com.moira.wimb.domain.user.dto.response.SimpleUserResponse
import com.moira.wimb.domain.user.entity.User
import org.apache.ibatis.annotations.Mapper

@Mapper
interface UserMapper {
    fun selectLoginIdChk(loginId: String): Boolean
    fun selectEmailChk(email: String): Boolean

    fun selectSimpleUserResponse(userId: String): SimpleUserResponse?
    fun selectUserByLoginId(loginId: String): User?

    fun insertUser(user: User)
}