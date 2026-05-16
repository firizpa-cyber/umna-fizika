package uz.fizika.tests.engine

import uz.fizika.core.database.entities.FormulaEntity
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow

data class TestQuestion(
    val id: String,
    val formulaId: String,
    val questionText: String,
    val options: List<String>,
    val correctIndex: Int,
    val difficulty: Float // difficulty parameter 'b' in IRT (-3.0 to 3.0)
)

/**
 * AdaptiveTestEngine implements a simplified IRT (Item Response Theory) model.
 * It adjusts the next question's difficulty based on the user's current estimated ability (theta).
 */
class AdaptiveTestEngine(
    private val allQuestions: List<TestQuestion>
) {
    private var theta: Float = 0.0f // Estimated ability
    private var variance: Float = 1.0f // Uncertainty of estimate
    private val askedQuestionIds = mutableSetOf<String>()

    /**
     * Probability of a correct answer given ability theta and difficulty b.
     * Logistic model: P(theta) = 1 / (1 + exp(-(theta - b)))
     */
    private fun probabilityCorrect(theta: Float, b: Float): Float {
        return 1.0f / (1.0f + exp(-(theta - b)))
    }

    /**
     * Updates the estimated ability (theta) after an answer.
     * Uses a simplified Bayesian update.
     */
    fun updateAbility(questionDifficulty: Float, isCorrect: Boolean) {
        val p = probabilityCorrect(theta, questionDifficulty)
        val score = if (isCorrect) 1.0f else 0.0f
        
        // Update theta based on discrepancy between result and expectation
        // Learning rate decreases as variance decreases
        val adjustment = (score - p) * variance
        theta += adjustment
        
        // Decrease variance as we get more data
        variance *= 0.9f
    }

    /**
     * Finds the next best question that matches the current theta estimate.
     */
    fun getNextQuestion(): TestQuestion? {
        val available = allQuestions.filter { it.id !in askedQuestionIds }
        if (available.isEmpty()) return null

        // Find question with difficulty closest to current theta
        val next = available.minByOrNull { kotlin.math.abs(it.difficulty - theta) }
        if (next != null) {
            askedQuestionIds.add(next.id)
        }
        return next
    }

    fun getCurrentAbility(): Float = theta
    
    fun getProgress(): Int = askedQuestionIds.size
}
