package com.autolearn.ar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AccBg = Color(0xFF0B0F19)
private val AccCardBg = Color(0xFF161E2E)
private val AccBorder = Color(0xFF26334D)
private val AccCyan = Color(0xFF38BDF8)
private val AccGreen = Color(0xFF34D399)
private val AccTextPrimary = Color(0xFFF8FAFC)
private val AccTextSecondary = Color(0xFF94A3B8)

@Composable
fun AccountScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AccBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "USER PROFILE",
            color = AccCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.5.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Avatar Badge
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(AccCyan.copy(alpha = 0.15f))
                .border(1.5.dp, AccCyan.copy(alpha = 0.5f), CircleShape)
        ) {
            Text(
                text = "A",
                color = AccCyan,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Automotive Trainee",
            color = AccTextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "AutoLearn AR Practical Student",
            color = AccTextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        AccountInfoCard(
            title = "Learning Progress",
            description = "Track your practical training performance and AI verification accuracy.",
            accentColor = AccGreen
        )

        Spacer(modifier = Modifier.height(10.dp))

        AccountInfoCard(
            title = "Achievements & Badges",
            description = "Complete practical component sessions to unlock achievements.",
            accentColor = AccCyan
        )

        Spacer(modifier = Modifier.height(10.dp))

        AccountInfoCard(
            title = "AR System Preferences",
            description = "Manage camera sensitivity, AI confidence threshold, and voice guidance settings.",
            accentColor = AccTextSecondary
        )
    }
}

@Composable
private fun AccountInfoCard(
    title: String,
    description: String,
    accentColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AccBorder, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AccCardBg)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                color = accentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                color = AccTextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}