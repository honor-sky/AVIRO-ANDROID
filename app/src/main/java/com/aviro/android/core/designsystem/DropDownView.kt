package com.aviro.android.core.designsystem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

object DropDownView {

    @Composable
    fun BasicDropdown (
        albumMenuExpanded : Boolean,
        itemList : List<String>, // 목록 리스트
        onClickItem : (Int) -> Unit // 목록 클릭시 동작
    ) {
        var expanded by remember { mutableStateOf(albumMenuExpanded) }
        val scrollState = rememberScrollState()

        return Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false}
            ) {
                repeat(itemList.size) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "${itemList[it]}"
                            )
                        },
                        onClick = { onClickItem }
                    )
                }

            }

         /*   LaunchedEffect(expanded) {
                if (expanded) {
                    // Scroll to show the bottom menu items.
                    scrollState.scrollTo(scrollState.maxValue)
                }
            }
        }*/
    }
    }
}