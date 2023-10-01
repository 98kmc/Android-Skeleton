package com.example.jetpack_compose_skeleton.modules.splash.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.jetpack_compose_skeleton.ui.theme.Purple40

@Composable
fun SplashScreenView() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Purple40),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Put Your Splash Screen Here! :)",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight(400),
                color = Color.White
            )
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun SplashScreenView_Preview() {

    SplashScreenView()
}