package uz.fizika.core.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import uz.fizika.core.database.entities.*

// ─── FormulaDao ──────────────────────────────────────────────────────────────
@Dao
interface FormulaDao {

    @Query("SELECT * FROM formulas ORDER BY title ASC")
    fun getAllFormulas(): Flow<List<FormulaEntity>>

    @Query("SELECT * FROM formulas WHERE topicId = :topicId")
    fun getFormulasByTopic(topicId: String): Flow<List<FormulaEntity>>

    @Query("SELECT * FROM formulas WHERE id = :id")
    suspend fun getFormulaById(id: String): FormulaEntity?

    // FTS-поиск по title + description
    @Query("""
        SELECT * FROM formulas
        WHERE title LIKE '%' || :query || '%'
           OR description LIKE '%' || :query || '%'
        ORDER BY title ASC
    """)
    fun searchFormulas(query: String): Flow<List<FormulaEntity>>

    @Query("SELECT * FROM formula_links WHERE fromId = :formulaId OR toId = :formulaId")
    suspend fun getLinkedFormulas(formulaId: String): List<FormulaLinkEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFormulas(formulas: List<FormulaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLinks(links: List<FormulaLinkEntity>)

    @Query("SELECT COUNT(*) FROM formulas")
    suspend fun count(): Int
}

// ─── TopicDao ─────────────────────────────────────────────────────────────────
@Dao
interface TopicDao {

    @Query("SELECT * FROM topics ORDER BY name ASC")
    fun getAllTopics(): Flow<List<TopicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<TopicEntity>)

    @Query("SELECT COUNT(*) FROM topics")
    suspend fun count(): Int
}

// ─── UserProgressDao ─────────────────────────────────────────────────────────
@Dao
interface UserProgressDao {

    @Query("SELECT * FROM user_progress WHERE formulaId = :formulaId")
    suspend fun getProgress(formulaId: String): UserProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: UserProgressEntity)

    @Query("UPDATE user_progress SET viewedCount = viewedCount + 1, lastSeenAt = :now WHERE formulaId = :id")
    suspend fun incrementView(id: String, now: Long = System.currentTimeMillis())
}

// ─── UserProfileDao ───────────────────────────────────────────────────────────
@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET totalXp = totalXp + :xp WHERE id = 1")
    suspend fun addXp(xp: Int)

    @Query("UPDATE user_profile SET streakDays = :days, lastActiveDate = :date WHERE id = 1")
    suspend fun updateStreak(days: Int, date: String)
}

// ─── GameScoreDao ─────────────────────────────────────────────────────────────
@Dao
interface GameScoreDao {

    @Query("SELECT * FROM game_scores ORDER BY playedAt DESC LIMIT 50")
    fun getRecentScores(): Flow<List<GameScoreEntity>>

    @Query("SELECT MAX(score) FROM game_scores WHERE gameType = :type")
    suspend fun getHighScore(type: String): Int?

    @Insert
    suspend fun insertScore(score: GameScoreEntity)
}

// ─── ChatMessageDao ───────────────────────────────────────────────────────────
@Dao
interface ChatMessageDao {

    @Query("SELECT * FROM chat_messages WHERE sessionId = :session ORDER BY timestamp ASC")
    fun getMessagesForSession(session: String): Flow<List<ChatMessageEntity>>

    @Insert
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages WHERE sessionId = :session")
    suspend fun clearSession(session: String)

    @Query("SELECT DISTINCT sessionId FROM chat_messages ORDER BY timestamp DESC")
    suspend fun getAllSessions(): List<String>
}

// ─── TestResultDao ────────────────────────────────────────────────────────────
@Dao
interface TestResultDao {

    @Query("SELECT * FROM test_results ORDER BY completedAt DESC LIMIT 20")
    fun getRecentResults(): Flow<List<TestResultEntity>>

    @Query("SELECT AVG(abilityEstimate) FROM test_results")
    suspend fun getAverageAbility(): Float?

    @Insert
    suspend fun insertResult(result: TestResultEntity)
}
