package com.aviro.android.presentation.review

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import com.aviro.android.R
import com.aviro.android.core.designsystem.CustomButton
import com.aviro.android.core.designsystem.Typographys
import com.aviro.android.presentation.aviro_dialog.AviroDialogUtils
import com.aviro.android.presentation.bottomsheet.ReviewViewModel
import com.aviro.android.presentation.home.ui.register.RegisterActivity
import java.security.Permission

class ReviewWriting : ComponentActivity() {


    private val viewmodel : ReviewWritingViewModel by viewModels() // 뷰모델


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface {
                BaseScreen()
            }
        }
    }


}



@Preview(showBackground = false)
@Composable
fun BasePreview() {
    Surface(
    ) {
        BaseScreen()
    }
}



@Composable
fun BaseScreen() {
    val context = LocalContext.current

    Column(modifier = Modifier.
    fillMaxSize()
        .padding(start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 툴바
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)

        )
        {
            Row(
                modifier = Modifier
                    .padding(top = 18.dp)
            ) {
                // 닫기 버튼
                IconButton(modifier = Modifier
                    .width(24.dp)
                    .height(24.dp),
                    onClick = { ((context as Activity).finish()) }) {

                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "뒤로가기"
                    )
                }

                // 제목
                Box() {
                    Text(
                        text = "러브얼스",
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = colorResource(R.color.Gray_800),
                        style = Typographys.BodyLarge.Strong
                    )
                }
            }

        }

        val scrollState = rememberScrollState()
        Column (modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)) {

            // 별점
            Box(modifier = Modifier
                .padding(top = 20.dp)
                )
            {
                ratingView()
            }

            // 사진 선택
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp))
            {
                reviewImage()
            }

            // 후기 작성
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp))
            {
                reviewScript()
            }

            // 등록 버튼
            Column (modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) { } // 버튼 하단 배치를 위한 조치
            Box {
                CustomButton.CustomBasicButton(
                    onClick = { ((context as Activity).finish()) } ,
                    status = CustomButton.ButtonStatus.Default,
                    style = CustomButton.ButtonType.Default,
                    size = CustomButton.ButtonSize.Default,
                    text = "후기 등록하기",
                    null,
                    null
                )
            }
    }
    }
}


@Composable
fun ratingView() {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,){
        // 문구
        Text(
            text = "만족하셨나요",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = colorResource(R.color.Base_Black),
            style = Typographys.Title.Subsection
        )



        // 별점
         Box(modifier = Modifier
              .fillMaxWidth()
              .padding(8.dp),
             contentAlignment = Alignment.Center
          ) {
             ratingStar()
          }

        // 문구
        Text(
            text = "최고예요",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = colorResource(R.color.Cobalt),
            style = Typographys.BodyLarge.Strong
        )
    }
}


@Composable
fun reviewImage() {

    val context = LocalContext.current

    // 갤러리 화면 이동 런처
    val galleryStartLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
        //// 넘어온 값이 RESULT_OK이면 getStringExtra로 값 가져오기
        if (result.resultCode == Activity.RESULT_OK) {
            // 선택한 사진 가져오기
            // val data = result.data
        }
    }

    // 갤러리 접근 권한 런처
    val galleryPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // 권한 허가 처리 로직
                galleryStartLauncher.launch(Intent(context, CustomGallery::class.java))
            }
            else {
                // 권한 거부 처리 로직
                // dialog 띄움
                AviroDialogUtils.createOneDialog(context,
                    "갤러리 접근 권한 요청",
                    "사진을 업로드 하기 위해서는\n갤러리 접근 권한 요청에 동의해주세요.",
                    "확인").show()
            }
        }


    fun checkGalleryPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES)
                == PackageManager.PERMISSION_GRANTED) {
                // 이미 권한이 허용된 경우
                galleryStartLauncher.launch(Intent(context, CustomGallery::class.java))
            } else {
                galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                // 이미 권한이 허용된 경우
                galleryStartLauncher.launch(Intent(context, CustomGallery::class.java))
            } else {
                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }


    var isImage = true

      Column()
      {
          Text(
              text = "사진/동영상 (선택)",
              color = colorResource(R.color.Base_Black),
              style = Typographys.Title.Group
          )

          Row(modifier = Modifier
              .padding(top = 10.dp)
              .fillMaxWidth()
          )  {

              Text(
                  text = "메뉴를 태그할 수 있어요.",
                  color = colorResource(R.color.Gray_600),
                  style = Typographys.BodyDefault.Regular
              )

              if(isImage) {
                  // 사지 추가 버튼
              }
          }



          val stroke = Stroke(width = 3f,
              pathEffect = PathEffect.dashPathEffect(floatArrayOf(33f, 30f), 2f)
          )
          val galleryBoxColor = colorResource(R.color.Blue_Primary)

          Row(modifier = Modifier
              .padding(top = 10.dp)
              .fillMaxWidth()
          ) {
              Box {
                  // 이미지 없음
                  if(isImage) {
                      IconButton(modifier = Modifier
                          .size(width = 120.dp, height = 120.dp)
                          .drawBehind {
                              drawRoundRect(
                                  color = galleryBoxColor,
                                  style = stroke,
                                  cornerRadius = CornerRadius(2.dp.toPx())
                              )
                          },
                          onClick = {
                              //checkGalleryPermission()
                              galleryStartLauncher.launch(Intent(context, CustomGallery::class.java))
                          }){
                          Icon(
                              modifier = Modifier
                                  .size(width = 48.dp, height = 48.dp),
                              painter = painterResource(id = R.drawable.ic_gallery),
                              contentDescription = "갤러리 버튼",
                              tint = colorResource(R.color.Blue_Primary),

                              )
                      }
                  } else {
                      // 이미지 1개라도 있을 때
                      /*
                      repeat(viewmodel._reviewImgs.value.size) {
                          // 이미지 셋팅
                          // 태그 있으면 태깅 표시 달기
                          // x 표시
                      }
                       */
                  }

              }


          }
      }



}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun reviewScript() {

       Box() {
           val hint = "후기를 자유롭게 작성해주세요.\n\n" +
                   "*다른 유저들은 맛, 가격, 분위기, 편의시설, 비건프렌들리함에 대한 후기를 궁금해 해요.\n\n" +
                   "*다른 유저 및 사장님들이 상처 받지 않도록 좋은 표현을 사용해주세요."
           CustomMultiLineTextField(hint,200,"")
       }

}


@Composable
fun ratingStar() {

    Row(modifier = Modifier
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center) {

        repeat(5) {
            Icon(modifier = Modifier
                .size(width = 30.dp, height = 30.dp),
                painter = painterResource(R.drawable.ic_like_bottomsheet_non),
                contentDescription = "")
        }

    }
}

@Composable
fun CustomSingleLineTextField(hint : String, maxCount : Int) {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomMultiLineTextField(hint : String, maxCount : Int, label : String) {

    val (reviewText, setReviewText) = remember { mutableStateOf("") }
    val currentCount = 10

    Column (
        modifier = Modifier
            .fillMaxWidth()
    ) {

        OutlinedTextField(modifier = Modifier
            .fillMaxWidth()
            .height(232.dp)
            .padding(top = 2.dp),
            value = reviewText ,
            onValueChange = setReviewText,
            textStyle = Typographys.BodyLarge.Regular,
            shape = RoundedCornerShape(4.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = Color.Black,
                focusedPlaceholderColor = colorResource(id = R.color.Base_White),
                unfocusedPlaceholderColor = colorResource(id = R.color.Base_White),
                errorPlaceholderColor = colorResource(id = R.color.Base_White),
                disabledPlaceholderColor = colorResource(id = R.color.Gray_100),

                unfocusedBorderColor = colorResource(id = R.color.Gray_200),
                focusedBorderColor = colorResource(id = R.color.Blue_Primary),
                disabledBorderColor = colorResource(id = R.color.Gray_200),
                errorBorderColor = colorResource(id = R.color.Red_Primary)
            ),
            placeholder = {
                Text(
                    text = hint,
                    color = colorResource(id = R.color.Gray_600),
                    style = Typographys.BodyLarge.Regular
                )
            }
        )

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp)
        ) {

            // 경고 시에만 뜨기 
            Text(text = "label",
                style = Typographys.BodySmall.Regular) // 에러메시지

            Text(
                text = "${reviewText.length}/$maxCount",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                color = if(currentCount >= maxCount) colorResource(R.color.Red_Primary) else colorResource(R.color.Base_Black),
                style = Typographys.BodySmall.Regular
            )

        }

    }

}





