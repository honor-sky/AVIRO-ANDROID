package com.aviro.android.presentation.review

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aviro.android.R
import com.aviro.android.common.ImageUtils
import com.aviro.android.core.designsystem.Typographys
import com.aviro.android.presentation.entity.GalleryPhotoItem
import java.io.File

    private fun moveToAppSetting(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
    }

    fun fetchAlbums(context: Context) : MutableList<String> {
        // 갤러리 내 앨범 항목 가져오기
        val galleryAlbumList = mutableListOf<String>()

        // 이미지 파일에서 가져올 정보들
        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        val queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor? = context.contentResolver.query(
            queryUri,
            projection,
            null,
            null,
            MediaStore.Images.ImageColumns.DATE_ADDED + " DESC"
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val bucketColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                val albumName = cursor.getString(bucketColumn)

                albumName.let {
                    galleryAlbumList.add(albumName)
                }

            } while(cursor.moveToNext())
        }

        return galleryAlbumList

    }

    // 사진 저장소에서 사진 가져오기
    @SuppressLint("Range")
    fun fetchImages(albumName : String, context : Context) : MutableList<GalleryPhotoItem> {
        // 이미지 가져오기 // Paging, 캐싱 필수
        val galleryPhotoList = mutableListOf<GalleryPhotoItem>()

        // 이미지 파일에서 가져올 정보들
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.DATA, // 파일 경로
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Images.ImageColumns.SIZE, // 크기
            MediaStore.Images.ImageColumns.DATE_ADDED, // 추가된 날짜
            MediaStore.Images.ImageColumns._ID // 고유 ID
        )

        var selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(albumName)
        val queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        // 사진만
    /*    selection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Images.Media.SIZE + " > 0"
            else null*/


        val cursor: Cursor? = context.contentResolver.query(
            queryUri,
            projection,
            selection,
            selectionArgs,
            MediaStore.Images.ImageColumns.DATE_ADDED + " DESC" // 최신순
        )

        // 처음부터 사진 없는 경우
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val mediaPath =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
                val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))

                // 리스트에 저장
                galleryPhotoList.add(
                    GalleryPhotoItem(
                        id,
                        Uri.fromFile( File(mediaPath)).toString(),
                        false
                    )
                )

            } while (cursor.moveToNext())
        }

        return galleryPhotoList
    }

//}




/*
onComplete : (List<GalleryPhotoItem>) -> Unit, // 이미지 선택 완료
onBack : () -> Unit, // 뒤로가기

imageList : List<GalleryPhotoItem>, // 이미지 리스트
albumList : List<String>, // 앨범 리스트

currentAlbum : String,  // 현재 앨범 이름
onShowAlbum : @Composable () -> Unit, // 앨범 리스트 열기
onDismisssAlbum : () -> Unit, // 앨범 리스트 닫기
onSelectAlbum : (String) -> Unit, // 앨범 선택
onSelectPhoto : (GalleryPhotoItem) -> Unit // 사진 하나 선택 // 뷰모델에서 처리 // 해당 이미지 선택 여부 변경, 선택됨 리스트에 추가 및 삭제
*/


@Composable
fun galleryScreen(
    context : Context,
    viewmodel : ReviewWritingViewModel,
    onBackButtonClicked : () -> Unit,
    onNextButtonClicked : () -> Unit,

   ) {

    val albumList = fetchAlbums(context) // 앨범 리스트 반환
    Log.d("albumList","$albumList")
    val imageList = fetchImages(albumList[0], context) // 이미지 리스트 반환


    val selectedPhotos by viewmodel.selectedPhotoList.collectAsState()

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
                            text = "최근항목",
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
                            text = "${selectedPhotos.size}",
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
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 20.dp),
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = {

                items(imageList.size) { item ->
                    Card(shape = RectangleShape){
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f) // 정사각형 모양 유지
                                .clickable {
                                    viewmodel.toggleSelectedPhoto(imageList[item])
                                    //onSelectPhoto(imageList[item])
                                }
                                .then(
                                    if (selectedPhotos.contains(imageList[item])) {
                                        Modifier.border(4.dp, color = colorResource(R.color.Blue_Primary), shape = RectangleShape)
                                    } else {
                                        Modifier // 선택되지 않은 경우 빈 Modifier
                                    }
                                )
                        ) {

                            val bitmap = ImageUtils.getImageFromCache(imageList[item].uri)?.let {
                                it
                            } ?: run {
                                ImageUtils.internalImageDownload(imageList[item].uri, context.contentResolver)
                            }

                            Image(
                                bitmap = bitmap!!.asImageBitmap(),
                                contentDescription = "갤러리 이미지 $item",
                                contentScale = ContentScale.Crop
                            )

                            // 선택된 사진이면 동그라미 + 숫자
                            if(selectedPhotos.contains(imageList[item])) {
                                Box(modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 2.dp, end = 2.dp)) {
                                    SelectedPhotoButton(selectedPhotos, imageList[item])
                                }

                            }
                        }
                    }

                }
            }

        )
    }

}


@Composable
fun SelectedPhotoButton(selectedPhotos : List<GalleryPhotoItem>, item : GalleryPhotoItem) {
 Box(modifier = Modifier
     .padding(end = 8.dp, top = 8.dp) // 이 코드가 선언된 위치도 중요 (size 밑에 선언되면 동그라미가 작아짐)
     .size(24.dp)
     .background(color = colorResource(R.color.Blue_Primary), shape = CircleShape),
     contentAlignment = Alignment.Center
 ) {
     Text(
         text = (selectedPhotos.indexOf(item) + 1).toString(),
         color = Color.White
     )
 }
}













