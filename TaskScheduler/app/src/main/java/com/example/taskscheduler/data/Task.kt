package com.example.taskscheduler.data

/**
 * Нэг даалгаврыг төлөөлөх data class
 * @param id       - Өвөрмөц ID (SQLite автоматаар үүсгэнэ)
 * @param title    - Даалгаврын нэр
 * @param listId   - Аль жагсаалтад харьяалагдахыг илэрхийлнэ
 * @param section  - "today", "upcoming", "someday"
 * @param date     - Огноо (yyyy-MM-dd форматаар, хоосон байж болно)
 * @param time     - Цаг (HH:mm форматаар, хоосон байж болно)
 * @param isDone   - Биелсэн эсэх
 */
data class Task(
    val id: Long = 0,
    val title: String,
    val listId: Long,
    val section: String = SECTION_SOMEDAY,
    val date: String = "",
    val time: String = "",
    val endTime : String = "",
    val isDone: Boolean = false
) {
    companion object {
        const val SECTION_TODAY    = "today"
        const val SECTION_UPCOMING = "upcoming"
        const val SECTION_SOMEDAY  = "someday"
    }
}
