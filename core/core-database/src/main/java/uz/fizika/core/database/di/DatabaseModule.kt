package uz.fizika.core.database.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.fizika.core.database.AppDatabase
import uz.fizika.core.database.dao.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.create(context)

    @Provides fun provideFormulaDao(db: AppDatabase): FormulaDao = db.formulaDao()
    @Provides fun provideTopicDao(db: AppDatabase): TopicDao = db.topicDao()
    @Provides fun provideUserProgressDao(db: AppDatabase): UserProgressDao = db.userProgressDao()
    @Provides fun provideUserProfileDao(db: AppDatabase): UserProfileDao = db.userProfileDao()
    @Provides fun provideGameScoreDao(db: AppDatabase): GameScoreDao = db.gameScoreDao()
    @Provides fun provideChatMessageDao(db: AppDatabase): ChatMessageDao = db.chatMessageDao()
    @Provides fun provideTestResultDao(db: AppDatabase): TestResultDao = db.testResultDao()
}
