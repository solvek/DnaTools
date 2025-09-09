import com.solvek.kotlindnascripts.tools.spovidky
import com.solvek.kotlindnascripts.tools.tree2plain

fun main(args: Array<String>) {
    if (args.isEmpty()){
        println("No tool name specified")
        return
    }

    val tool = args[0]

    when(tool) {
        "tree2plain" -> tree2plain()
        "spovidky" -> spovidky()
        else -> {
            println("Unknown tool $tool")
            return
        }
    }

    println("All finished")
}
