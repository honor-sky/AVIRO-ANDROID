package com.aviro.android.data.datasource.auth

import com.aviro.android.data.api.AuthService
import com.aviro.android.data.model.auth.SignInRequest
import com.aviro.android.data.model.auth.SignInResponse
import com.aviro.android.data.model.auth.TokenResponse
import com.aviro.android.data.model.auth.TokensRequest
import com.aviro.android.data.model.auth.UserResponse
import com.aviro.android.data.model.base.DataResponse
import javax.inject.Inject
import javax.inject.Named


class AuthDataSourceImp @Inject constructor(
    @Named("AuthServiceBase") private val authServiceBase : AuthService,
    @Named("AuthServiceSupport") private val authServiceSupport : AuthService
) : AuthDataSource {

    // 서버로 refresh token 요청 (구글, 애플)
    override suspend fun getTokens(request: TokensRequest) : Result<DataResponse<TokenResponse>> {
        return authServiceBase.getTokens(request)
    }

    // 서버로 로그인 요청 (구글, 애플 자동 로그인)
    override suspend fun requestSignIn(request: SignInRequest) : Result<DataResponse<SignInResponse>> {
        return authServiceBase.sign(request)
    }

    // 회원여부와 닉네임 요청 (카카오, 네이버 로그인)
    override suspend fun getUser(userId: String) : Result<DataResponse<UserResponse>> {
        return authServiceSupport.getUser(userId) //authService2
    }

}