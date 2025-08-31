package com.app.navgurukultask.di
import android.content.Context
import androidx.room.Room
import com.app.navgurukultask.data.local.database.AppDatabase
import com.app.navgurukultask.data.local.dao.StudentDao
import com.app.navgurukultask.data.local.dao.StudentScorecardDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideStudentDao(db: AppDatabase): StudentDao = db.studentDao()

    @Provides
    @Singleton
    fun provideScoreCardDao(db: AppDatabase): StudentScorecardDao = db.scoreCardDao()
}
