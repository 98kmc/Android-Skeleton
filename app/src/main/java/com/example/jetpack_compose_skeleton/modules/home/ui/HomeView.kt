package com.example.jetpack_compose_skeleton.modules.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeView() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "home_ic",
            tint = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Home, sweet Home... :)",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight(400),
                color = MaterialTheme.colorScheme.onBackground
            )
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun SplashScreenView_Preview() {

    HomeView()
}