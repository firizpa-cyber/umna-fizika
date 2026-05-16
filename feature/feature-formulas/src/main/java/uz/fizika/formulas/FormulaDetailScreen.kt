package uz.fizika.formulas

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import uz.fizika.core.database.entities.FormulaEntity
import uz.fizika.core.ui.components.*
import uz.fizika.core.ui.theme.NeonColors
import uz.fizika.formulas.model.GraphEdge
import uz.fizika.formulas.model.GraphNode
import uz.fizika.formulas.model.topicColorMap
import uz.fizika.formulas.viewmodel.FormulaDetailViewModel
import kotlin.math.*

@Composable
fun FormulaDetailScreen(
    formulaId: String,
    navController: NavController,
    viewModel: FormulaDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(formulaId) { viewModel.load(formulaId) }
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        Box(Modifier.fillMaxSize().background(NeonColors.Background), Alignment.Center) {
            CircularProgressIndicator(color = NeonColors.Primary)
        }
        return
    }

    val formula = state.formula ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeonColors.Background)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Top Bar ──────────────────────────────────────────────────────────
        NeonTopBar(
            title = formula.title,
            subtitle = "Сложность: ${"★".repeat(formula.difficulty)}${"☆".repeat(5 - formula.difficulty)}",
            accentColor = Color(topicColorMap[formula.topicId] ?: 0xFF7B8CDE),
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, null, tint = NeonColors.OnSurfaceVariant)
                }
            }
        )

        Spacer(Modifier.height(16.dp))

        // ── Карточка формулы ─────────────────────────────────────────────────
        FormulaHeroCard(formula = formula)

        Spacer(Modifier.height(16.dp))

        // ── Переменные ───────────────────────────────────────────────────────
        VariablesSection(variablesJson = formula.variables)

        Spacer(Modifier.height(16.dp))

        // ── Граф связей ──────────────────────────────────────────────────────
        if (state.graphNodes.size > 1) {
            FormulaGraphSection(
                nodes    = state.graphNodes,
                edges    = state.graphEdges,
                focusId  = formulaId,
                onNodeClick = { id ->
                    if (id != formulaId) navController.navigate("formula_detail/$id")
                }
            )
            Spacer(Modifier.height(16.dp))
        }

        // ── Кнопки действий ──────────────────────────────────────────────────
        ActionButtons(
            onSolve   = { navController.navigate("solver?formulaId=${formula.id}") },
            onAskAI   = { navController.navigate("ai_chat?formulaId=${formula.id}") },
            modifier  = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(32.dp))
    }
}

// ─── FormulaHeroCard ─────────────────────────────────────────────────────────
@Composable
private fun FormulaHeroCard(formula: FormulaEntity) {
    val accentColor = Color(topicColorMap[formula.topicId] ?: 0xFF7B8CDE)

    NeonCard(
        glowColor   = accentColor.copy(alpha = 0.25f),
        borderColor = accentColor.copy(alpha = 0.5f),
        modifier    = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Формула (крупно)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(accentColor.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                .padding(vertical = 20.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text  = formula.latex,
                style = MaterialTheme.typography.headlineMedium.copy(
                    color      = accentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 28.sp,
                    letterSpacing = 1.sp
                )
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text  = formula.description,
            style = MaterialTheme.typography.bodyMedium.copy(color = NeonColors.OnSurface),
            lineHeight = 22.sp
        )

        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Straighten, null,
                tint = NeonColors.OnSurfaceVariant, modifier = Modifier.size(16.dp))
            Text(
                text  = formula.units,
                style = MaterialTheme.typography.labelMedium.copy(color = NeonColors.OnSurfaceVariant)
            )
        }
    }
}

// ─── VariablesSection ────────────────────────────────────────────────────────
@Composable
private fun VariablesSection(variablesJson: String) {
    // Простой парсер JSON вида {"key":"value",...}
    val vars = remember(variablesJson) {
        variablesJson
            .removeSurrounding("{", "}")
            .split(",")
            .mapNotNull { entry ->
                val parts = entry.split("\":\"")
                if (parts.size == 2) {
                    parts[0].trim('"', ' ') to parts[1].trim('"', ' ', '}')
                } else null
            }
    }
    if (vars.isEmpty()) return

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text  = "Обозначения",
            style = MaterialTheme.typography.titleMedium.copy(
                color = NeonColors.OnBackground, fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(Modifier.height(8.dp))
        vars.forEach { (symbol, meaning) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text  = symbol,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = NeonColors.Primary, fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.width(40.dp)
                )
                Text(
                    text  = "—",
                    style = MaterialTheme.typography.bodyMedium.copy(color = NeonColors.Outline)
                )
                Text(
                    text  = meaning,
                    style = MaterialTheme.typography.bodyMedium.copy(color = NeonColors.OnSurface)
                )
            }
        }
    }
}

// ─── FormulaGraphSection ─────────────────────────────────────────────────────
@Composable
private fun FormulaGraphSection(
    nodes: List<GraphNode>,
    edges: List<GraphEdge>,
    focusId: String,
    onNodeClick: (String) -> Unit
) {
    // Раскладываем узлы по кругу вокруг центрального
    val layoutNodes = remember(nodes, focusId) {
        val result = nodes.toMutableList()
        val center = result.indexOfFirst { it.id == focusId }
        if (center >= 0) {
            val cx = 300f; val cy = 200f; val r = 130f
            val others = result.indices.filter { it != center }
            result[center].x = cx; result[center].y = cy
            others.forEachIndexed { i, idx ->
                val angle = 2 * PI * i / others.size
                result[idx].x = (cx + r * cos(angle)).toFloat()
                result[idx].y = (cy + r * sin(angle)).toFloat()
            }
        }
        result
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text  = "Связанные формулы",
            style = MaterialTheme.typography.titleMedium.copy(
                color = NeonColors.OnBackground, fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(NeonColors.SurfaceVariant)
                .border(1.dp, NeonColors.Outline, RoundedCornerShape(16.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Рёбра
                edges.forEach { edge ->
                    val from = layoutNodes.find { it.id == edge.fromId }
                    val to   = layoutNodes.find { it.id == edge.toId }
                    if (from != null && to != null) {
                        val edgeColor = when (edge.type) {
                            "derives_from" -> Color(0xFF7B8CDE).copy(alpha = 0.7f)
                            "used_in"      -> Color(0xFF4FC3A1).copy(alpha = 0.7f)
                            else           -> Color(0xFF7880A8).copy(alpha = 0.5f)
                        }
                        drawLine(
                            color       = edgeColor,
                            start       = Offset(from.x, from.y),
                            end         = Offset(to.x, to.y),
                            strokeWidth = 2f,
                            pathEffect  = PathEffect.dashPathEffect(floatArrayOf(8f, 4f))
                        )
                    }
                }
                // Узлы
                layoutNodes.forEach { node ->
                    val color = Color(topicColorMap[node.topicId] ?: 0xFF7B8CDE)
                    val isFocus = node.id == focusId
                    val radius  = if (isFocus) 28f else 20f

                    drawCircle(
                        color  = color.copy(alpha = 0.15f),
                        radius = radius + 8f,
                        center = Offset(node.x, node.y)
                    )
                    drawCircle(
                        color  = color,
                        radius = radius,
                        center = Offset(node.x, node.y),
                        style  = Stroke(width = 2f)
                    )
                    if (isFocus) {
                        drawCircle(
                            color  = color.copy(alpha = 0.3f),
                            radius = radius - 4f,
                            center = Offset(node.x, node.y)
                        )
                    }
                }
            }
        }
    }
}

// ─── ActionButtons ────────────────────────────────────────────────────────────
@Composable
private fun ActionButtons(
    onSolve: () -> Unit,
    onAskAI: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier              = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        NeonButton(
            text           = "Решить задачу",
            onClick        = onSolve,
            modifier       = Modifier.weight(1f),
            containerColor = NeonColors.PrimaryContainer,
            contentColor   = NeonColors.Primary,
            glowColor      = NeonColors.GlowPrimary
        )
        NeonButton(
            text           = "Спросить ИИ",
            onClick        = onAskAI,
            modifier       = Modifier.weight(1f),
            containerColor = NeonColors.SecondaryContainer,
            contentColor   = NeonColors.Secondary,
            glowColor      = NeonColors.GlowSecondary
        )
    }
}
