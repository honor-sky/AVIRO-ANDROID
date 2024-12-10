package com.aviro.android.core.designsystem

import android.widget.Button
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.aviro.android.R

object CustomButton {


    data class ButtonColors(
        val backgroundColor: Int,
        val textColor: Int,
        val borderColor: Int
    )

    data class ButtonDimensions(
        val modifier: Modifier,
        val fontStyle: TextStyle,
    )

    enum class ButtonStatus {
        Default,
        Clicked,
        Focused,
        Disabled
    }

    enum class ButtonType {
        Default,
        Secondary,
        Destructive,
        Tertiary,
        Link,
        LinkDestructive
    }

    enum class ButtonSize {
        Small,
        Default,
        Large
    }

    enum class IconGravity {
        Start, End
    }



    @Composable
    fun CustomBasicButton(
        onClick: () -> Unit,
        status: ButtonStatus,
        style: ButtonType,
        size: ButtonSize,
        text: String,
        icon : Int?,
        iconGravity : IconGravity?
    ) {
        // 상태, 스타일, 사이즈에 맞는 값들 가져오기
        val colors = getButtonColors(status, style)
        val dimensions = getButtonDimensions(size)

        return Button(
            onClick = onClick,
            modifier = dimensions.modifier,
            border = BorderStroke(1.dp, colorResource(colors.borderColor)),
            shape = RoundedCornerShape(36.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = colorResource(colors.textColor),
                containerColor = colorResource(colors.backgroundColor)
            ),
            enabled = status != ButtonStatus.Disabled
        ) {

            if (icon != null && iconGravity != null) {
                when (iconGravity) {
                    IconGravity.Start -> {
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = "",  // 접근성 포커스 없애기
                            modifier = Modifier
                                .width(36.dp)
                                .height(36.dp)
                                .padding(end = 8.dp,start = 12.dp)
                                .align(Alignment.CenterVertically)
                        )

                        Text(
                            text = text,
                            style = dimensions.fontStyle,
                        )
                    }

                    IconGravity.End -> {

                        Text(
                            text = text,
                            style = dimensions.fontStyle,

                        )

                        Icon(
                            painter = painterResource(icon),
                            contentDescription = "", // 접근성 포커스 없애기
                            modifier = Modifier
                                .width(36.dp)
                                .height(36.dp)
                                .padding(end = 12.dp, start = 8.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }

            } else {
                Text(
                    text = text,
                    style = dimensions.fontStyle
                )
            }
        }
    }


    @Composable
    fun CustomTextButton(
        onClick: () -> Unit,
        status: ButtonStatus,
        style: ButtonType,
        size: ButtonSize,
        text: String,
        icon : Int?,
        iconGravity : IconGravity?
    ) {
        // 상태, 스타일, 사이즈에 맞는 값들 가져오기
        val colors = getButtonColors(status, style)
        val dimensions = getButtonDimensions(size)

        // 텍스트 버튼인 경우
        TextButton(
            onClick = { onClick() }
        ) {
            if(icon != null && iconGravity != null) {
                when(iconGravity) {
                    IconGravity.Start -> {
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = "",
                            modifier = Modifier
                                .width(20.dp)
                                .height(20.dp)
                                .padding(end = 4.dp, start = 12.dp)
                                .align(Alignment.CenterVertically)
                        )
                        Text(
                            text = text,
                            style = dimensions.fontStyle
                        )
                    }
                    IconGravity.End -> {

                        Text(
                            text = text,
                            style = dimensions.fontStyle
                        )

                        Icon(
                            painter = painterResource(icon),
                            contentDescription = "",
                            modifier = Modifier
                                .width(20.dp)
                                .height(20.dp)
                                .padding(end = 12.dp, start = 4.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }

            }
        }
    }



    fun getButtonDimensions(size: ButtonSize): ButtonDimensions {
        return when (size) {
            ButtonSize.Small -> ButtonDimensions(
                modifier = Modifier
                    .padding(top = 6.dp, bottom = 6.dp)
                    .height(32.dp)
                    .fillMaxWidth(),
                fontStyle = Typographys.BodyDefault.Strong
            )
            ButtonSize.Default -> ButtonDimensions(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 10.dp)
                    .height(44.dp)
                    .fillMaxWidth(),
                fontStyle = Typographys.BodyDefault.Strong
            )
            ButtonSize.Large -> ButtonDimensions(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 12.dp)
                    .height(52.dp)
                    .fillMaxWidth(),
                fontStyle = Typographys.BodyDefault.Strong
            )
        }
    }

    fun getButtonColors(status: ButtonStatus, style: ButtonType): ButtonColors {
        return when (status) {
            ButtonStatus.Default -> when (style) {
                ButtonType.Default -> ButtonColors(R.color.background_button_primary_normal, R.color.text_white, R.color.background_button_primary_normal)
                ButtonType.Secondary -> ButtonColors(R.color.background_button_secondary_normal, R.color.text_body, R.color.border_button_secondary_normal)
                ButtonType.Destructive -> ButtonColors(R.color.background_button_destructive, R.color.text_destructive, R.color.border_button_outline_destructive)

                ButtonType.Tertiary -> TODO()
                ButtonType.Link -> TODO()
                ButtonType.LinkDestructive -> TODO()
            }
            ButtonStatus.Clicked -> when (style) {
                ButtonType.Default -> ButtonColors(R.color.background_button_primary_active, R.color.text_white, R.color.background_button_primary_active)
                ButtonType.Secondary -> ButtonColors(R.color.background_button_secondary_active, R.color.text_primary, R.color.border_button_secondary_active)
                ButtonType.Destructive -> ButtonColors(R.color.background_button_destructive_active, R.color.text_destructive_active, R.color.border_button_outline_destructive_active)

                ButtonType.Tertiary -> TODO()
                ButtonType.Link -> TODO()
                ButtonType.LinkDestructive -> TODO()
            }

            // 포커스용 외부 border 추가
            ButtonStatus.Focused -> when (style) {
                ButtonType.Default -> ButtonColors(R.color.background_button_primary_normal, R.color.text_white, R.color.background_button_primary_normal)
                ButtonType.Secondary -> ButtonColors(R.color.background_button_secondary_normal, R.color.text_body, R.color.border_button_secondary_normal)
                ButtonType.Destructive -> ButtonColors(R.color.Base_White, R.color.text_destructive, R.color.border_button_outline_destructive)

                ButtonType.Tertiary -> TODO()
                ButtonType.Link -> TODO()
                ButtonType.LinkDestructive -> TODO()
            }
            ButtonStatus.Disabled -> when (style) {
                ButtonType.Default -> ButtonColors(R.color.background_button_primary_disabled, R.color.text_disabled, R.color.background_button_primary_disabled)
                ButtonType.Secondary -> ButtonColors(R.color.background_button_secondary_disabled, R.color.text_body, R.color.border_button_secondary_disabled)
                ButtonType.Destructive -> ButtonColors(R.color.background_button_destructive_disabled, R.color.text_destructive_disabled, R.color.border_button_outline_destructive_disabled)

                ButtonType.Tertiary -> TODO()
                ButtonType.Link -> TODO()
                ButtonType.LinkDestructive -> TODO()
            }
        }
    }



}



// Default, clicked, disable, focued 별로 사이즈, 컬러
// 사이즈 관련 modifier
// 컬러 관련 테마