package com.autolearn.ar.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.autolearn.ar.data.altoComponents
import com.autolearn.ar.data.theoryContentMap
import com.autolearn.ar.model.EngineComponent
import com.autolearn.ar.ui.theme.LocalAppColors

@Composable
fun LibraryScreen(
    onComponentClick: (String) -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val app = LocalAppColors.current
    val shapes = MaterialTheme.shapes
    val type = MaterialTheme.typography
    var query by remember { mutableStateOf("") }

    val filtered = remember(query) {
        val q = query.trim().lowercase()
        if (q.isEmpty()) {
            altoComponents
        } else {
            altoComponents.filter { component ->
                val theory = theoryContentMap[component.id]
                component.name.contains(q, ignoreCase = true) ||
                    component.function.contains(q, ignoreCase = true) ||
                    component.specifications.contains(q, ignoreCase = true) ||
                    (theory?.subtitle?.contains(q, ignoreCase = true) == true) ||
                    (theory?.title?.contains(q, ignoreCase = true) == true)
            }
        }
    }

    val practicalCount = theoryContentMap.values.count { it.hasPracticalPipeline }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Library",
            style = type.headlineSmall,
            color = colors.onBackground
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Search theory sheets and practical modules.",
            style = type.bodyMedium,
            color = colors.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
                Text("Search components", style = type.bodyMedium)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search"
                )
            },
            shape = shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.outline,
                focusedContainerColor = colors.surface,
                unfocusedContainerColor = colors.surface
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .background(colors.primaryContainer, shapes.small)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${filtered.size} of ${altoComponents.size}",
                    style = type.labelMedium,
                    color = colors.onPrimaryContainer
                )
            }
            Box(
                modifier = Modifier
                    .background(colors.tertiaryContainer, shapes.small)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "$practicalCount scored",
                    style = type.labelMedium,
                    color = colors.onTertiaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No components match “$query”.",
                    style = type.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filtered, key = { it.id }) { component ->
                    LibraryComponentCard(
                        component = component,
                        onClick = { onComponentClick(component.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LibraryComponentCard(
    component: EngineComponent,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val app = LocalAppColors.current
    val shapes = MaterialTheme.shapes
    val type = MaterialTheme.typography
    val theory = theoryContentMap[component.id]
    val hasPractical = theory?.hasPracticalPipeline == true

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, colors.outline, shapes.medium)
            .clickable(onClick = onClick),
        shape = shapes.medium,
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (theory?.imageResId != null) {
                        Image(
                            painter = painterResource(id = theory.imageResId),
                            contentDescription = component.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(shapes.small)
                                .border(1.dp, colors.outline, shapes.small)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(shapes.small)
                                .background(colors.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (hasPractical) Icons.Default.Build else Icons.Default.Info,
                                contentDescription = null,
                                tint = if (hasPractical) app.success else colors.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = component.name,
                            style = type.titleSmall,
                            color = colors.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = theory?.subtitle ?: component.function,
                            style = type.bodySmall,
                            color = colors.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .background(
                            if (hasPractical) app.success.copy(alpha = 0.14f)
                            else colors.secondary.copy(alpha = 0.14f),
                            shapes.small
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (hasPractical) "Scored" else "Theory",
                        style = type.labelSmall,
                        color = if (hasPractical) app.success else colors.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val objCount = theory?.learningObjectives?.size ?: 0
                Text(
                    text = "$objCount objectives · ${theory?.procedureSteps?.size ?: 0} steps",
                    style = type.bodySmall,
                    color = colors.onSurfaceVariant
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Open",
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
