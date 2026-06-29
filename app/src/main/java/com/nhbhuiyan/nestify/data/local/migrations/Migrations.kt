package com.nhbhuiyan.nestify.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room migrations for the Nestify database.
 *
 * Historically the database used `fallbackToDestructiveMigration()`, which wiped ALL local
 * data (notes, grades, CT marks, schedules…) on every version bump. That is unacceptable once
 * real student data exists. From v19 onward we ship explicit migrations instead.
 *
 * IMPORTANT: each migration's generated schema must match EXACTLY what Room derives from the
 * `@Entity` classes (column order/types, NOT NULL, FK clauses, index names). Room validates the
 * resulting schema on open and throws if it differs. Note that Kotlin default values
 * (e.g. `repeatCount: Int = 1`) are applied in code, NOT as SQL `DEFAULT` clauses — so the
 * CREATE TABLE below intentionally has no DEFAULT clauses, mirroring Room's own generation.
 */

/**
 * 18 → 19: introduces the `pyqs` table (PYQEntity), previously created implicitly via the
 * destructive fallback. Uses IF NOT EXISTS so it is a safe no-op on devices that already created
 * the table under the old fallback path.
 */
val MIGRATION_18_19 = object : Migration(18, 19) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `pyqs` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`topicId` INTEGER NOT NULL, " +
                "`questionText` TEXT, " +
                "`questionImagePath` TEXT, " +
                "`answerText` TEXT, " +
                "`answerImagePath` TEXT, " +
                "`nbFormulas` TEXT, " +
                "`nbTheories` TEXT, " +
                "`nbConstants` TEXT, " +
                "`nbExtras` TEXT, " +
                "`repeatCount` INTEGER NOT NULL, " +
                "`yearsSeen` TEXT NOT NULL, " +
                "`marks` TEXT, " +
                "FOREIGN KEY(`topicId`) REFERENCES `syllabus_topics`(`id`) " +
                "ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_pyqs_topicId` ON `pyqs` (`topicId`)")
    }
}

/**
 * 19 → 20: adds the `firestoreId` column to both `syllabus_topics` and `pyqs` tables.
 */
val MIGRATION_19_20 = object : Migration(19, 20) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `syllabus_topics` ADD COLUMN `firestoreId` TEXT")
        db.execSQL("ALTER TABLE `pyqs` ADD COLUMN `firestoreId` TEXT")
    }
}

/** Ordered list of all migrations; pass to `Room.databaseBuilder(...).addMigrations(*ALL_MIGRATIONS)`. */
val ALL_MIGRATIONS: Array<Migration> = arrayOf(
    MIGRATION_18_19,
    MIGRATION_19_20,
)
