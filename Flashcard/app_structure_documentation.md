# FlashStudy Аппликейшний Бүтэц, Кодын Баримтжуулалт

Энэхүү баримт бичигт FlashStudy аппликейшнд ашиглагдсан бүх `.kt` (Kotlin) файлуудын үүрэг зориулалт болон хамгийн чухал цөм кодын (core code) хэсгүүдийг тайлбарласан болно.

Аппликейшн нь **MVVM (Model-View-ViewModel)**-тэй төстэй архитектурыг **Jetpack Compose** болон **Room Database**-тэй хослуулан ашиглаж бүтээгдсэн.

---

## 1. Өгөгдлийн Давхарга (Data Layer)

Энэ хэсэг нь аппликейшний өгөгдлийн загвар, мэдээллийн сантай харилцах хэсгийг хариуцдаг.

### 1.1 Өгөгдлийн Загварууд (Data Models)
Эдгээр файлууд нь апп доторх үндсэн объектуудын бүтцийг тодорхойлдог.
- **`data/Deck.kt`**: Картын багцын (Deck) мэдээллийг хадгалах загвар (`id`, `name`, `cards` г.м).
- **`data/FlashCard.kt`**: Нэг ширхэг флаш картын мэдээллийг хадгалах загвар (`term`, `definition`, `leitnerBox` г.м).
- **`data/Folder.kt`**: Хавтсын (Folder) мэдээллийг хадгалах загвар (`name`, `deckIds` г.м).

### 1.2 Room Database (Local Storage)
Дотоод мэдээллийн санд өгөгдөл хадгалах хэсэг.
- **`data/local/Entities.kt`**: Өгөгдлийн загваруудыг (Deck, FlashCard, Folder) SQLite мэдээллийн санд хадгалах хүснэгт (table) болгон хувиргасан Room Entities. Түүнчлэн `Folder` болон `Deck`-ийн хоорондох Many-to-Many хамаарлыг `FolderDeckCrossRef`-ээр шийдсэн.
- **`data/local/DeckDao.kt`**: Мэдээллийн сантай шууд харилцах (CRUD буюу Create, Read, Update, Delete) үйлдлүүдийг агуулсан Data Access Object (DAO) интерфэйс. SQL query-нүүд энд бичигдсэн байдаг.
- **`data/local/FlashStudyDatabase.kt`**: Room мэдээллийн сангийн үндсэн тохиргоо, хувилбар (version), болон DAO-нуудыг зарласан хийсвэр (abstract) класс.

### 1.3 Repository
- **`data/DeckRepository.kt`**: UI (Дэлгэцүүд) болон Өгөгдлийн сан (DAO) хоёрын дунд гүүр болж ажилладаг класс. Дэлгэцүүд шууд мэдээллийн сантай харьцахын оронд энэ Repository-ээр дамжуулж өгөгдлөө авч, хадгалдаг.

---

## 2. UI болон Navigation (Дэлгэцүүд)

Энэ хэсэг нь хэрэглэгчид харагдах интерфейс (Jetpack Compose) болон дэлгэц хоорондын шилжилтүүдийг хариуцна.

### 2.1 Үндсэн Файлууд
- **`MainActivity.kt`**: Аппликейшний хамгийн анх ачааллах цэг (Entry point). Ганцхан Activity ашигласан бөгөөд үүн дотор мэдээллийн санг (Room) үүсгэж, `AppNavigation`-ийг дуудаж эхлүүлдэг.
- **`navigation/AppNavigation.kt`**: Аппликейшний бүх дэлгэцүүдийн жагсаалт, дэлгэц хооронд хэрхэн шилжихийг (Routing) зохицуулна. Доод талын "Bottom Navigation Bar" энд зурагддаг.

### 2.2 Дэлгэцүүд (Screens)
- **`ui/screens/HomeScreen.kt`**: Нүүр дэлгэц. Хамгийн сүүлд давтсан багцууд болон нийт явцыг харуулна.
- **`ui/screens/LibraryScreen.kt`**: Номын сан. Бүх хавтас болон картын багцуудын жагсаалтыг харуулж, хайлт хийх боломжтой.
- **`ui/screens/CreateEditDeckScreen.kt`**: Шинээр багц үүсгэх эсвэл засах дэлгэц. Багцын нэр, тайлбар бичих болон картууд нэмэх боломжтой.
- **`ui/screens/CardEditorScreen.kt`**: Нэг ширхэг флаш картын нэр томьёо (term) болон тайлбарыг (definition) оруулж, засах дэлгэц.
- **`ui/screens/CreateFolderScreen.kt`**: Шинэ хавтас үүсгэх дэлгэц.
- **`ui/screens/FolderDetailScreen.kt`**: Хавтас доторх багцуудын жагсаалтыг харуулах дэлгэц. Эндээс хавтас руу шинэ багц нэмж болно.
- **`ui/screens/DeckDetailScreen.kt`**: Тодорхой нэг картын багцын дэлгэрэнгүй мэдээлэл. "Карт тоглуулагч" болон "Цээжлэх" горим руу орох товчнууд энд бий.
- **`ui/screens/FlashcardScreen.kt`**: **Карт тоглуулагч (Swipe & Flip).** Аппликейшний хамгийн чухал дэлгэцүүдийн нэг. Картыг баруун, зүүн тийш чирж (swipe) сурсан эсэхээ тэмдэглэнэ.
- **`ui/screens/LearnScreen.kt`**: Олон сонголттой асуулт (Quiz) хэлбэрээр цээжлэх горим.
- **`ui/screens/StatsScreen.kt`**: Картын багцын сургалтын статистик (Leitner Box-ийн түвшингээр хэдэн карт байгаа) мэдээллийг харуулна.

### 2.3 Дизайн болон Загвар (Theme)
- **`ui/theme/Color.kt`**: Аппликейшнд ашиглагдах бүх өнгөнүүдийн тогтмолууд (Primary, Surface, Success г.м).
- **`ui/theme/Type.kt`**: Фонт, үсгийн хэв маяг болон хэмжээнүүдийн тохиргоо (Typography).
- **`ui/theme/Theme.kt`**: Өнгө, фонтуудыг нэгтгэн `FlashStudyTheme` гэсэн үндсэн загварыг үүсгэнэ. `GradientBackground` зэрэг ерөнхий суурь дизайнууд энд бичигдсэн.

---

## 3. Цөм Кодын Тайлбар (Core Logic)

Аппликейшний хамгийн онцлох, чухал кодын хэсгүүдийн тайлбар:

### 3.1 Swipe Mechanics (FlashcardScreen.kt)
Картыг баруун (Мэдсэн) эсвэл зүүн (Давтах) тийш чирж шийдвэр гаргах логик. `pointerInput` болон `Animatable` ашигласан.

```kotlin
val offsetX = remember { Animatable(0f) }
val offsetY = remember { Animatable(0f) }

// ... (UI дотор)
Modifier.pointerInput(Unit) {
    detectDragGestures(
        onDragEnd = {
            if (offsetX.value > threshold) {
                swipeCard(1) // Баруун тийш чирсэн (Know)
            } else if (offsetX.value < -threshold) {
                swipeCard(-1) // Зүүн тийш чирсэн (Review)
            } else {
                // Хангалттай хол чирээгүй бол гол руу нь буцаах
                scope.launch { offsetX.animateTo(0f) }
                scope.launch { offsetY.animateTo(0f) }
            }
        },
        onDrag = { change, dragAmount ->
            change.consume()
            scope.launch {
                // Картыг хуруу дагаж хөдөлгөх
                offsetX.snapTo(offsetX.value + dragAmount.x)
                offsetY.snapTo(offsetY.value + dragAmount.y)
            }
        }
    )
}
```

### 3.2 3D Card Flip (FlashcardScreen.kt)
Картыг тогшиход эргэх анимац. `graphicsLayer` дотор `rotationY` ашиглаж хийгдсэн бөгөөд урд тал (Term), ард тал (Definition) солигддог. Текст толь шиг урвуу харагдахаас сэргийлж ард талд `rotation - 180f` ашигласан.

```kotlin
// Урд талын карт (Term)
if (rotation <= 90f) {
    Card(
        modifier = Modifier.graphicsLayer {
            rotationY = rotation
            cameraDistance = 12f * densityValue
        }
    ) { /* Нэр томьёо */ }
} 
// Ард талын карт (Definition)
else {
    Card(
        modifier = Modifier.graphicsLayer { 
            rotationY = rotation - 180f // Урвуу харагдахаас сэргийлнэ
            cameraDistance = 12f * densityValue
        }
    ) { /* Тайлбар */ }
}
```

### 3.3 Leitner System Update (FlashcardScreen.kt)
Хэрэглэгч картнаас "Мэдсэн" (Баруун) эсвэл "Давтах" (Зүүн) гэж сонгоход картын сурсан түвшинг (Leitner Box) нэмэгдүүлэх эсвэл бууруулах логик.

```kotlin
fun swipeCard(direction: Int) { 
    scope.launch {
        if (direction > 0) { // Баруун (Know)
            currentCard?.let { card ->
                // Дээд тал нь 5-р хайрцагт хүрнэ
                val newBox = minOf(5, card.leitnerBox + 1) 
                val updated = card.copy(leitnerBox = newBox, needsReview = false)
                repository.updateCard(deckId, updated)
            }
        } else { // Зүүн (Review)
            currentCard?.let { card ->
                // Мэдэхгүй бол буцаад 1-р хайрцаг руу унана
                val updated = card.copy(leitnerBox = 1, needsReview = true)
                repository.updateCard(deckId, updated)
            }
        }
        nextCard()
    }
}
```

### 3.4 Room Database Many-to-Many Хамаарал (Entities.kt)
Хавтас (Folder) болон Багц (Deck) хоёрын дунд аль аль нь олон утга агуулж болох олон-олон хамаарал үүсгэсэн. Ингэснээр нэг багц олон хавтаст орж болно.

```kotlin
@Entity(
    tableName = "folder_deck_cross_ref",
    primaryKeys = ["folderId", "deckId"]
    // ... Foreign keys ...
)
data class FolderDeckCrossRef(
    val folderId: String,
    val deckId: String
)

data class FolderWithDecks(
    @Embedded val folder: FolderEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = FolderDeckCrossRef::class,
            parentColumn = "folderId",
            entityColumn = "deckId"
        )
    )
    val decks: List<DeckEntity>
)
```

Энэхүү бүтэц нь цаашид аппликейшнийг өргөжүүлэх (Scale хийх), шинэ боломжууд нэмэхэд маш уян хатан, цэвэр кодны стандартыг хангасан байдлаар зохиогдсон.
