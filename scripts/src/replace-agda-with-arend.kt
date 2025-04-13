import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream

const val agdaMarkdownExt = ".lagda.md"
const val arendExt = ".ard"
const val specialCommentStart = "{-Agda-"
const val specialCommentEnd = "-Agda-}"

fun main() {
    replace(Path.of("plfa.github.io", "src", "plfa", "part1", "Induction.lagda.md"), Path.of("book", "src", "part1", "Induction.ard"))
}

private fun replace(agdaFilesPath: Path, arendFilesPath: Path) {
    println("INFO: processing $agdaFilesPath and $arendFilesPath")
    val agdaFilesStream = if (Files.isDirectory(agdaFilesPath)) Files.list(agdaFilesPath) else Stream.of(agdaFilesPath)
    val agdaMarkdownFiles = agdaFilesStream
        .filter { it.fileName.toString().endsWith(agdaMarkdownExt) }
        .toList()
    println("INFO: found ${agdaMarkdownFiles.size} Agda Markdown files")
    val arendFilesStream = if (Files.isDirectory(arendFilesPath)) Files.list(arendFilesPath) else Stream.of(arendFilesPath)
    val arendFiles = arendFilesStream
        .filter { it.fileName.toString().endsWith(arendExt) }
        .toList()
    println("INFO: found ${arendFiles.size} Arend files")
    val allPairs = agdaMarkdownFiles.map { md ->
        val agdaFileName = md.fileName.toString().removeSuffix(agdaMarkdownExt)
        val ard = arendFiles.find { it.fileName.toString().removeSuffix(arendExt) == agdaFileName }
        md to ard
    }
    allPairs.forEach { (agda, arend) -> if (arend == null) println("WARNING: found no Arend file for $agda") }
    val pairs: List<Pair<Path, Path>> = allPairs.filter { it.second != null }.map { it.first to it.second as Path }
    pairs.forEach { (agda, arend) ->
        val arendFileText = Files.readString(arend)
        val parsedEntries = arendFileText
            .splitToSequence(specialCommentStart)
            .filter { it.isNotBlank() }
            .map { part ->
                val pair = part.split(specialCommentEnd)
                if (pair.size == 2)
                    pair[0].removePrefix(specialCommentStart).trim() to pair[1].removePrefix(specialCommentEnd).trim()
                else null
            }
            .toList()
        if (parsedEntries.any { it == null }) {
            println("WARNING: unexpected layout of special comments in $arend")
            return@forEach
        }
        val parsedPairs = parsedEntries.filterNotNull()
        val agdaFileText = Files.readString(agda)
        val (updatedFileText, _) =
            parsedPairs.fold(agdaFileText to 0) { (fileText, offset), (key, arendCode) ->
                val agdaCode = "```agda\n$key\n```"
                val replacement = """<details><summary>Agda</summary>

```agda
$key
```
</details>

```tex
$arendCode
```"""
                val startOffset = fileText.indexOf(agdaCode, offset)
                if (startOffset == -1) {
                    println("WARNING: the following Agda snippet is not found in $agda:")
                    println(key)
                    println("Corresponding Arend code snippet:")
                    println("Corresponding Arend code snippet:")
                    println(arendCode)
                    return@fold fileText to offset
                }
                val endOffset = startOffset + agdaCode.length
                fileText.replaceRange(startOffset, endOffset, replacement) to startOffset + replacement.length
            }
        Files.writeString(agda, updatedFileText)
        println("INFO: replaced $agda")
    }
}