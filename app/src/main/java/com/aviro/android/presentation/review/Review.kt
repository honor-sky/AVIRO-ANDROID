package com.aviro.android.presentation.review

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aviro.android.presentation.aviro_dialog.AviroDialogUtils
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class Review : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val context = LocalContext.current

            Scaffold {
                val navController: NavHostController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = ReviewScreen.Script.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {

                    // 리뷰 작성 화면
                    composable(route = ReviewScreen.Script.name) { backStackEntry ->
                        val viewModel: ReviewWritingViewModel =
                            if (navController.previousBackStackEntry != null) {
                                hiltViewModel(navController.previousBackStackEntry!!)
                            } else {
                                hiltViewModel()
                            }

                        writingScreen(
                            context,
                            viewModel,
                            onBackButtonClicked = {
                                // 리뷰 작성 삭제 경고창
                                AviroDialogUtils.createTwoDialog(
                                    context,
                                    "정말로 후기 작성을 취소하나요?",
                                    "작성하던 후기가 모두 삭제됩니다.",
                                    "아니오",
                                    "예",
                                    { finish() }
                                ).show()
                            },
                            onNextButtonClicked = {
                                navController.navigate(ReviewScreen.Gallery.name)
                            }
                        )
                    }

                    // 앨범 접근 권한 체크 함수 넘겨주기
                    // 앨범, 사진 데이터 캐싱

                    // 사진 선택 화면
                    composable(route = ReviewScreen.Gallery.name) { entry ->
                        val viewModel: ReviewWritingViewModel =
                        if (navController.previousBackStackEntry != null) {
                            hiltViewModel(navController.previousBackStackEntry!!)
                        } else {
                            hiltViewModel()
                        }
                            galleryScreen(
                                context,
                                viewModel,
                                onBackButtonClicked = {
                                    // 이전 화면
                                    navController.popBackStack(ReviewScreen.Script.name, true)
                                },
                                onNextButtonClicked = {
                                    // viewModel.setQuantity(it) // 해당 화면에서 입력한 정보 viewmodel에 반영
                                    navController.navigate(ReviewScreen.Tag.name)
                                }
                            )
                    }


                    // 메뉴 태그 화면
                    composable(route = ReviewScreen.Tag.name) {
                        val viewModel: ReviewWritingViewModel =
                            if (navController.previousBackStackEntry != null) {
                                hiltViewModel(navController.previousBackStackEntry!!)
                            } else {
                                hiltViewModel()
                            }

                        menuTagScreen(
                            context,
                            viewModel,
                            onBackButtonClicked = {
                                // 이전 화면
                                navController.popBackStack(ReviewScreen.Gallery.name, true)
                            },
                            onNextButtonClicked = {
                                navController.navigate(ReviewScreen.Script.name) // 다시
                            }
                        )
                    }
                }
            }
        }

    }

}