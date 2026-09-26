package com.moira.wimb.domain.user.service

import com.moira.wimb.domain.infra.entity.FileIdentifier
import com.moira.wimb.domain.infra.entity.FileStatus
import com.moira.wimb.domain.infra.entity.IdentificationPurpose
import com.moira.wimb.domain.infra.entity.IdentificationStatus
import com.moira.wimb.domain.infra.mapper.CommonFileMapper
import com.moira.wimb.domain.infra.mapper.IdentificationMapper
import com.moira.wimb.domain.user.dto.request.LoginRequest
import com.moira.wimb.domain.user.dto.request.SignupRequest
import com.moira.wimb.domain.user.dto.request.UserProfileImageUpdateRequest
import com.moira.wimb.domain.user.dto.response.SimpleUserResponse
import com.moira.wimb.domain.user.entity.User
import com.moira.wimb.domain.user.entity.UserStatus
import com.moira.wimb.domain.user.mapper.UserMapper
import com.moira.wimb.global.auth.SessionHandler
import com.moira.wimb.global.exception.CommonException
import com.moira.wimb.global.exception.ErrorCode
import com.moira.wimb.global.utility.CommonUtils
import com.moira.wimb.global.utility.CommonVariables.USER_ID_PREFIX
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    // mapper
    private val commonFileMapper: CommonFileMapper,
    private val identificationMapper: IdentificationMapper,
    private val userMapper: UserMapper,
    // utility
    private val encoder: PasswordEncoder,
    private val sessionHandler: SessionHandler
) {
    /**
     * 회원가입
     */
    @Transactional
    fun signup(request: SignupRequest) {
        // 1. 유효성 검사
        if (userMapper.selectLoginIdChk(request.loginId)) {
            throw CommonException(ErrorCode.LOGIN_ID_EXISTS)
        }
        if (userMapper.selectEmailChk(request.email)) {
            throw CommonException(ErrorCode.EMAIL_EXISTS)
        }
        val identification = identificationMapper.selectRecentIdentification(
            email = request.email,
            purpose = IdentificationPurpose.SIGNUP.name
        )
        if (identification == null || identification.status != IdentificationStatus.VERIFIED.name) {
            throw CommonException(ErrorCode.NO_IDENTIFICATION)
        }

        // 2. 비밀번호 암호화
        val encodedPassword = encoder.encode(request.password)
            ?: throw CommonException(ErrorCode.PASSWORD_ENCRYPTION_FAILED)

        // 3. User 저장
        val userId = CommonUtils.createRandomId(USER_ID_PREFIX)
        val user = User.create(userId, request, encodedPassword)
        userMapper.insertUser(user)

        // 4. Identification의 status를 USED로 변경
        identificationMapper.updateStatus(identification.seqNo, IdentificationStatus.USED.name)
    }

    /**
     * 로그인
     */
    @Transactional
    fun login(request: LoginRequest, httpReq: HttpServletRequest) {
        // 1. User 조회
        val user = userMapper.selectUserByLoginId(request.loginId)
            ?: throw CommonException(ErrorCode.LOGIN_FAILED)

        // 2. 유효성 검사
        if (!encoder.matches(request.password, user.password)) {
            throw CommonException(ErrorCode.LOGIN_FAILED)
        } else if (user.status == UserStatus.BANNED.name) {
            throw CommonException(ErrorCode.BANNED_USER_CANNOT_LOGIN)
        } else if (user.status == UserStatus.DELETED.name) {
            throw CommonException(ErrorCode.DELETED_USER_CANNOT_LOGIN)
        }

        // 3. 세션 세팅
        sessionHandler.setAuth(user, httpReq)
    }

    /**
     * 로그아웃
     */
    fun logout(httpReq: HttpServletRequest) {
        // 1. 세션 초기화
        sessionHandler.deleteAuth(httpReq)
    }

    /**
     * 내 정보 조회
     */
    @Transactional(readOnly = true)
    fun me(userId: String): SimpleUserResponse {
        // 1. SimpleUserResponse 조회
        return userMapper.selectSimpleUserResponse(userId) ?: throw CommonException(ErrorCode.USER_NOT_FOUND)
    }

    /**
     * 프로필 사진 수정
     */
    @Transactional
    fun updateProfileImage(userId: String, request: UserProfileImageUpdateRequest) {
        // 1. 업로드한 신규 CommonFile 조회
        val commonFile = commonFileMapper.selectCommonFile2(
            fileId = request.fileId,
            fileSeqNo = 1,
            userId = userId,
            identifier = FileIdentifier.USER_PROFILE_IMAGE.name,
            status = FileStatus.UPLOADED.name
        ) ?: throw CommonException(ErrorCode.COMMON_FILE_NOT_FOUND)

        // 2. 업로드한 신규 CommonFile 조회 후 status를 USING으로 변경
        commonFileMapper.updateStatusByFileId(commonFile.fileId, userId, FileStatus.USING.name)

        // 3. 기존 CommonFile 조회 후 삭제 처리 (soft-delete)
        val currentFileId = userMapper.selectFileId(userId)

        if (currentFileId != null) {
            commonFileMapper.updateStatusByFileId(currentFileId, userId, FileStatus.DELETED.name)
        }

        // 4. User의 fileId 수정
        userMapper.updateFileId(userId, commonFile.fileId)
    }
}