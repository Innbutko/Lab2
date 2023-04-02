import java.time.LocalDate
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.AnnotationConfigApplicationContext

//Клас, що представляє журнал з відповідними полями
data class Journal(
    val name: String, //ім'я журналу
    val topic: String, //тема журналу
    val language: String, //мова журналу
    val foundingDate: LocalDate, //дата заснування журналу
    val issn: String, //ідентифікаційний код журналу
    val price: Double, //ціна журналу
    val isPeriodic: Boolean, //періодичний чи ні журнал
    var articlesList: MutableList<ScientificArticle> //список наукових статей у журналі
) : Comparable<Journal> {
    override fun compareTo(other: Journal): Int {
        //Сортування за іменем журналу, а якщо імена різні - то за датою заснування.
        return if (this.name == other.name) {
            this.foundingDate.compareTo(other.foundingDate)
        } else {
            this.name.compareTo(other.name)
        }
    }
}

//Клас, що представляє наукову статтю з відповідними полями
data class ScientificArticle(
    val title:String, //заголовок статті
    val author:String, //автор статті
    val dateWritten :LocalDate, //дата написання статті
    val wordCount:Int, //кількість слів у статті
    val referenceCount:Int, //кількість посилань у статті
    val originalLanguage:Boolean //оригінальна мова статті
) : Comparable<ScientificArticle> {
    override fun compareTo(other: ScientificArticle): Int {
        //Сортування за датою написання. Якщо збігається - за заголовком
        return if (this.dateWritten == other.dateWritten) {
            this.title.compareTo(other.title)
        } else {
            this.dateWritten.compareTo(other.dateWritten)
        }
    }

}

//Контейнерний клас, що зберігає список об'єктів типу T.
class EntityContainer<T> : Container<T> {
    val entityList: MutableList<T> = mutableListOf()

    /**
     * Додає елемент у контейнер на вказаний індекс.
     * @param index Індекс, де слід додати елемент.
     * @param element Елемент, що слід додати до контейнера.
     */
    override fun add(index: Int, element: T) {
        entityList.add(index, element)
    }

    /**
     * Видаляє та повертає об'єкт з контейнера за вказаним індексом.
     * @param index Індекс об'єкта, який потрібно видалити з контейнера.
     * @return Видалений об'єкт з контейнера.
     */
    override fun remove(index: Int): T {
        return entityList.removeAt(index)
    }

    /**
     * Оновлює існуючий об'єкт в контейнері новим значенням на певній позиції.
     * @param index Позиція, на якій ми хочемо оновити наші дані
     * @param element Нове значення, яке замінить старе
    **/
    override fun update(index: Int, element: T) {
        entityList[index] = element
    }

    /**
     * Повертає об'єкт, збережений у цій колекції, за його позицією, або викидає IndexOutOfBoundsException, якщо такого елемента немає.
     * @param index	Позиція потрібних даних
     * @return Об'єкт, збережений на вказаній позиції
    **/
    override fun get(index: Int): T {
        return entityList[index]
    }

    /**
     * Повертає всі об'єкти, збережені у цій колекції, як List<T>.
     * @return List, що містить всі елементи, збережені в об'єкті EntityContainer.
     **/
    override fun getAll(): List<T> {
        return entityList.toList()
    }
}

/** Інтерфейс, що визначає основні методи класу контейнера. **/
interface Container<T> {
    fun add(index:Int,element:T)
    fun remove (index:Int):T
    fun update (index:Int,element:T)
    fun get (index:Int):T
    fun getAll (): List <T>
}

/** Конфігураційний клас Spring, що створює екземпляри EntityContainer **/
@Configuration
open class AppConfig {

    @Bean
    open fun journalContainer() : EntityContainer<Journal>{
        return EntityContainer()
    }

    @Bean
    open fun articleContainer() : EntityContainer<ScientificArticle>{
        return EntityContainer()
    }
}

/** Основна функція програми **/
fun main(args: Array<String>) {
    val context = AnnotationConfigApplicationContext(AppConfig::class.java)
    val jrnContainer = context.getBean("journalContainer", EntityContainer::class.java) as EntityContainer<Journal>
    val arcContainer = context.getBean("articleContainer", EntityContainer::class.java) as EntityContainer<ScientificArticle>

    arcContainer.add(0, ScientificArticle("Думи мої", "Т.Г. Шевченко", LocalDate.now(), 100, 10, true))
    arcContainer.add(1, ScientificArticle("Суботній звіт", "В.В. Суботін", LocalDate.now(), 150, 100, true))

    jrnContainer.add(0, Journal("Вісник КПІ", "Життя університету", "Українська", LocalDate.now(), "243-5345", 45.65, true, arcContainer.entityList))
    jrnContainer.add(1, Journal("Підслухано, КПІ", "Життя університету", "Українська", LocalDate.now(), "2543-535", 450.65, false, arcContainer.entityList))
    jrnContainer.add(2, Journal("Новини КПІ", "Життя університету", "Українська", LocalDate.now(), "2643-5354", 65.65, true, arcContainer.entityList))
    jrnContainer.add(3, Journal("Життя Києва", "Новини", "Українська", LocalDate.now(), "2435-5355", 12.54, false, arcContainer.entityList))
    jrnContainer.add(4, Journal("Волонтерський рух", "Новини", "Українська", LocalDate.now(), "2453-5535", 412.32, true, arcContainer.entityList))

    println(jrnContainer.get(3))
    jrnContainer.remove(4)
}