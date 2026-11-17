package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DailyRecordRepository(private val dao: DailyRecordDao) {
    private val updateMutex = Mutex()

    fun observeRecord(dateKey: String): Flow<DailyRecordEntity> {
        return dao.observeRecord(dateKey).map { existing ->
            existing ?: DailyRecordEntity(dateKey = dateKey)
        }
    }

    suspend fun toggleMorning(dateKey: String) {
        updateRecord(dateKey) { current ->
            current.copy(
                morningTaken = !current.morningTaken,
                updatedAtMillis = System.currentTimeMillis()
            )
        }
    }

    suspend fun toggleLunch(dateKey: String) {
        updateRecord(dateKey) { current ->
            current.copy(
                lunchTaken = !current.lunchTaken,
                updatedAtMillis = System.currentTimeMillis()
            )
        }
    }

    suspend fun toggleDinner(dateKey: String) {
        updateRecord(dateKey) { current ->
            current.copy(
                dinnerTaken = !current.dinnerTaken,
                updatedAtMillis = System.currentTimeMillis()
            )
        }
    }

    suspend fun incrementWater(dateKey: String) {
        updateRecord(dateKey) { current ->
            current.copy(
                waterCups = current.waterCups + 1,
                updatedAtMillis = System.currentTimeMillis()
            )
        }
    }

    suspend fun decrementWater(dateKey: String) {
        updateRecord(dateKey) { current ->
            current.copy(
                waterCups = (current.waterCups - 1).coerceAtLeast(0),
                updatedAtMillis = System.currentTimeMillis()
            )
        }
    }

    suspend fun clearStaleRecordsIfNeeded(todayDateKey: String) {
        updateMutex.withLock {
            dao.deleteRecordsNotForDate(todayDateKey)
        }
    }

    private suspend fun updateRecord(
        dateKey: String,
        transform: (DailyRecordEntity) -> DailyRecordEntity,
    ) {
        updateMutex.withLock {
            val current = dao.getRecord(dateKey) ?: DailyRecordEntity(dateKey = dateKey)
            dao.upsertRecord(transform(current))
        }
    }
}
