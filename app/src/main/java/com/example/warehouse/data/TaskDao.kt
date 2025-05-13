package com.example.warehouse.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO для работы с таблицей tasks.
 */
@Dao
interface TaskDao {
    /**
     * Вставляет или обновляет запись о приёмке.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceive(task: ReceiveTask)

    /**
     * Возвращает все записи в обратном хронологическом порядке.
     */
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAll(): Flow<List<Task>>

    /**
     * Поиск по номеру заказа (поле order) или чертежу.
     */
    @Query(
        """
        SELECT * FROM tasks
        WHERE `order` LIKE '%' || :query || '%'
           OR drawing LIKE '%' || :query || '%'
        ORDER BY id DESC
        """
    )
    fun search(query: String): Flow<List<Task>>
}
