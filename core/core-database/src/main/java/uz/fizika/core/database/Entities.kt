package uz.fizika.core.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ─── Formula ─────────────────────────────────────────────────────────────────
@Entity(tableName = "formulas")
data class FormulaEntity(
    @PrimaryKey val id: String,
    val topicId: String,
    val title: String,              // "Второй закон Ньютона"
    val latex: String,              // "F = ma"
    val description: String,
    val units: String,              // "Н, кг·м/с²"
    val variables: String,          // JSON: {"F":"сила","m":"масса","a":"ускорение"}
    val difficulty: Int,            // 1–5
    val exampleCount: Int = 0
)

// ─── Topic ───────────────────────────────────────────────────────────────────
@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey val id: String,
    val name: String,               // "Механика"
    val icon: String,               // emoji или имя иконки
    val color: Long,                // ARGB цвет раздела
    val formulaCount: Int = 0
)

// ─── FormulaLink (граф связей) ────────────────────────────────────────────────
@Entity(
    tableName = "formula_links",
    foreignKeys = [
        ForeignKey(entity = FormulaEntity::class, parentColumns = ["id"], childColumns = ["fromId"]),
        ForeignKey(entity = FormulaEntity::class, parentColumns = ["id"], childColumns = ["toId"])
    ],
    indices = [Index("fromId"), Index("toId")],
    primaryKeys = ["fromId", "toId"]
)
data class FormulaLinkEntity(
    val fromId: String,
    val toId: String,
    val linkType: String   // "derives_from" | "used_in" | "related"
)

// ─── UserProgress ─────────────────────────────────────────────────────────────
@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val formulaId: String,
    val viewedCount: Int = 0,
    val solvedCount: Int = 0,
    val lastSeenAt: Long = 0L
)

// ─── GameScore ────────────────────────────────────────────────────────────────
@Entity(tableName = "game_scores")
data class GameScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val gameType: String,           // "quiz" | "match" | "simulator"
    val score: Int,
    val xpEarned: Int,
    val playedAt: Long = System.currentTimeMillis()
)

// ─── UserProfile ─────────────────────────────────────────────────────────────
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,    // single-row singleton
    val totalXp: Int = 0,
    val level: Int = 1,
    val streakDays: Int = 0,
    val lastActiveDate: String = "",
    val achievements: String = "[]" // JSON список ачивок
)

// ─── ChatMessage (история ИИ-чата) ───────────────────────────────────────────
@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val role: String,               // "user" | "model"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val sessionId: String = "default"
)

// ─── TestResult ───────────────────────────────────────────────────────────────
@Entity(tableName = "test_results")
data class TestResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val topicScores: String,        // JSON: {"Механика":0.8,"Оптика":0.6,...}
    val totalQuestions: Int,
    val correctAnswers: Int,
    val completedAt: Long = System.currentTimeMillis(),
    val abilityEstimate: Float = 0f  // IRT θ параметр
)
