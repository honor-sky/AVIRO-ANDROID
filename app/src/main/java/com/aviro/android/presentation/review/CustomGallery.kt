package com.aviro.android.presentation.review

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aviro.android.R
import com.aviro.android.core.designsystem.Typographys

class CustomGallery : ComponentActivity() {

    // 권한

    // 뷰모델
    private val viewmodel : ReviewWritingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface {
                galleryScreen()
            }
        }

    }
}


@Preview(showBackground = false)
@Composable
fun galleryScreenPreview() {
    Surface(
    ) {
        galleryScreen()
    }
}



@Composable
fun galleryScreen() {
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
                Box( modifier = Modifier.weight(1f) ){
                    // 닫기 버튼
                    IconButton(modifier = Modifier
                        .width(24.dp)
                        .height(24.dp),
                        onClick = { /* ((context as Activity).finish()) */ }) {

                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_left),
                            contentDescription = "뒤로가기"
                        )
                    }
                }

                // 앨범 이름
                Box( modifier = Modifier.weight(1f), // 가운데 박스
                    contentAlignment = Alignment.Center) { // 가운데 텍스트를 정렬

                    Row {

                        Text(
                            text = "전체",
                            //textAlign = TextAlign.Center,
                            color = colorResource(R.color.Gray_800),
                            style = Typographys.BodyLarge.Strong
                        )

                        Icon(modifier = Modifier
                            .width(20.dp)
                            .height(20.dp)
                            .padding(start = 4.dp),
                            painter = painterResource(id = R.drawable.ic_arrow_down),
                            contentDescription = "앨범 선택하기"
                        )
                    }

                }

                // 선택 완료 버튼
                Box( modifier = Modifier.weight(1f), // 우측 박스
                    contentAlignment = Alignment.CenterEnd // 우측 정렬
                ) {
                    Row {
                        Text(
                            text = "3",
                            modifier = Modifier
                                .padding(end = 3.dp),
                            color = colorResource(R.color.Blue_Primary),
                            style = Typographys.BodyLarge.Strong
                        )

                        Text(
                            text = "선택",
                            color = colorResource(R.color.Base_Black),
                            style = Typographys.BodyLarge.Strong
                        )
                    }
                }
            }
        }

        // 이미지 grid
        // 이미지 클릭시 선택된 번호 -> 뷰모델로 이동
        val items = (1..50).toList() // 예제 데이터를 위한 리스트

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 20.dp),
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),

        ) {

            getImagesFromInternal() // 이미지 가져오기 // 어떤 앨범인지에 따라 다름
            // 이미지 표시
            // 각 이미지 아이템 클릭시, 동작 구현
            // 이미지 리스트에 uri, 순서 저장
            // 이미지에 순서 표시 뜸




            items(items.size) { item ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f) // 정사각형 모양 유지
                        .background(Color.LightGray)
                ) {
                    Card (
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(R.color.Gray_600)
                        )
                    ) {

                    }
                }
            }
        }
    }


}

fun getImagesFromInternal() {
    // 이미지 가져오기
    // galleryScreen에 셋팅 // Paging, 캐싱 필수

}


fun checkPermission() {

}

