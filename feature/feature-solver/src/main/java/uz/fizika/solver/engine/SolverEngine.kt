package uz.fizika.solver.engine

import uz.fizika.core.database.entities.FormulaEntity

data class SolverResult(
    val formula: FormulaEntity,
    val steps: List<String>,
    val finalAnswer: String
)

/**
 * Basic SolverEngine that attempts to match input text with physics formulas.
 */
class SolverEngine(
    private val formulas: List<FormulaEntity>
) {
    /**
     * Tries to find a formula that contains keywords from the input.
     * In a real app, this would use a more sophisticated NLP or rule-based system.
     */
    fun solve(input: String): SolverResult? {
        val tokens = input.lowercase().split(" ", ",", ".")
        
        // Simple keyword matching for demo
        val matchedFormula = formulas.find { formula ->
            val keywords = formula.variables.lowercase()
            tokens.any { it in keywords && it.length > 1 } || formula.title.lowercase() in input.lowercase()
        } ?: return null

        return SolverResult(
            formula = matchedFormula,
            steps = listOf(
                "Анализ условия: выделены ключевые параметры.",
                "Выбор формулы: ${matchedFormula.title}.",
                "Подстановка значений в ${matchedFormula.latex}.",
                "Вычисление результата."
            ),
            finalAnswer = "Результат вычислен на основе ${matchedFormula.latex}."
        )
    }
}
