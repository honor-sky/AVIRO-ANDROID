package com.aviro.android.core.designsystem

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.aviro.android.R


object Typographys {


    private val pretendardStyle = FontFamily(
        Font(R.font.pretendard_thin, weight = FontWeight.Thin),
        Font(R.font.pretendard_light, weight = FontWeight.Light),
        Font(R.font.pretendard_medium, weight = FontWeight.Medium),
        Font(R.font.pretendard_regular, weight = FontWeight.Normal),
        Font(R.font.pretendard_semibold, weight = FontWeight.SemiBold),
        Font(R.font.pretendard_bold, weight = FontWeight.Bold)
    )


    object Title {

        val Screen = TextStyle(
            fontFamily = pretendardStyle,
            fontWeight = FontWeight.SemiBold,
            fontSize = Dimens.FontSize.xxl,
            lineHeight = Dimens.LineHeight.xxl,
            letterSpacing = (-2).sp
        )

        val Section = TextStyle(
            fontFamily = pretendardStyle,
            fontWeight = FontWeight.SemiBold,
            fontSize = Dimens.FontSize.xl,
            lineHeight = Dimens.LineHeight.xl,
            letterSpacing = (-1.5).sp
        )

        val Subsection = TextStyle(
            fontFamily = pretendardStyle,
            fontWeight = FontWeight.SemiBold,
            fontSize = Dimens.FontSize.lg,
            lineHeight = Dimens.LineHeight.lg,
            letterSpacing = (-1).sp
        )

        val Body = TextStyle(
            fontFamily = pretendardStyle,
            fontWeight = FontWeight.SemiBold,
            fontSize = Dimens.FontSize.md,
            lineHeight = Dimens.LineHeight.md,
            letterSpacing = (-0.5).sp
        )

        val Group = TextStyle(
            fontFamily = pretendardStyle,
            fontWeight = FontWeight.SemiBold,
            fontSize = Dimens.FontSize.sm,
            lineHeight = Dimens.LineHeight.sm,
            letterSpacing = Dimens.Spacing.none
        )


    }

    object BodyLarge {

        val Strong = TextStyle(
            fontFamily = pretendardStyle,
            fontWeight = FontWeight.SemiBold,
            fontSize = Dimens.FontSize.sm,
            lineHeight = Dimens.LineHeight.sm,
            letterSpacing = Dimens.Spacing.none

        )

        val Regular = TextStyle(
            fontFamily = pretendardStyle,
            fontWeight = FontWeight.Normal,
            fontSize = Dimens.FontSize.sm,
            lineHeight = Dimens.LineHeight.sm,
            letterSpacing = Dimens.Spacing.none
        )

        val Emphasis = TextStyle(
            fontFamily = pretendardStyle,
            fontWeight = FontWeight.Medium,
            fontSize = Dimens.FontSize.sm,
            lineHeight = Dimens.LineHeight.sm,
            letterSpacing = Dimens.Spacing.none
        )

        val Link = TextStyle(
            fontFamily = pretendardStyle,
            fontWeight = FontWeight.SemiBold,
            fontSize = Dimens.FontSize.sm,
            lineHeight = Dimens.LineHeight.sm,
            letterSpacing = Dimens.Spacing.none
        )

    }

    object BodyDefault {

        val Strong = TextStyle(
            fontFamily = pretendardStyle,
            fontWeight = FontWeight.SemiBold,
            fontSize = Dimens.FontSize.xs,
            lineHeight = Dimens.LineHeight.xs,
            letterSpacing = Dimens.Spacing.none

        )

        val Regular = TextStyle(
            fontFamily = pretendardStyle,
            fontWeight = FontWeight.Normal,
            fontSize = Dimens.FontSize.xs,
            lineHeight = Dimens.LineHeight.xs,
            letterSpacing = Dimens.Spacing.none
        )

        val Emphasis = TextStyle(
            fontFamily = pretendardStyle,
            fontWeight = FontWeight.Medium,
            fontSize = Dimens.FontSize.xs,
            lineHeight = Dimens.LineHeight.xs,
            letterSpacing = Dimens.Spacing.none
        )

        val Link = TextStyle(
            fontFamily = pretendardStyle,
            fontWeight = FontWeight.SemiBold,
            fontSize = Dimens.FontSize.xs,
            lineHeight = Dimens.LineHeight.xs,
            letterSpacing = Dimens.Spacing.none
        )


    }


    object BodySmall {

        val Strong = TextStyle(
            fontFamily = pretendardStyle,
            fontWeight = FontWeight.SemiBold,
            fontSize = Dimens.FontSize.xxs,
            lineHeight = Dimens.LineHeight.xxs,
            letterSpacing = Dimens.Spacing.none

        )

        val Regular = TextStyle(
            fontFamily = pretendardStyle,
            fontWeight = FontWeight.Normal,
            fontSize = Dimens.FontSize.xxs,
            lineHeight = Dimens.LineHeight.xxs,
            letterSpacing = Dimens.Spacing.none
        )

        val Emphasis = TextStyle(
            fontFamily = pretendardStyle,
            fontWeight = FontWeight.Medium,
            fontSize = Dimens.FontSize.xxs,
            lineHeight = Dimens.LineHeight.xxs,
            letterSpacing = Dimens.Spacing.none
        )

        val Link = TextStyle(
            fontFamily = pretendardStyle,
            fontWeight = FontWeight.SemiBold,
            fontSize = Dimens.FontSize.xxs,
            lineHeight = Dimens.LineHeight.xxs,
            letterSpacing = Dimens.Spacing.none
        )

    }

}

