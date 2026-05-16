package uz.fizika.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.fizika.core.ui.theme.NeonColors

// ─────────────────────────────────────────────────────────────────────────────
// NeonCard — карточка с мягким свечением и тонкой неоновой границей
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun NeonCard(
    modifier: Modifier = Modifier,
    glowColor: Color = NeonColors.GlowPrimary,
    borderColor: Color = NeonColors.Primary.copy(alpha = 0.35f),
    shape: Shape = RoundedCornerShape(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .softGlow(glowColor = glowColor, glowRadius = 10.dp)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .clip(shape)
            .background(NeonColors.Surface)
            .padding(16.dp),
        content = content
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// NeonButton — кнопка с градиентом и пульсирующим свечением при нажатии
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    glowColor: Color = NeonColors.GlowPrimary,
    containerColor: Color = NeonColors.Primary,
    contentColor: Color = NeonColors.OnPrimary,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "neon_pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue  = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .softGlow(
                glowColor  = glowColor.copy(alpha = if (enabled) glowAlpha * 0.5f else 0.1f),
                glowRadius = 8.dp
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor   = contentColor,
            disabledContainerColor = NeonColors.SurfaceVariant,
            disabledContentColor   = NeonColors.OnSurfaceVariant
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(text = text, fontWeight = FontWeight.SemiBold)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// NeonProgressBar — полоса прогресса с неоновым заполнением
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun NeonProgressBar(
    progress: Float,   // 0f..1f
    modifier: Modifier = Modifier,
    trackColor: Color    = NeonColors.OutlineVariant,
    progressColor: Color = NeonColors.Primary,
    glowColor: Color     = NeonColors.GlowPrimary,
    height: Dp = 8.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue  = progress.coerceIn(0f, 1f),
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .fillMaxHeight()
                .softGlow(glowColor = glowColor, glowRadius = 6.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            progressColor.copy(alpha = 0.8f),
                            progressColor
                        )
                    ),
                    shape = RoundedCornerShape(50)
                )
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// NeonTopBar — верхняя панель с градиентным фоном
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeonTopBar(
    title: String,
    subtitle: String? = null,
    accentColor: Color = NeonColors.Primary,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        NeonColors.SurfaceVariant,
                        NeonColors.Surface
                    )
                )
            )
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text  = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = NeonColors.OnBackground,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    if (subtitle != null) {
                        Text(
                            text  = subtitle,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = accentColor
                            )
                        )
                    }
                }
            },
            navigationIcon = navigationIcon,
            actions        = actions,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor         = Color.Transparent,
                titleContentColor      = NeonColors.OnBackground,
                actionIconContentColor = accentColor,
                navigationIconContentColor = NeonColors.OnSurfaceVariant
            )
        )
        // Нижняя акцентная линия
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.BottomStart)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            accentColor.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// NeonChip — чип-тег для разделов физики
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun NeonChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    accentColor: Color = NeonColors.Primary,
    modifier: Modifier = Modifier
) {
    val bgColor = if (selected) accentColor.copy(alpha = 0.2f) else NeonColors.SurfaceVariant
    val borderCol = if (selected) accentColor.copy(alpha = 0.6f) else NeonColors.Outline
    val textCol = if (selected) accentColor else NeonColors.OnSurfaceVariant

    FilterChip(
        selected = selected,
        onClick  = onClick,
        label    = { Text(label, color = textCol) },
        modifier = modifier.softGlow(
            glowColor  = if (selected) accentColor.copy(alpha = 0.3f) else Color.Transparent,
            glowRadius = 6.dp
        ),
        colors = FilterChipDefaults.filterChipColors(
            containerColor         = bgColor,
            selectedContainerColor = bgColor,
            labelColor             = textCol
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled       = true,
            selected      = selected,
            borderColor   = borderCol,
            selectedBorderColor = accentColor.copy(alpha = 0.8f),
            borderWidth   = 1.dp
        ),
        shape = RoundedCornerShape(8.dp)
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// XpBadge — бейдж опыта с золотым свечением
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun XpBadge(
    xp: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .softGlow(glowColor = NeonColors.XpGlow, glowRadius = 8.dp)
            .background(
                color = NeonColors.Gold.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp)
            )
            .border(1.dp, NeonColors.Gold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text  = "⚡",
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text  = "$xp XP",
            style = MaterialTheme.typography.labelMedium.copy(
                color      = NeonColors.Gold,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
