package uz.fizika.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import uz.fizika.core.ui.components.*
import uz.fizika.core.ui.theme.NeonColors

data class GameMode(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color,
    val xpReward: String,
    val route: String
)

@Composable
fun GamesScreen(navController: NavController) {
    val gameModes = remember {
        listOf(
            GameMode("quiz",  "Физический блиц",
                "Ответь на вопросы как можно быстрее! Серия правильных ответов даёт бонус x3.",
                Icons.Default.FlashOn, NeonColors.Primary, "до 300 XP", "game_quiz"),
            GameMode("match", "Сопоставь формулу",
                "Переворачивай карточки и находи пары: формула + её название.",
                Icons.Default.GridView, NeonColors.Secondary, "до 200 XP", "game_match"),
            GameMode("sim",   "2D Симулятор",
                "Изменяй параметры и наблюдай за физическими явлениями в реальном времени.",
                Icons.Default.Science, NeonColors.Tertiary, "50 XP", "game_simulator"),
            GameMode("daily", "Задача дня",
                "Одна сложная задача в сутки. Реши её и получи дневной бонус!",
                Icons.Default.Today, NeonColors.Gold, "150 XP + стрик", "game_daily")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeonColors.Background)
    ) {
        NeonTopBar(
            title    = "Игровой режим",
            subtitle = "Учись играя",
            accentColor = NeonColors.Secondary
        )

        // XP / Уровень пользователя
        UserLevelBanner(modifier = Modifier.padding(16.dp))

        Spacer(Modifier.height(8.dp))

        Text(
            text     = "Выбери режим",
            style    = MaterialTheme.typography.titleMedium.copy(
                color = NeonColors.OnBackground, fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement   = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(gameModes) { mode ->
                GameModeCard(
                    mode    = mode,
                    onClick = { navController.navigate(mode.route) }
                )
            }
        }
    }
}

// ─── UserLevelBanner ─────────────────────────────────────────────────────────
@Composable
private fun UserLevelBanner(modifier: Modifier = Modifier) {
    NeonCard(
        modifier    = modifier.fillMaxWidth(),
        glowColor   = NeonColors.XpGlow,
        borderColor = NeonColors.Gold.copy(alpha = 0.4f)
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text("Уровень 1",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = NeonColors.Gold, fontWeight = FontWeight.Bold
                    )
                )
                Text("0 / 100 XP до следующего уровня",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NeonColors.OnSurfaceVariant
                    )
                )
                Spacer(Modifier.height(8.dp))
                NeonProgressBar(
                    progress      = 0f,
                    progressColor = NeonColors.Gold,
                    glowColor     = NeonColors.XpGlow,
                    modifier      = Modifier.width(180.dp)
                )
            }
            XpBadge(xp = 0)
        }
    }
}

// ─── GameModeCard ─────────────────────────────────────────────────────────────
@Composable
private fun GameModeCard(mode: GameMode, onClick: () -> Unit) {
    val glowColor = mode.accentColor.copy(alpha = 0.2f)

    NeonCard(
        glowColor   = glowColor,
        borderColor = mode.accentColor.copy(alpha = 0.35f),
        modifier    = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(mode.accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(mode.icon, null,
                    tint     = mode.accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column {
                Text(
                    text  = mode.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color      = NeonColors.OnBackground,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = mode.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NeonColors.OnSurfaceVariant
                    ),
                    maxLines  = 3,
                    lineHeight = 16.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("⚡", style = MaterialTheme.typography.labelSmall)
                Text(
                    text  = mode.xpReward,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = mode.accentColor, fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

private val NeonColors.Gold get() = Color(0xFFFFD700)
private val androidx.compose.ui.unit.TextUnit.sp get() = this
