package kazusato.jjug.spring2018.kubeapi

import org.glassfish.jersey.client.ChunkedInput
import org.slf4j.LoggerFactory
import java.io.StringReader
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.LinkedBlockingQueue
import javax.json.Json
import javax.json.JsonObject
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.core.GenericType

fun main(args: Array<String>) {
    SobaOrderController().callKubeApi()
}

class SobaOrderController {

    private val queue = LinkedBlockingQueue<JsonObject>()

    private val client = ClientBuilder.newClient()

    companion object {
        @JvmStatic private val logger = LoggerFactory.getLogger(SobaOrderController::class.java)
    }

    fun callKubeApi() {
        logger.info("Connecting Kubernetes API server through localhost:8001.")

        while (true) {
            val resourceVersion = initialQuery()
            watchQuery(resourceVersion)

            Thread.sleep(10_000)
            logger.info("Watch query terminated: sleep 10 seconds and will retry.")
        }
    }

    private fun initialQuery(): String {
        val target = client.target("http://localhost:8001")
                .path("apis/kazusato.local/v1alpha1/sobaorders")
        val resp = target.request().get()

        logger.info("Response: ${resp}")
        val jsonStr = resp.readEntity(String::class.java)
        logger.info("Body: ${jsonStr}")

        val resourceVersion = readResourceVersion(jsonStr)
        logger.info("Resource version: ${resourceVersion}")
//        queue.push(jsonStr)

        return resourceVersion
    }

    private fun watchQuery(resourceVersion: String) {
        val target = client.target("http://localhost:8001")
                .path("apis/kazusato.local/v1alpha1/sobaorders")
                .queryParam("resourceVersion", resourceVersion)
                .queryParam("watch", "1")
        val resp = target.request().get()

        val chunkedInput = resp.readEntity(object : GenericType<ChunkedInput<String>>() {})
        while (true) {
            val chunk = chunkedInput.read() ?: break
            logger.info("Chunk: ${chunk}")
        }
    }

    private fun pushForInitialQuery(jsonStr: String) {

    }

    private fun clearQueue() {

    }

    private fun readResourceVersion(jsonStr: String): String {
        val reader = Json.createReader(StringReader(jsonStr))
        val obj = reader.readObject()
        val metadata = obj.getJsonObject("metadata")
        return metadata.getString("resourceVersion")
    }

}