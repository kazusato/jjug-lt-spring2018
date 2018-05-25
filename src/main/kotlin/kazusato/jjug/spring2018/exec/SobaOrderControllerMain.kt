package kazusato.jjug.spring2018.exec

import kazusato.jjug.spring2018.kubeapi.SobaOrderControllerOkhttp
import kazusato.jjug.spring2018.phone.SobaOrderTwillioCaller
import org.slf4j.LoggerFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    val exec: ExecutorService = Executors.newFixedThreadPool(2)
    val controller = SobaOrderControllerOkhttp()
    val queue = controller.queue
    val caller = SobaOrderTwillioCaller(queue)

    val logger = LoggerFactory.getLogger("Main")

    try {
        exec.submit {
            try {
                controller.callKubeApi()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        exec.submit {
            try {
                caller.watchQueue()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    } finally {
        logger.info("shutdown start")
        exec.shutdown()
        if(exec.awaitTermination(10, TimeUnit.SECONDS)) {
            exec.shutdownNow()
        }
    }
}

