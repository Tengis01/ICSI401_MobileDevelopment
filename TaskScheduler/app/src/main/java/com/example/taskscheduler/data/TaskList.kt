// ФАЙЛЫН БАЙРШИЛ: java/com/example/taskscheduler/data/TaskList.kt
package com.example.taskscheduler.data

/**
 * Нэг жагсаалтыг (Personal, Work, Study...) төлөөлөх data class
 * @param id       - Өвөрмөц ID
 * @param name     - Жагсаалтын нэр
 * @param iconName - drawable-ийн нэр ("ic_personal", "ic_work", "ic_study", "ic_business")
 * @param taskCount- Энэ жагсаалтад хэдэн даалгавар байгаа (тооцоолсон)
 */
data class TaskList(
    val id: Long = 0,
    val name: String,
    val iconName: String = "ic_personal",
    val taskCount: Int = 0
)