package kazusato.jjug.spring2018.kubeapi

import org.glassfish.jersey.client.ChunkedInput
import org.glassfish.jersey.client.ClientConfig
import org.glassfish.jersey.logging.LoggingFeature
import org.slf4j.LoggerFactory
import java.io.StringReader
import java.util.concurrent.LinkedBlockingQueue
import java.util.logging.Level
import javax.json.Json
import javax.json.JsonObject
import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.Response

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
        logger.info("Connect to the Kubernetes API server through localhost:8001.")

        var resourceVersion = initialQuery()
        while (true) {
            logger.info("Start watching.")
            resourceVersion = watchQuery(resourceVersion)
            logger.info("Watching terminated.")
        }
    }

    private fun initialQuery(): String {
        val target = client.target("http://localhost:8001")
                .path("apis/kazusato.local/v1alpha1/sobaorders")
        val resp = target.request().get()
        logger.info("Response: ${resp}")

        if (resp.status != Response.Status.OK.statusCode) {
            throw RuntimeException("Response status: ${resp.status}")
        }

        val jsonStr = resp.readEntity(String::class.java)
        logger.info("Body: ${jsonStr}")

        val resourceVersion = readResourceVersion(jsonStr)
        logger.info("Resource version: ${resourceVersion}")

        return resourceVersion
    }

    private fun watchQuery(resourceVersion: String): String {
        val target = client.target("http://localhost:8001")
                .path("apis/kazusato.local/v1alpha1/sobaorders")
                .queryParam("resourceVersion", resourceVersion)
                .queryParam("watch", "1")
        val resp = target.request().get()

        val chunkedInput = resp.readEntity(object : GenericType<ChunkedInput<String>>() {})
        var newResourceVersion = resourceVersion
        while (true) {
            val chunk: String = chunkedInput.read() ?: break
            newResourceVersion = readResourceVersionFromChunk(chunk)
            logger.info("Chunk: ${chunk}")
            // FIXME chunkを受け取ると、その直後に必ずHTTPがいったん終了するのはなぜ？
            // FIXME なぜかcurlだとYAML投入後一瞬で受信するchunkが、ここだと30秒遅れる。
        }

        return newResourceVersion
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

    private fun readResourceVersionFromChunk(jsonStr: String): String {
        // FIXME 複数のJSONが{...}{..1}{...}と1つのchunkに含まれている場合に対応できていない。
        val reader = Json.createReader(StringReader(jsonStr))
        val obj = reader.readObject()
        val targetObj = obj.getJsonObject("object")
        val metadata = targetObj.getJsonObject("metadata")
        return metadata.getString("resourceVersion")
    }

}