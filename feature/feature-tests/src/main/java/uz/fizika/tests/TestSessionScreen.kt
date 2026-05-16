package uz.fizika.tests

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import uz.fizika.core.ui.components.*
import uz.fizika.core.ui.theme.NeonColors
import uz.fizika.tests.engine.AdaptiveTestEngine
import uz.fizika.tests.engine.TestQuestion

@Composable
fun TestSessionScreen(navController: NavController) {
    // Mock data for questions
    val mockQuestions = remember {
        listOf(
            TestQuestion("1", "f_newton_2", "Какая формула выражает второй закон Ньютона?", listOf("F = ma", "E = mc²", "P = IV", "v = d/t"), 0, -1.0f),
            TestQuestion("2", "ohm_law", "Закон Ома для участка цепи:", listOf("I = U/R", "U = IR", "R = U/I", "Все варианты"), 3, 0.0f),
            TestQuestion("3", "energy_kinetic", "Кинетическая энергия тела равна:", listOf("mgh", "mv²/2", "kx²/2", "mc²"), 1, 0.5f),
            TestQuestion("4", "ideal_gas", "Уравнение Менделеева-Клапейрона:", listOf("PV = nRT", "P = F/A", "V = IR", "F = Gmm/r²"), 0, 1.5f),
            TestQuestion("5", "snell_law", "Закон преломления света:", listOf("n1 sin θ1 = n2 sin θ2", "E = hf", "λ = h/p", "F = qE"), 0, 2.5f)
        )
    }

    val engine = remember { AdaptiveTestEngine(mockQuestions) }
    var currentQuestion by remember { mutableStateOf(engine.getNextQuestion()) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isAnswered by remember { mutableStateOf(false) }
    var testFinished by remember { mutableStateOf(false) }

    if (testFinished || currentQuestion == null) {
        TestResultsSummary(
            ability = engine.getCurrentAbility(),
            onFinish = { navController.popBackStack() }
        )
        return
    }

    val question = currentQuestion!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeonColors.Background)
    ) {
        NeonTopBar(
            title = "Тестирование",
            subtitle = "Вопрос ${engine.getProgress()}",
            accentColor = NeonColors.Tertiary,
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.Close, null, tint = NeonColors.OnSurfaceVariant)
                }
            }
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            NeonProgressBar(
                progress = engine.getProgress().toFloat() / mockQuestions.size,
                progressColor = NeonColors.Tertiary,
                glowColor = NeonColors.GlowTertiary
            )

            NeonCard(
                glowColor = NeonColors.GlowTertiary.copy(alpha = 0.2f),
                borderColor = NeonColors.Tertiary.copy(alpha = 0.3f)
            ) {
                Text(
                    text = question.questionText,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = NeonColors.OnBackground,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 30.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                question.options.forEachIndexed { index, option ->
                    val isCorrect = index == question.correctIndex
                    val isSelected = index == selectedOption
                    
                    val itemBgColor = when {
                        !isAnswered -> NeonColors.SurfaceVariant
                        isCorrect -> NeonColors.Secondary.copy(alpha = 0.2f)
                        isSelected -> NeonColors.Error.copy(alpha = 0.2f)
                        else -> NeonColors.SurfaceVariant
                    }
                    
                    val itemBorderColor = when {
                        !isAnswered -> NeonColors.Outline
                        isCorrect -> NeonColors.Secondary
                        isSelected -> NeonColors.Error
                        else -> NeonColors.Outline
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(itemBgColor)
                            .border(1.dp, itemBorderColor, RoundedCornerShape(12.dp))
                            .clickable(enabled = !isAnswered) {
                                selectedOption = index
                            }
                            .padding(16.dp)
                    ) {
                        Text(
                            text = option,
                            color = if (isAnswered && isCorrect) NeonColors.Secondary else NeonColors.OnSurface,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        Box(modifier = Modifier.padding(16.dp)) {
            if (!isAnswered) {
                NeonButton(
                    text = "Ответить",
                    onClick = { isAnswered = true },
                    enabled = selectedOption != null,
                    containerColor = NeonColors.Tertiary,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                NeonButton(
                    text = "Далее",
                    onClick = {
                        engine.updateAbility(question.difficulty, selectedOption == question.correctIndex)
                        val next = engine.getNextQuestion()
                        if (next == null) {
                            testFinished = true
                        } else {
                            currentQuestion = next
                            selectedOption = null
                            isAnswered = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun TestResultsSummary(ability: Float, onFinish: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeonColors.Background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🎉", fontSize = 80.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            "Тест завершен!",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = NeonColors.OnBackground
            )
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Ваш расчетный уровень способностей:",
            color = NeonColors.OnSurfaceVariant
        )
        Text(
            String.format("%.2f", ability),
            style = MaterialTheme.typography.displayMedium.copy(
                color = NeonColors.Tertiary,
                fontWeight = FontWeight.Black
            )
        )
        Spacer(Modifier.height(48.dp))
        NeonButton(
            text = "Вернуться в меню",
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
