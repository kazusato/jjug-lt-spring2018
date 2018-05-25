package kazusato.jjug.spring2018.kubeapi

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.StringReader
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import javax.json.Json
import javax.json.JsonObject

class SobaOrderControllerOkhttp {

    private val queue = LinkedBlockingQueue<JsonObject>()

    private val client = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

    companion object {
        @JvmStatic private val logger = LoggerFactory.getLogger(SobaOrderControllerOkhttp::class.java)
    }

    fun callKubeApi() {
        logger.info("Connect to the Kubernetes API server through localhost:8001.")

        var resourceVersion = initialQuery()
        while (true) {
            logger.info("Start watching.")
            try {
                resourceVersion = watchQuery(resourceVersion)
            } catch (e: IOException) {
                logger.info(e.message)
            }
            logger.info("Watching terminated.")
        }
    }

    private fun initialQuery(): String {
        val url = HttpUrl.Builder().scheme("http").host("localhost").port(8001)
                .addPathSegment("apis/kazusato.local/v1alpha1/sobaorders")
                .build()
        val req = Request.Builder().url(url).build()
        val resp = client.newCall(req).execute()
        logger.info("Response: ${resp}")

        if (!resp.isSuccessful) {
            throw RuntimeException("Response status: ${resp.code()}")
        }

        val jsonStr = resp.body()?.string() ?: throw RuntimeException("Null response body.")
        logger.info("Body ${jsonStr}")

        val resourceVersion = readResourceVersion(jsonStr)
        logger.info("Resource version: ${resourceVersion}")

        return resourceVersion
    }

    private fun watchQuery(resourceVersion: String): String {
        val url = HttpUrl.Builder().scheme("http").host("localhost").port(8001)
                .addPathSegment("apis/kazusato.local/v1alpha1/sobaorders")
                .addQueryParameter("resourceVersion", resourceVersion)
                .addQueryParameter("watch", "1")
                .build()
        val req = Request.Builder().url(url).build()
        val resp = client.newCall(req).execute()

        var newResourceVersion = resourceVersion
        while (!resp.body()!!.source().exhausted()) {
            val chunk = resp.body()?.source()?.readUtf8Line() ?: throw RuntimeException("Null response body.")
            newResourceVersion = readResourceVersionFromChunk(chunk)
            logger.info("Chunk: ${chunk}")
            logger.info("Resource version: ${newResourceVersion}")
        }

        return newResourceVersion
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
