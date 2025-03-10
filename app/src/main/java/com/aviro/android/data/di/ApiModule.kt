package com.aviro.android.data.di

import com.aviro.android.data.api.*
import com.aviro.android.data.utils.ResultCallAdapterFactory
import com.aviro.android.data.utils.AviroHeaderInterceptor
import com.aviro.android.data.utils.KakaoHeaderInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    private val AVIRO_BASE_URL_V2 = com.aviro.android.BuildConfig.AWS_API_KEY_V2
    private val AVIRO_BASE_URL_V3 = com.aviro.android.BuildConfig.AWS_API_KEY_V3
    private val KAKAO_BASE_URL = com.aviro.android.BuildConfig.KAKAO_RESTAPI_URL
    private val PUBLIC_BASE_URL = com.aviro.android.BuildConfig.PUBLIC_API_URL

    val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC  // 로그 레벨 BASIC
    }

    @Provides
    @Singleton
    fun providesResultCallAdapterFactory(): ResultCallAdapterFactory = ResultCallAdapterFactory()


    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AviroBaseClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AviroSupportClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class KakaoClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class PublicClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AviroBaseRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AviroSupportRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class KakaoRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class PublicRetrofit

    @Singleton
    @Provides
    @AviroBaseClient
    fun provideAviroBaseClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .addNetworkInterceptor(AviroHeaderInterceptor()) //aws, kakao 다르게 설정
            .build()
    }

    @Singleton
    @Provides
    @AviroSupportClient
    fun provideAviroSupportClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .addNetworkInterceptor(AviroHeaderInterceptor()) //aws, kakao 다르게 설정
            .build()
    }

    @Singleton
    @Provides
    @KakaoClient
    fun provideKakaoClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .addNetworkInterceptor(KakaoHeaderInterceptor()) //aws, kakao 다르게 설정
            .build()
    }

    @Singleton
    @Provides
    @PublicClient
    fun providePublicClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
        // .addNetworkInterceptor()
    }



    @Singleton
    @Provides
    @AviroBaseRetrofit
    fun provideAviroBaseRetrofit(@ApiModule.AviroBaseClient client : OkHttpClient, resultCallAdapterFactory: ResultCallAdapterFactory): Retrofit { //AWS
        return Retrofit.Builder()
            .client(client)
            .baseUrl(AVIRO_BASE_URL_V2)
            .addConverterFactory(GsonConverterFactory.create()) //gson
            .addCallAdapterFactory(resultCallAdapterFactory)
            .build()
    }

    @Singleton
    @Provides
    @AviroSupportRetrofit
    fun provideAviroSupportRetrofit(@ApiModule.AviroSupportClient client : OkHttpClient, resultCallAdapterFactory: ResultCallAdapterFactory): Retrofit { //AWS
        return Retrofit.Builder()
            .client(client)
            .baseUrl(AVIRO_BASE_URL_V3)
            .addConverterFactory(GsonConverterFactory.create()) //gson
            .addCallAdapterFactory(resultCallAdapterFactory)
            .build()
    }

    @Singleton
    @Provides
    @KakaoRetrofit
    fun provideKakaoRetrofit(@ApiModule.KakaoClient client : OkHttpClient, resultCallAdapterFactory: ResultCallAdapterFactory): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl(KAKAO_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) //gson
            .addCallAdapterFactory(resultCallAdapterFactory)
            .build()
    }

    @Singleton
    @Provides
    @PublicRetrofit
    fun providePublicRetrofit(@ApiModule.PublicClient client : OkHttpClient, resultCallAdapterFactory: ResultCallAdapterFactory): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl(PUBLIC_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) //gson
            .addCallAdapterFactory(resultCallAdapterFactory)
            .build()
    }



    // 싱글톤으로 auth api 인스턴스를 생성
    @Singleton
    @Provides
    @Named("AuthServiceBase")
    fun provideAuthServiceBase(@ApiModule.AviroBaseRetrofit aviroRetrofit: Retrofit): AuthService {
        return aviroRetrofit.create(AuthService::class.java)
    }

    @Singleton
    @Provides
    @Named("AuthServiceSupport")
    fun provideAuthServiceSupport(@ApiModule.AviroSupportRetrofit aviroRetrofit: Retrofit): AuthService {
        return aviroRetrofit.create(AuthService::class.java)
    }

    @Singleton
    @Provides
    fun provideMemberService(@ApiModule.AviroBaseRetrofit aviroRetrofit: Retrofit): MemberService {
        return aviroRetrofit.create(MemberService::class.java)
    }

    @Singleton
    @Provides
    @Named("ChallengeServiceBase")
    fun provideChallengeServiceBase(@ApiModule.AviroBaseRetrofit aviroRetrofit: Retrofit): ChallengeService {
        return aviroRetrofit.create(ChallengeService::class.java)
    }

    @Singleton
    @Provides
    @Named("ChallengeServiceSupport")
    fun provideChallengeServiceSupport(@ApiModule.AviroSupportRetrofit aviroRetrofit: Retrofit): ChallengeService {
        return aviroRetrofit.create(ChallengeService::class.java)
    }


    @Singleton
    @Provides
    fun provideRestaurantService(@ApiModule.AviroBaseRetrofit aviroRetrofit: Retrofit): RestaurantService {
        return aviroRetrofit.create(RestaurantService::class.java)
    }

    @Singleton
    @Provides
    fun provideKakaoService(@ApiModule.KakaoRetrofit kakaoRetrofit : Retrofit): KakaoService {
        return kakaoRetrofit.create(KakaoService::class.java)
    }

    @Singleton
    @Provides
    fun providePublicService(@ApiModule.PublicRetrofit publicRetrofit : Retrofit): PublicService {
        return publicRetrofit.create(PublicService::class.java)
    }

}