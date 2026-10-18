package com.autolearn.ar.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autolearn.ar.data.altoComponents
import com.autolearn.ar.data.theoryContentMap

private val LibBg = Color(0xFF0B0F19)
private val LibCardBg = Color(0xFF161E2E)
private val LibBorder = Color(0xFF26334D)
private val LibCyan = Color(0xFF38BDF8)
private val LibGreen = Color(0xFF34D399)
private val LibAmber = Color(0xFFFBBF24)
private val LibTextPrimary = Color(0xFFF8FAFC)
private val LibTextSecondary = Color(0xFF94A3B8)

@Composable
fun LibraryScreen(
    onComponentClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LibBg)
            .padding(horizontal = 22.dp, vertical = 28.dp)
    ) {
        Text(
            text = "COMPONENT LIBRARY",
            color = LibCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.5.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Theory & Practical Modules",
            color = LibTextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Tap a component to study its theory, safety guidelines, and AI-assisted learning.",
            color = LibTextSecondary,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Summary pills
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Total components
            Box(
                modifier = Modifier
                    .background(LibCyan.copy(alpha = 0.10f), RoundedCornerShape(20.dp))
                    .border(0.5.dp, LibCyan.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "${altoComponents.size} Components",
                    color = LibCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Practical-ready count
            val practicalCount = theoryContentMap.values.count { it.hasPracticalPipeline }
            Box(
                modifier = Modifier
                    .background(LibGreen.copy(alpha = 0.10f), RoundedCornerShape(20.dp))
                    .border(0.5.dp, LibGreen.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "$practicalCount Scored Practical",
                    color = LibGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(altoComponents) { component ->

                val theory = theoryContentMap[component.id]
                val hasPractical = theory?.hasPracticalPipeline == true

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, LibBorder, RoundedCornerShape(18.dp))
                        .clickable { onComponentClick(component.id) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = LibCardBg)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Component icon
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (theory?.imageResId != null) {
                                    Image(
                                        painter = androidx.compose.ui.res.painterResource(id = theory.imageResId),
                                        contentDescription = component.name,
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .border(1.dp, LibBorder, RoundedCornerShape(12.dp))
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                Brush.linearGradient(
                                                    colors = if (hasPractical)
                                                        listOf(LibGreen.copy(alpha = 0.25f), LibCyan.copy(alpha = 0.15f))
                                                    else
                                                        listOf(LibCyan.copy(alpha = 0.15f), LibAmber.copy(alpha = 0.10f))
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (hasPractical) Icons.Default.Build else Icons.Default.Info,
                                            contentDescription = null,
                                            tint = if (hasPractical) LibGreen else LibCyan,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = component.name,
                                        color = LibTextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = theory?.subtitle ?: component.function,
                                        color = LibTextSecondary,
                                        fontSize = 11.sp,
                                        maxLines = 1
                                    )
                                }
                            }

                            // Status badge
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (hasPractical) LibGreen.copy(alpha = 0.12f)
                                        else LibAmber.copy(alpha = 0.12f),
                                        CircleShape
                                    )
                                    .border(
                                        0.5.dp,
                                        if (hasPractical) LibGreen.copy(alpha = 0.3f)
                                        else LibAmber.copy(alpha = 0.3f),
                                        CircleShape
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (hasPractical) "SCORED" else "THEORY",
                                    color = if (hasPractical) LibGreen else LibAmber,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Bottom row: learning objectives count + arrow
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val objCount = theory?.learningObjectives?.size ?: 0
                            Text(
                                text = "$objCount learning objectives • ${theory?.procedureSteps?.size ?: 0} steps",
                                color = LibTextSecondary,
                                fontSize = 11.sp
                            )

                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Open",
                                tint = LibTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}