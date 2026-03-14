package com.example.taskscheduler.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        // Database-iin ner
        private const val DATABASE_NAME = "task_vault.db"

        // Database-iin huvilbar - end_time bagana nemsen uchir 2 bolgow
        private const val DATABASE_VERSION = 2

        // task_lists husnegtiin ner
        const val TABLE_LISTS = "task_lists"

        // task_lists husnegtiin baganuud
        const val COL_LIST_ID = "id"
        const val COL_LIST_NAME = "name"
        const val COL_LIST_ICON = "icon_name"

        // tasks husnegtiin ner
        const val TABLE_TASKS = "tasks"

        // tasks husnegtiin baganuud
        const val COL_TASK_ID = "id"
        const val COL_TASK_TITLE = "title"
        const val COL_TASK_LIST_ID = "list_id"
        const val COL_TASK_SECTION = "section"
        const val COL_TASK_DATE = "date"
        const val COL_TASK_TIME = "time"
        const val COL_TASK_ENDTIME = "end_time"   // duusah tsag - zaildaa hoosoon baij bolno
        const val COL_TASK_DONE = "is_done"
    }

    // Database anh udaa uusgeh ued ene function ajillana
    // End task list bolon task husnegtuudiig uusgene
    override fun onCreate(db: SQLiteDatabase) {

        // Task list hadgalah husnegt uusgej baina
        // End list-iin id, ner, icon-iin ner hadgalagdana
        db.execSQL("""
            CREATE TABLE $TABLE_LISTS (
                $COL_LIST_ID   INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_LIST_NAME TEXT NOT NULL,
                $COL_LIST_ICON TEXT DEFAULT 'ic_personal'
            )
        """.trimIndent())

        // Task hadgalah husnegt uusgej baina
        // End task-iin garchig, yamar list-d hamaarah, heseg, ognoo, tsag, hiigdsen esehiig hadgalna
        db.execSQL("""
            CREATE TABLE $TABLE_TASKS (
                $COL_TASK_ID      INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TASK_TITLE   TEXT NOT NULL,
                $COL_TASK_LIST_ID INTEGER NOT NULL,
                $COL_TASK_SECTION TEXT DEFAULT 'someday',
                $COL_TASK_DATE    TEXT DEFAULT '',
                $COL_TASK_TIME    TEXT DEFAULT '',
                $COL_TASK_ENDTIME TEXT DEFAULT '',
                $COL_TASK_DONE    INTEGER DEFAULT 0,
                FOREIGN KEY ($COL_TASK_LIST_ID) REFERENCES $TABLE_LISTS($COL_LIST_ID)
            )
        """.trimIndent())

        // Database anh uushees hooson baihgui bailgahyn tuld undsen 3 list-iig nemej baina
        insertDefaultLists(db)
    }

    // Database-iin huvilbar oorchlogdvol ene function ajillana
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        // version 1-ees 2 ruu shiljihед end_time bagana nemne
        // DROP hiihgui - oridiin data hadgalagdaj uldene
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_TASKS ADD COLUMN $COL_TASK_ENDTIME TEXT DEFAULT ''")
        }
    }

    // App anh ashiglagdah ued undsen list-uudiig database ruu nemeh function
    private fun insertDefaultLists(db: SQLiteDatabase) {

        // Anhnaasaa belen baih list-uudiin ner bolon icon-iig todorhoilj baina
        val lists = listOf(
            Pair("Хувийн", "ic_personal"),
            Pair("Ажил", "ic_business"),
            Pair("Судалгаа", "ic_study")
        )

        // List buriig neg negeer ni database ruu hadgalj baina
        lists.forEach { (name, icon) ->

            // Husnegt ruu oruulah utguudiig tur hadgalah object
            val cv = ContentValues().apply {
                put(COL_LIST_NAME, name)
                put(COL_LIST_ICON, icon)
            }

            // Neg list-iig task_lists husnegt ruu oruulj baina
            db.insert(TABLE_LISTS, null, cv)
        }
    }

    // Database dotor baigaa buh list-iig avah function
    // Task count gedeg ni hiigeegui task-iin toog hamt gargaj baina
    fun getAllLists(): List<TaskList> {

        // Avsan list-uudiig hadgalah hooson jagsaalt
        val lists = mutableListOf<TaskList>()

        // Unshih erhtei database object avna
        val db = readableDatabase

        // SQL query ashiglaad buh list-iig task-iin tootoi ni hamt avch baina
        // LEFT JOIN ashiglasnaar task baihgui list ch gesen haragdana
        // COUNT(t.id) ashiglaad neg list deer heden hiigeegui task baigaag toolj baina
        val cursor = db.rawQuery("""
            SELECT l.$COL_LIST_ID, l.$COL_LIST_NAME, l.$COL_LIST_ICON,
                   COUNT(t.$COL_TASK_ID) as task_count
            FROM $TABLE_LISTS l
            LEFT JOIN $TABLE_TASKS t ON l.$COL_LIST_ID = t.$COL_TASK_LIST_ID
                AND t.$COL_TASK_DONE = 0
            GROUP BY l.$COL_LIST_ID
            ORDER BY l.$COL_LIST_ID ASC
        """.trimIndent(), null)

        // Cursor dotorh mur buriig unshaad TaskList object bolgoj jagsaalt ruu nemej baina
        while (cursor.moveToNext()) {
            lists.add(
                TaskList(
                    id = cursor.getLong(0),
                    name = cursor.getString(1),
                    iconName = cursor.getString(2),
                    taskCount = cursor.getInt(3)
                )
            )
        }

        // Cursor-iig хааж baina
        cursor.close()

        // Belen bolson list-uudiig butsaana
        return lists
    }

    // Shine list database ruu nemeh function
    fun insertList(taskList: TaskList): Long {

        // Bichih erhtei database object avna
        val db = writableDatabase

        // Husnegt ruu oruulah utguudiig beldej baina
        val cv = ContentValues().apply {
            put(COL_LIST_NAME, taskList.name)
            put(COL_LIST_ICON, taskList.iconName)
        }

        // Shine list-iig database ruu oruulaad insert hiisen muriin id-g butsaana
        return db.insert(TABLE_LISTS, null, cv)
    }

    // Songogdson list-iig ustgah function
    // Ene list dotorh task-uudiig mun hamt ustgana
    fun deleteList(listId: Long) {

        // Bichih erhtei database object avna
        val db = writableDatabase

        // Ehleed ene list-d hamaarah task-uudiig ustgana
        db.delete(TABLE_TASKS, "$COL_TASK_LIST_ID = ?", arrayOf(listId.toString()))

        // Daraa ni list-uuriig ni ustgana
        db.delete(TABLE_LISTS, "$COL_LIST_ID = ?", arrayOf(listId.toString()))
    }

    // Database dotor baigaa buh task-iig avah function
    fun getAllTasks(): List<Task> {

        // Avsan task-uudiig hadgalah hooson jagsaalt
        val tasks = mutableListOf<Task>()

        // Unshih erhtei database object avna
        val db = readableDatabase

        // tasks husnegtees buh muruudiig avna
        // Ognoogoor ni ehleed erembeleed, daraa ni ehleh tsagaar ni eremblej baina
        val cursor = db.query(
            TABLE_TASKS,
            null,
            null,
            null,
            null,
            null,
            "$COL_TASK_DATE ASC, $COL_TASK_TIME ASC"
        )

        // Cursor dotorh mur buriig Task object bolgoj jagsaalt ruu nemej baina
        while (cursor.moveToNext()) {
            tasks.add(cursorToTask(cursor))
        }

        // Cursor-iig haana
        cursor.close()

        // Belen task-uudiig butsaana
        return tasks
    }

    // Tuhain list-d hamaarah task-uudiig avah function
    fun getTasksByList(listId: Long): List<Task> {

        // Avsan task-uudiig hadgalah hooson jagsaalt
        val tasks = mutableListOf<Task>()

        // Unshih erhtei database object avna
        val db = readableDatabase

        // listId-taig ni taarsan task-uudiig database-aas shuulgej avna
        val cursor = db.query(
            TABLE_TASKS,
            null,
            "$COL_TASK_LIST_ID = ?",
            arrayOf(listId.toString()),
            null,
            null,
            "$COL_TASK_ID ASC"
        )

        // Cursor dotorh mur buriig Task object bolgoj jagsaalt ruu nemej baina
        while (cursor.moveToNext()) {
            tasks.add(cursorToTask(cursor))
        }

        // Cursor-iig haana
        cursor.close()

        // Tuhain list-iin task-uudiig butsaana
        return tasks
    }

    // Shine task database ruu nemeh function
    fun insertTask(task: Task): Long {

        // Bichih erhtei database object avna
        val db = writableDatabase

        // Husnegt ruu oruulah task-iin medeelliig beldej baina
        val cv = ContentValues().apply {
            put(COL_TASK_TITLE, task.title)
            put(COL_TASK_LIST_ID, task.listId)
            put(COL_TASK_SECTION, task.section)
            put(COL_TASK_DATE, task.date)
            put(COL_TASK_TIME, task.time)
            put(COL_TASK_ENDTIME, task.endTime)   // shine nemegdsen duusah tsag
            put(COL_TASK_DONE, if (task.isDone) 1 else 0)
        }

        // Shine task-iig database ruu oruulaad insert hiisen muriin id-g butsaana
        return db.insert(TABLE_TASKS, null, cv)
    }

    // Task hiigdsen esehiin tuluv-g oorchloh function
    fun toggleTaskDone(taskId: Long, isDone: Boolean) {

        // Bichih erhtei database object avna
        val db = writableDatabase

        // is_done baganad hadgalah shine utgiig beldej baina
        // true bol 1, false bol 0 bolgoj hadgalna
        val cv = ContentValues().apply {
            put(COL_TASK_DONE, if (isDone) 1 else 0)
        }

        // Tuhain id-tai task-iin hiigdsen esehiig shinechilj baina
        db.update(TABLE_TASKS, cv, "$COL_TASK_ID = ?", arrayOf(taskId.toString()))
    }

    // Songogdson task-iig database-aas ustgah function
    fun deleteTask(taskId: Long) {

        // Bichih erhtei database object avna
        val db = writableDatabase

        // Tuhain id-tai task-iig ustgana
        db.delete(TABLE_TASKS, "$COL_TASK_ID = ?", arrayOf(taskId.toString()))
    }

    // Shine task-iin tsag oor task-uudiin tsagiin zavsart davkhtsaj baina esehiig shalgah function
    // true = zovshuurch bolno, false = davkhtsaj baina
    fun isTimeAvailable(date: String, startTime: String, endTime: String, excludeId: Long = -1): Boolean {

        // tsag oruulgaagui task-iig shalgakhgui
        if (date.isEmpty() || startTime.isEmpty()) return true

        // Tuhain ognootoi, tsagtai, duusah tsagtai buh task-uudiig avna
        val tasks = getAllTasks().filter {
            it.date == date &&
                    it.time.isNotEmpty() &&
                    it.endTime.isNotEmpty() &&
                    it.id != excludeId
        }

        // Shine task-iin ehleh tsagiig minutaar huvirgana
        val newStart = timeToMinutes(startTime)

        for (task in tasks) {
            val existStart = timeToMinutes(task.time)
            val existEnd   = timeToMinutes(task.endTime)

            // shine ehleh tsag oor task-iin ehlel duusgaliin zavsart orj baina uu
            if (newStart in existStart until existEnd) return false

            // duusah tsag mun songogdson bol interval davkhtsalt shalgana
            if (endTime.isNotEmpty()) {
                val newEnd = timeToMinutes(endTime)
                if (newStart < existEnd && newEnd > existStart) return false
            }
        }

        return true
    }

    // "09:30" formatтай tsagiig 570 gej minutaar huvirgah tuslah function
    private fun timeToMinutes(time: String): Int {
        if (time.isEmpty()) return -1
        val parts = time.split(":")
        return parts[0].toInt() * 60 + parts[1].toInt()
    }

    // Cursor dotorh neg muriin medeelliig unshaad Task object bolgoj huvirgah function
    // Ene ni davtagdaj ashiglagdah tul tusad ni gargasan
    private fun cursorToTask(cursor: android.database.Cursor): Task {
        return Task(
            id      = cursor.getLong(cursor.getColumnIndexOrThrow(COL_TASK_ID)),
            title   = cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_TITLE)),
            listId  = cursor.getLong(cursor.getColumnIndexOrThrow(COL_TASK_LIST_ID)),
            section = cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_SECTION)),
            date    = cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_DATE)),
            time    = cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_TIME)),
            endTime = cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_ENDTIME)),
            isDone  = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TASK_DONE)) == 1
        )
    }
}