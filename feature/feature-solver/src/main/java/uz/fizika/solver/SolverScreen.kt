package uz.fizika.solver

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
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
import uz.fizika.solver.engine.SolverResult

@Composable
fun SolverScreen(navController: NavController) {
    var taskText by remember { mutableStateOf("") }
    var solverResult by remember { mutableStateOf<SolverResult?>(null) }
    var isSolving by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeonColors.Background)
            .verticalScroll(rememberScrollState())
    ) {
        NeonTopBar(
            title = "Решатель задач",
            subtitle = "Экспертная система",
            accentColor = NeonColors.Primary
        )

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Input Card
            NeonCard(
                glowColor = NeonColors.GlowPrimary.copy(alpha = 0.2f),
                borderColor = NeonColors.Primary.copy(alpha = 0.4f)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Введите условие задачи",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = NeonColors.OnBackground,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    
                    OutlinedTextField(
                        value = taskText,
                        onValueChange = { taskText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        placeholder = { Text("Пример: Масса тела 5 кг, ускорение 2 м/с. Найти силу.", color = NeonColors.OnSurfaceVariant) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonColors.Primary,
                            unfocusedBorderColor = NeonColors.Outline,
                            cursorColor = NeonColors.Primary,
                            focusedContainerColor = NeonColors.SurfaceVariant,
                            unfocusedContainerColor = NeonColors.SurfaceVariant,
                            focusedTextColor = NeonColors.OnBackground,
                            unfocusedTextColor = NeonColors.OnBackground
                        )
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        NeonButton(
                            text = "Распознать фото",
                            onClick = { /* OCR logic hook */ },
                            containerColor = NeonColors.SurfaceVariant,
                            contentColor = NeonColors.OnSurface,
                            modifier = Modifier.weight(1f)
                        )
                        NeonButton(
                            text = "Решить",
                            onClick = { 
                                isSolving = true
                                // Simulated delay
                                isSolving = false
                                // In a real app, logic would go here
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Results Section
            AnimatedVisibility(
                visible = taskText.length > 10, // Show results when some text is entered for demo
                enter = fadeIn() + expandVertically()
            ) {
                SolutionDisplay(
                    result = null // Using null for mock demo display in this turn
                )
            }
        }
    }
}

@Composable
fun SolutionDisplay(result: SolverResult?) {
    NeonCard(
        glowColor = NeonColors.GlowSecondary.copy(alpha = 0.1f),
        borderColor = NeonColors.Secondary.copy(alpha = 0.3f)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoFixHigh, null, tint = NeonColors.Secondary)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Пошаговое решение",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = NeonColors.Secondary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Mock Steps
            val steps = listOf(
                "Дано: m = 5 кг, a = 2 м/с²",
                "Найти: F = ?",
                "Решение: Используем II закон Ньютона: F = ma",
                "Вычисление: F = 5 * 2 = 10 Н"
            )

            steps.forEachIndexed { index, step ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "${index + 1}.",
                        color = NeonColors.Primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(step, color = NeonColors.OnSurface)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NeonColors.Secondary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    "Ответ: 10 Н",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = NeonColors.Secondary,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
