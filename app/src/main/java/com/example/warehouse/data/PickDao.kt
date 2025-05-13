package com.example.warehouse.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PickDao {
    // Загрузить все задачи
    @Query("SELECT * FROM pick_tasks ORDER BY drawing")
    fun getTasks(): Flow<List<PickTask>>

    // Обновить статус задач
    @Query("UPDATE pick_tasks SET completed = 1 WHERE drawing = :drawing")
    suspend fun markCompleted(drawing: String)

    // Вставить/обновить задачи после загрузки из Excel
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTasks(tasks: List<PickTask>)

    // Лог выдач
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLog(entry: PickLog)

    @Query("SELECT * FROM pick_log ORDER BY id DESC")
    fun getLog(): Flow<List<PickLog>>
}
