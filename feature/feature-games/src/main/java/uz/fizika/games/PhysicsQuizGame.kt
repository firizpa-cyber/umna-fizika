package uz.fizika.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import uz.fizika.core.ui.components.*
import uz.fizika.core.ui.theme.NeonColors

// Вопрос для блиц-игры
data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val xpReward: Int = 20
)

// Встроенный набор вопросов
val physicsQuizBank = listOf(
    QuizQuestion("Чему равна сила, если масса тела 2 кг, а ускорение 5 м/с²?",
        listOf("5 Н", "10 Н", "7 Н", "2.5 Н"), 1, "F = ma = 2·5 = 10 Н"),
    QuizQuestion("Какая единица измерения напряжения?",
        listOf("Ампер", "Ватт", "Вольт", "Ом"), 2, "Напряжение измеряется в Вольтах (В)"),
    QuizQuestion("Чему равна кинетическая энергия тела массой 4 кг при скорости 3 м/с?",
        listOf("12 Дж", "18 Дж", "24 Дж", "6 Дж"), 1, "Eₖ = mv²/2 = 4·9/2 = 18 Дж"),
    QuizQuestion("Закон Ома — это...",
        listOf("F=ma", "I=U/R", "E=mc²", "pV=nRT"), 1, "I = U/R — ток равен напряжению делённому на сопротивление"),
    QuizQuestion("Постоянная Планка h ≈",
        listOf("6.626·10⁻³⁴ Дж·с", "9.8 м/с²", "3·10⁸ м/с", "6.674·10⁻¹¹"), 0, "h = 6.626·10⁻³⁴ Дж·с"),
    QuizQuestion("Какое уравнение описывает идеальный газ?",
        listOf("E=mc²", "F=ma", "pV=nRT", "λ=h/mv"), 2, "Уравнение Менделеева-Клапейрона: pV = nRT"),
    QuizQuestion("Скорость света в вакууме ≈",
        listOf("3·10⁶ м/с", "3·10⁸ м/с", "3·10¹⁰ м/с", "300 м/с"), 1, "c ≈ 3·10⁸ м/с"),
    QuizQuestion("Что измеряет амперметр?",
        listOf("Напряжение", "Мощность", "Силу тока", "Сопротивление"), 2, "Амперметр измеряет силу тока в амперах"),
    QuizQuestion("Первое начало термодинамики: ΔU = ...",
        listOf("Q + W", "Q - W", "W - Q", "Q/W"), 1, "ΔU = Q − W: изменение внутренней энергии"),
    QuizQuestion("Закон Кулона описывает...",
        listOf("Притяжение планет", "Взаимодействие зарядов", "Движение газов", "Закон Ома"), 1, "F = k·q₁q₂/r²")
)

@Composable
fun PhysicsQuizGame(navController: NavController) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var score        by remember { mutableIntStateOf(0) }
    var streak       by remember { mutableIntStateOf(0) }
    var totalXp      by remember { mutableIntStateOf(0) }
    var selectedIdx  by remember { mutableStateOf<Int?>(null) }
    var showExplanation by remember { mutableStateOf(false) }
    var timeLeft     by remember { mutableIntStateOf(15) }
    var gameOver     by remember { mutableStateOf(false) }
    val questions    = remember { physicsQuizBank.shuffled() }

    // Таймер
    LaunchedEffect(currentIndex, selectedIdx) {
        if (selectedIdx != null || gameOver) return@LaunchedEffect
        timeLeft = 15
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        // Время вышло — засчитываем как неправильный
        if (selectedIdx == null) {
            selectedIdx = -1
            showExplanation = true
            streak = 0
        }
    }

    if (gameOver) {
        QuizResultScreen(score = score, totalXp = totalXp, onRestart = {
            currentIndex = 0; score = 0; streak = 0; totalXp = 0
            selectedIdx = null; showExplanation = false; gameOver = false
        }, onBack = { navController.popBackStack() })
        return
    }

    val question = questions.getOrNull(currentIndex) ?: run {
        gameOver = true; return
    }
    val multiplier = when {
        streak >= 5 -> 3f; streak >= 3 -> 2f; streak >= 2 -> 1.5f; else -> 1f
    }

    Column(
        modifier = Modifier.fillMaxSize().background(NeonColors.Background)
    ) {
        NeonTopBar(
            title       = "Физический блиц",
            subtitle    = "Вопрос ${currentIndex + 1} / ${questions.size}",
            accentColor = NeonColors.Primary,
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.Close, null, tint = NeonColors.OnSurfaceVariant)
                }
            }
        )

        // Прогресс + XP
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeonProgressBar(
                    progress = (currentIndex + 1f) / questions.size,
                    modifier = Modifier.weight(1f).padding(end = 12.dp)
                )
                XpBadge(xp = totalXp)
            }
            if (streak >= 2) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("🔥", fontSize = 14.sp)
                    Text("Серия x${multiplier.toInt()} (${streak} подряд)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = NeonColors.Tertiary, fontWeight = FontWeight.Bold))
                }
            }
        }

        // Таймер
        TimerCircle(timeLeft = timeLeft, modifier = Modifier.align(Alignment.CenterHorizontally))

        Spacer(Modifier.height(12.dp))

        // Вопрос
        NeonCard(
            modifier    = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            glowColor   = NeonColors.GlowPrimary,
            borderColor = NeonColors.Primary.copy(alpha = 0.4f)
        ) {
            Text(
                text  = question.question,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = NeonColors.OnBackground, fontWeight = FontWeight.SemiBold,
                    lineHeight = 24.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(16.dp))

        // Варианты ответов
        question.options.forEachIndexed { idx, option ->
            AnswerOption(
                text      = option,
                index     = idx,
                selected  = selectedIdx,
                correct   = question.correctIndex,
                onSelect  = { chosen ->
                    if (selectedIdx != null) return@AnswerOption
                    selectedIdx = chosen
                    val isCorrect = chosen == question.correctIndex
                    if (isCorrect) {
                        streak++
                        val xpEarned = (question.xpReward * multiplier).toInt()
                        totalXp += xpEarned
                        score++
                    } else {
                        streak = 0
                    }
                    showExplanation = true
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        // Пояснение + кнопка «Далее»
        AnimatedVisibility(
            visible = showExplanation,
            enter   = slideInVertically { it } + fadeIn()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Surface(
                    color = NeonColors.SurfaceContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Info, null,
                            tint = NeonColors.Secondary, modifier = Modifier.size(18.dp))
                        Text(question.explanation,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = NeonColors.OnSurface))
                    }
                }
                Spacer(Modifier.height(12.dp))
                NeonButton(
                    text     = if (currentIndex < questions.size - 1) "Следующий вопрос →" else "Результаты",
                    onClick  = {
                        if (currentIndex < questions.size - 1) {
                            currentIndex++; selectedIdx = null; showExplanation = false
                        } else { gameOver = true }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun TimerCircle(timeLeft: Int, modifier: Modifier = Modifier) {
    val color = when {
        timeLeft > 10 -> NeonColors.Secondary
        timeLeft > 5  -> NeonColors.Tertiary
        else          -> NeonColors.Error
    }
    Box(
        modifier          = modifier.size(64.dp),
        contentAlignment  = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress   = { timeLeft / 15f },
            modifier   = Modifier.fillMaxSize(),
            color      = color,
            trackColor = NeonColors.Outline,
            strokeWidth= 4.dp
        )
        Text(
            text  = "$timeLeft",
            style = MaterialTheme.typography.titleLarge.copy(
                color = color, fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun AnswerOption(
    text: String, index: Int, selected: Int?, correct: Int,
    onSelect: (Int) -> Unit, modifier: Modifier = Modifier
) {
    val bgColor = when {
        selected == null                   -> NeonColors.SurfaceVariant
        index == correct                   -> NeonColors.Secondary.copy(alpha = 0.2f)
        index == selected && index != correct -> NeonColors.Error.copy(alpha = 0.2f)
        else                               -> NeonColors.SurfaceVariant
    }
    val borderColor = when {
        selected == null                   -> NeonColors.Outline
        index == correct                   -> NeonColors.Secondary
        index == selected && index != correct -> NeonColors.Error
        else                               -> NeonColors.Outline
    }
    val icon = when {
        selected != null && index == correct           -> "✓"
        selected != null && index == selected && index != correct -> "✗"
        else -> ('A' + index).toString()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = selected == null) { onSelect(index) }
            .padding(14.dp),
        verticalAlignment    = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier         = Modifier.size(28.dp).clip(CircleShape).background(borderColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, style = MaterialTheme.typography.labelMedium.copy(
                color = borderColor, fontWeight = FontWeight.Bold))
        }
        Text(text, style = MaterialTheme.typography.bodyMedium.copy(color = NeonColors.OnSurface))
    }
}

@Composable
private fun QuizResultScreen(
    score: Int, totalXp: Int, onRestart: () -> Unit, onBack: () -> Unit
) {
    val total     = physicsQuizBank.size
    val percent   = (score.toFloat() / total * 100).toInt()
    val emoji     = when { percent >= 80 -> "🏆"; percent >= 60 -> "⭐"; else -> "📚" }

    Column(
        modifier               = Modifier.fillMaxSize().background(NeonColors.Background).padding(24.dp),
        horizontalAlignment    = Alignment.CenterHorizontally,
        verticalArrangement    = Arrangement.Center
    ) {
        Text(emoji, fontSize = 72.sp)
        Spacer(Modifier.height(16.dp))
        Text("Результат", style = MaterialTheme.typography.headlineMedium.copy(
            color = NeonColors.OnBackground, fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(8.dp))
        Text("$score / $total правильных ответов",
            style = MaterialTheme.typography.titleLarge.copy(color = NeonColors.Primary))
        Spacer(Modifier.height(4.dp))
        XpBadge(xp = totalXp)
        Spacer(Modifier.height(32.dp))
        NeonProgressBar(progress = score.toFloat() / total, height = 12.dp,
            modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(32.dp))
        NeonButton("Сыграть снова", onRestart, Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onBack) {
            Text("В меню", color = NeonColors.OnSurfaceVariant)
        }
    }
}

private val NeonColors.Tertiary get() = Color(0xFFE88C5A)
private val NeonColors.Gold     get() = Color(0xFFFFD700)
private val NeonColors.XpGlow   get() = Color(0x55FFD700)
