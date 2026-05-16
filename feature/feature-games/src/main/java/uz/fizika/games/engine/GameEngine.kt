package uz.fizika.games.engine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import uz.fizika.core.database.dao.GameScoreDao
import uz.fizika.core.database.dao.UserProfileDao
import uz.fizika.core.database.entities.GameScoreEntity
import javax.inject.Inject
import javax.inject.Singleton

data class GameEngineState(
    val totalXp: Int        = 0,
    val level: Int          = 1,
    val streakDays: Int     = 0,
    val sessionXp: Int      = 0,         // XP за текущую сессию
    val multiplier: Float   = 1f,        // Множитель за серию
    val correctStreak: Int  = 0          // Серия правильных ответов подряд
)

/**
 * GameEngine — управляет XP, уровнями, стриками и множителями.
 * Единственный источник истины для игровой механики.
 */
@Singleton
class GameEngine @Inject constructor(
    private val profileDao: UserProfileDao,
    private val scoreDao: GameScoreDao
) {
    private val _state = MutableStateFlow(GameEngineState())
    val state: StateFlow<GameEngineState> = _state.asStateFlow()

    companion object {
        // XP необходимый для каждого уровня: level^2 * 100
        fun xpForLevel(level: Int) = level * level * 100

        // Множители за серию правильных ответов
        fun multiplierForStreak(streak: Int) = when {
            streak >= 10 -> 3f
            streak >= 5  -> 2f
            streak >= 3  -> 1.5f
            else         -> 1f
        }
    }

    suspend fun initialize() {
        // Загружаем профиль из БД
        // (в реальной реализации через Flow, здесь — разовое чтение)
    }

    /** Вызывается после правильного ответа */
    suspend fun onCorrectAnswer(baseXp: Int, gameType: String) {
        _state.update { current ->
            val newStreak = current.correctStreak + 1
            val mult      = multiplierForStreak(newStreak)
            val earned    = (baseXp * mult).toInt()
            val newSessionXp = current.sessionXp + earned
            val newTotalXp   = current.totalXp + earned
            val newLevel     = computeLevel(newTotalXp)

            current.copy(
                totalXp      = newTotalXp,
                level        = newLevel,
                sessionXp    = newSessionXp,
                multiplier   = mult,
                correctStreak= newStreak
            )
        }
        profileDao.addXp(baseXp)
    }

    /** Вызывается после неправильного ответа */
    fun onWrongAnswer() {
        _state.update { it.copy(correctStreak = 0, multiplier = 1f) }
    }

    /** Сохраняет результат игры в БД */
    suspend fun saveScore(gameType: String, score: Int) {
        scoreDao.insertScore(
            GameScoreEntity(
                gameType = gameType,
                score    = score,
                xpEarned = _state.value.sessionXp
            )
        )
        _state.update { it.copy(sessionXp = 0, correctStreak = 0, multiplier = 1f) }
    }

    private fun computeLevel(totalXp: Int): Int {
        var level = 1
        while (totalXp >= xpForLevel(level + 1)) level++
        return level
    }
}
