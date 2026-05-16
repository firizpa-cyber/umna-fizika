package uz.fizika.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Modifier.softGlow — рисует мягкое неоновое свечение вокруг элемента.
 * Минимизирует нагрузку на зрение: используем малый радиус и низкую альфа.
 *
 * @param glowColor   Цвет свечения (рекомендуется NeonColors.GlowPrimary)
 * @param glowRadius  Радиус размытия (по умолчанию 12.dp — мягкое свечение)
 * @param offsetY     Вертикальное смещение тени (0.dp = равномерное свечение)
 */
fun Modifier.softGlow(
    glowColor: Color,
    glowRadius: Dp = 12.dp,
    offsetY: Dp = 0.dp
): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            asFrameworkPaint().apply {
                isAntiAlias = true
                color = android.graphics.Color.TRANSPARENT
                setShadowLayer(
                    glowRadius.toPx(),
                    0f,
                    offsetY.toPx(),
                    glowColor.copy(alpha = 0.72f).toArgb()
                )
            }
        }
        canvas.drawRoundRect(
            left   = 0f,
            top    = 0f,
            right  = size.width,
            bottom = size.height,
            radiusX = 16.dp.toPx(),
            radiusY = 16.dp.toPx(),
            paint   = paint
        )
    }
}

/**
 * Обёртка для применения softGlow к любому Composable.
 */
@Composable
fun GlowBox(
    glowColor: Color,
    glowRadius: Dp = 12.dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.softGlow(glowColor, glowRadius)
    ) {
        content()
    }
}
