package com.aviro.android.presentation.review

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aviro.android.R
import com.aviro.android.core.designsystem.Typographys


@Composable
fun menuTagScreen(
    context : Context,
    viewmodel : ReviewWritingViewModel,
    onBackButtonClicked : () -> Unit,
    onNextButtonClicked : () -> Unit
) {

    // TopBar (제목, 완료 버튼)
    Column(modifier = Modifier
        .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // toolbar (뒤로가기, 앨범 선택, 선택완료)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(start = 16.dp, end = 16.dp)
        )
        {
            Row(
                modifier = Modifier
                    .padding(top = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    // 닫기 버튼
                    IconButton(modifier = Modifier
                        .width(24.dp)
                        .height(24.dp),
                        onClick = { onBackButtonClicked() }) {

                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_left),
                            contentDescription = "뒤로가기"
                        )
                    }
                }

                // 앨범 이름
                Box(
                    modifier = Modifier.weight(1f), // 가운데 박스
                    contentAlignment = Alignment.Center,
                ) { // 가운데 텍스트를 정렬

                    Row {
                        Text(
                            text = "메뉴 태그",
                            //textAlign = TextAlign.Center,
                            color = colorResource(R.color.Gray_800),
                            style = Typographys.BodyLarge.Strong
                        )

                    }
                }


                // 선택 완료 버튼
                Box( modifier = Modifier
                    .weight(1f)
                    .clickable {onNextButtonClicked()}, // 우측 박스
                    contentAlignment = Alignment.CenterEnd // 우측 정렬
                ) {
                    Row {
                        Text(
                            text = "다음",
                            color = colorResource(R.color.Blue_Primary),
                            style = Typographys.BodyLarge.Strong
                        )
                    }
                }
            }
        }

        val selectedPhotos = viewmodel.selectedPhotoList.collectAsState()

        /* 이미지 스크린 공간 */
        // 사진 개수
        Column {
            Box( modifier = Modifier
                .weight(1f),
                contentAlignment = Alignment.Center // 중앙 정렬
            ) {
                Row {
                    Text(
                        text = "1",
                        color = colorResource(R.color.Base_Black),
                        style = Typographys.BodyLarge.Strong
                    )

                    Text(
                        text = "/${selectedPhotos.value.size}",
                        color = colorResource(R.color.Base_Black),
                        style = Typographys.BodyLarge.Strong
                    )
                }
            }
        }
        // 사진
        Box( modifier = Modifier
            .padding(top = 10.dp)
            .size(375.dp, 375.dp)
        ) {
            // 본래 사진에 맞춰서 표시 (viewpager로 구현)

            // 버튼
        }


        /* 태그 공간 */
        Column(modifier = Modifier
            .padding(top = 10.dp)
        ) {

        }
            Box( modifier = Modifier
                .padding(top = 20.dp)
            ) {
                Text(
                    text = "사진의 메뉴를 태그해주세요.",
                    color = colorResource(R.color.Base_Black),
                    style = Typographys.Title.Body
                )
            }

        Box( modifier = Modifier
            .padding(top = 12.dp, start = 12.dp, end = 12.dp)
        ) {
            // 가로 길이 계산

            // 메뉴명으로 태그 만들기

            // 마지막에 추가하기 버튼 넣기
        }



        }


}



/* 태그 생성 */
@Composable
fun createTag() {

}




