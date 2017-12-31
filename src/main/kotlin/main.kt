import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import java.nio.file.Paths

@JsonIgnoreProperties(ignoreUnknown = true)
data class Tractate(
        @JsonProperty("text") val chapters: List<RawChapter>,
        @JsonProperty("heTitle") val title: String
)

typealias RawChapter = List<Verse>
typealias Verse = String

data class Chapter(val tractate: String, val numChapter: Int, val initials: String)

val mapper = jacksonObjectMapper()

fun main(args: Array<String>) {
    val dir = "src/main/resources/sefaria/merged"
    val word = "נשמה"

    val tractates = File(dir).walk()
            .filter { it.name == "merged.json" }
            .map {
                mapper.readValue<Tractate>(it.inputStream())
            }

    val chapters = tractates
            .map { tractate ->
                tractate.chapters.mapIndexed { numChapter, verses ->
                    Chapter(tractate.title,
                            numChapter + 1,
                            verses.map { verse -> verse[0] }.joinToString(""))
                }
            }.flatten()

    println("chapters: ${chapters.count()}")

    val numVerses = chapters
            .map {
                it.initials.length
            }
            .sum()

    println("verses: $numVerses")

    chapters
            .filter {
                word in it.initials
            }
            .forEach {
                println("neshamah: tractate: ${it.tractate}, chapter ${it.numChapter}, verse: ${it.initials.indexOf(word) + 1}")
            }
}