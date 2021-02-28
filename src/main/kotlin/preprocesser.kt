import java.io.File


fun preprocess(input: String): String {
    return input.split("\n").map {
        if(it.contains("#")) {
            val (line, comment) = it.split("#", limit = 2)
            if(comment.startsWith("include")) {
                val file = File(comment.drop("include".length).trim())
                println("reading from file $file")
                if(line.isNotBlank()) error("can't #include on a line with content")
                return@map preprocess(file.readText())
            } else line
        } else it
    }.joinToString("\n","","")
}