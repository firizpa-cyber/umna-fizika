package uz.fizika.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.fizika.core.database.dao.*
import uz.fizika.core.database.entities.*
import uz.fizika.core.database.prepopulate.PhysicsDataSeeder

@Database(
    entities = [
        FormulaEntity::class,
        TopicEntity::class,
        FormulaLinkEntity::class,
        UserProgressEntity::class,
        UserProfileEntity::class,
        GameScoreEntity::class,
        ChatMessageEntity::class,
        TestResultEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun formulaDao(): FormulaDao
    abstract fun topicDao(): TopicDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun gameScoreDao(): GameScoreDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun testResultDao(): TestResultDao

    companion object {
        const val DB_NAME = "umna_fizika.db"

        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Предзаполнение начальными данными (формулы, разделы)
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = Room.databaseBuilder(
                                context, AppDatabase::class.java, DB_NAME
                            ).build()
                            PhysicsDataSeeder.seed(database)
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
