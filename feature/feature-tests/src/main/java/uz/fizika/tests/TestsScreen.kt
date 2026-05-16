package uz.fizika.tests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import uz.fizika.core.ui.components.*
import uz.fizika.core.ui.theme.NeonColors

@Composable
fun TestsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeonColors.Background)
    ) {
        NeonTopBar(
            title = "Адаптивные тесты",
            subtitle = "Оцени свой уровень",
            accentColor = NeonColors.Tertiary
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Hero Start Card
            NeonCard(
                glowColor = NeonColors.GlowTertiary,
                borderColor = NeonColors.Tertiary.copy(alpha = 0.5f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Готов проверить знания?",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeonColors.OnBackground
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Алгоритм подберет вопросы специально под твой уровень.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeonColors.OnSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))
                    NeonButton(
                        text = "Начать тест",
                        onClick = { navController.navigate("test_session") },
                        containerColor = NeonColors.Tertiary,
                        contentColor = NeonColors.OnTertiary,
                        glowColor = NeonColors.GlowTertiary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Text(
                "История результатов",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = NeonColors.OnBackground
                )
            )

            // Empty state for history for now
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.History,
                        null,
                        modifier = Modifier.size(48.dp),
                        tint = NeonColors.Outline
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "У вас пока нет завершенных тестов",
                        color = NeonColors.OnSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
