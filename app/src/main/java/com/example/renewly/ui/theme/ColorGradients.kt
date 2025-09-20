package com.example.renewly.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class GradientInfo(val hex: String, val brush: Brush)

object AppGradients {
    // Existing Gradients
    val Sunset = GradientInfo(
        hex = "#FF8A65_#FF5722",
        brush = Brush.horizontalGradient(listOf(Color(0xFFFF8A65), Color(0xFFFF5722)))
    )
    val Ocean = GradientInfo(
        hex = "#4FC3F7_#03A9F4",
        brush = Brush.horizontalGradient(listOf(Color(0xFF4FC3F7), Color(0xFF03A9F4)))
    )
    val Mint = GradientInfo(
        hex = "#80CBC4_#009688",
        brush = Brush.horizontalGradient(listOf(Color(0xFF80CBC4), Color(0xFF009688)))
    )
    val Grape = GradientInfo(
        hex = "#BA68C8_#9C27B0",
        brush = Brush.horizontalGradient(listOf(Color(0xFFBA68C8), Color(0xFF9C27B0)))
    )
    val Fire = GradientInfo(
        hex = "#FFB74D_#FF9800",
        brush = Brush.horizontalGradient(listOf(Color(0xFFFFB74D), Color(0xFFff9800)))
    )
    val Default = GradientInfo(
        hex = "#424242_#212121",
        brush = Brush.horizontalGradient(listOf(Color(0xFF424242), Color(0xFF212121)))
    )

    // --- NEW BASIC GRADIENTS ---
    val RubyRed = GradientInfo(
        hex = "#EF5350_#E53935",
        brush = Brush.horizontalGradient(listOf(Color(0xFFEF5350), Color(0xFFE53935)))
    )
    val ForestGreen = GradientInfo(
        hex = "#66BB6A_#43A047",
        brush = Brush.horizontalGradient(listOf(Color(0xFF66BB6A), Color(0xFF43A047)))
    )
    val RoyalBlue = GradientInfo(
        hex = "#42A5F5_#1E88E5",
        brush = Brush.horizontalGradient(listOf(Color(0xFF42A5F5), Color(0xFF1E88E5)))
    )
    val Gold = GradientInfo(
        hex = "#FFEE58_#FDD835",
        brush = Brush.horizontalGradient(listOf(Color(0xFFFFEE58), Color(0xFFFDD835)))
    )
    val Graphite = GradientInfo(
        hex = "#BDBDBD_#616161",
        brush = Brush.horizontalGradient(listOf(Color(0xFFBDBDBD), Color(0xFF616161)))
    )


    // Updated list that the color picker will use
    val predefinedGradients = listOf(
        Default,
        Graphite,
        RubyRed,
        ForestGreen,
        RoyalBlue,
        Sunset,
        Ocean,
        Mint,
        Grape,
        Fire,
        Gold
    )

    fun getBrushByHex(hex: String?): Brush {
        return predefinedGradients.find { it.hex == hex }?.brush ?: Default.brush
    }
}