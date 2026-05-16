package uz.fizika.formulas.model

import uz.fizika.core.database.entities.FormulaEntity

data class FormulaWithProgress(
    val formula: FormulaEntity,
    val viewedCount: Int = 0,
    val solvedCount: Int = 0
)

data class GraphNode(
    val id: String,
    val label: String,
    val latex: String,
    val topicId: String,
    // Позиция на Canvas (заполняется при layout)
    var x: Float = 0f,
    var y: Float = 0f
)

data class GraphEdge(
    val fromId: String,
    val toId: String,
    val type: String  // "derives_from" | "used_in" | "related"
)

// Цвета разделов для графа
val topicColorMap = mapOf(
    "mechanics"       to 0xFF7B8CDE,
    "thermodynamics"  to 0xFFE88C5A,
    "electrostatics"  to 0xFFFFD700,
    "electrodynamics" to 0xFF4FC3A1,
    "optics"          to 0xFF9B8CD8,
    "quantum"         to 0xFF5BC8F0,
    "nuclear"         to 0xFFFF6B6B,
    "relativity"      to 0xFFB8A0FF
)
