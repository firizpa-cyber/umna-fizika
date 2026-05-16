package uz.fizika.formulas

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import uz.fizika.core.database.entities.FormulaEntity
import uz.fizika.core.database.entities.TopicEntity
import uz.fizika.core.ui.components.*
import uz.fizika.core.ui.theme.NeonColors
import uz.fizika.formulas.model.topicColorMap
import uz.fizika.formulas.viewmodel.FormulaListViewModel

@Composable
fun FormulaListScreen(
    navController: NavController,
    viewModel: FormulaListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeonColors.Background)
    ) {
        // ── Top Bar ──────────────────────────────────────────────────────────
        NeonTopBar(
            title    = "Справочник формул",
            subtitle = "${state.formulas.size} формул",
            accentColor = NeonColors.Primary
        )

        // ── Поиск ────────────────────────────────────────────────────────────
        SearchBar(
            query    = state.searchQuery,
            onQuery  = viewModel::onSearchQuery,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // ── Фильтр по разделам ────────────────────────────────────────────────
        AnimatedVisibility(visible = state.searchQuery.isBlank()) {
            TopicFilterRow(
                topics   = state.topics,
                selected = state.selectedTopicId,
                onSelect = viewModel::selectTopic
            )
        }

        // ── Список формул ─────────────────────────────────────────────────────
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonColors.Primary)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = state.formulas,
                    key   = { it.id }
                ) { formula ->
                    FormulaCard(
                        formula = formula,
                        topicColor = Color(
                            topicColorMap[formula.topicId] ?: 0xFF7B8CDE
                        ),
                        onClick = {
                            navController.navigate("formula_detail/${formula.id}")
                        }
                    )
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

// ─── SearchBar ───────────────────────────────────────────────────────────────
@Composable
private fun SearchBar(
    query: String,
    onQuery: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value         = query,
        onValueChange = onQuery,
        modifier      = modifier.fillMaxWidth(),
        placeholder   = { Text("Поиск формул…", color = NeonColors.OnSurfaceVariant) },
        leadingIcon   = {
            Icon(Icons.Default.Search, null, tint = NeonColors.OnSurfaceVariant)
        },
        trailingIcon  = if (query.isNotBlank()) {{
            IconButton(onClick = { onQuery("") }) {
                Icon(Icons.Default.Close, null, tint = NeonColors.OnSurfaceVariant)
            }
        }} else null,
        singleLine    = true,
        shape         = RoundedCornerShape(12.dp),
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = NeonColors.Primary,
            unfocusedBorderColor = NeonColors.Outline,
            cursorColor          = NeonColors.Primary,
            focusedTextColor     = NeonColors.OnBackground,
            unfocusedTextColor   = NeonColors.OnBackground,
            focusedContainerColor   = NeonColors.SurfaceVariant,
            unfocusedContainerColor = NeonColors.SurfaceVariant
        )
    )
}

// ─── TopicFilterRow ───────────────────────────────────────────────────────────
@Composable
private fun TopicFilterRow(
    topics: List<TopicEntity>,
    selected: String?,
    onSelect: (String?) -> Unit
) {
    LazyRow(
        contentPadding       = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            NeonChip(
                label    = "Все",
                selected = selected == null,
                onClick  = { onSelect(null) }
            )
        }
        items(topics) { topic ->
            val color = Color(topicColorMap[topic.id] ?: 0xFF7B8CDE)
            NeonChip(
                label       = "${topic.icon} ${topic.name}",
                selected    = selected == topic.id,
                onClick     = { onSelect(if (selected == topic.id) null else topic.id) },
                accentColor = color
            )
        }
    }
}

// ─── FormulaCard ─────────────────────────────────────────────────────────────
@Composable
private fun FormulaCard(
    formula: FormulaEntity,
    topicColor: Color,
    onClick: () -> Unit
) {
    val glowColor = topicColor.copy(alpha = 0.2f)

    NeonCard(
        glowColor   = glowColor,
        borderColor = topicColor.copy(alpha = 0.3f),
        modifier    = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Боковая цветная полоска
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(topicColor)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text  = formula.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color      = NeonColors.OnBackground,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                // LaTeX отображаем как текст (для LaTeX-рендера нужен MathView)
                Text(
                    text  = formula.latex,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = topicColor
                    ),
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text  = formula.units,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NeonColors.OnSurfaceVariant
                    )
                )
            }

            // Индикатор сложности
            DifficultyDots(difficulty = formula.difficulty, color = topicColor)

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint   = NeonColors.OnSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ─── DifficultyDots ───────────────────────────────────────────────────────────
@Composable
private fun DifficultyDots(difficulty: Int, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(5) { i ->
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (i < difficulty) color else NeonColors.Outline)
            )
        }
    }
}
