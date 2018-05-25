package kazusato.jjug.spring2018.kubeapi

import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.client.ResponseHandler
import org.slf4j.LoggerFactory
import java.io.InputStreamReader
import javax.json.Json

class SobaOrderChunkedResponseHandler: ResponseHandler<String> {

    companion object {
        @JvmStatic private var logger = LoggerFactory.getLogger(SobaOrderChunkedResponseHandler::class.java)
    }

    override fun handleResponse(resp: HttpResponse?): String? {
        val status = resp?.statusLine?.statusCode ?: throw RuntimeException("HTTP response is null.")

        if (status != HttpStatus.SC_OK) {
            throw RuntimeException("HTTP status: ${status}")
        }

        var resourceVersion: String? = null
        val entity = resp.entity
        while (true) {
            val inputStream = entity.content
            logger.info("Input Stream: ${inputStream}")
            logger.info("Chunked response: ${entity.isChunked}")
            InputStreamReader(inputStream).use {
                resourceVersion = readResourceVersionFromChunk(it)
            }
        }

        return resourceVersion
    }

    private fun readResourceVersionFromChunk(reader: InputStreamReader): String? {
        val jsonReader = Json.createReader(reader)
        var resourceVersion: String? = null
//        while (true) {
            val obj = jsonReader.readObject() //?: break
            logger.info("Chunk: ${obj}")
            val targetObj = obj.getJsonObject("object")
            val metadata = targetObj.getJsonObject("metadata")
            resourceVersion = metadata.getString("resourceVersion")
            logger.info("Resource version: ${resourceVersion}")
//        }
        return resourceVersion
    }

}
