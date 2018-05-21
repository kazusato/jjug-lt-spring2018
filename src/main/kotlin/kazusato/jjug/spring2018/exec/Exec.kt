package kazusato.jjug.spring2018.exec

import java.util.concurrent.Executors

fun main(args: Array<String>) {
    val exec = Executors.newFixedThreadPool(2)
    exec.submit {
        for (i in 1..10) {
            println("hello world")
            Thread.sleep(1000)
        }
    }
    exec.submit {
        for (i in 1..20) {
            println("こんにちは、世界")
            Thread.sleep(500)
        }
    }
}