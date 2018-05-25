package kazusato.jjug.spring2018.kubeapi

import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.client.ResponseHandler
import org.apache.http.util.EntityUtils
import java.io.StringReader
import javax.json.Json
import javax.json.JsonObject

class SobaOrderResponseHandler : ResponseHandler<Pair<String, JsonObject>> {

    override fun handleResponse(resp: HttpResponse?): Pair<String, JsonObject> {
        val status = resp?.statusLine?.statusCode ?: throw RuntimeException("HTTP response is null.")

        if (status != HttpStatus.SC_OK) {
            throw RuntimeException("HTTP status: ${status}")
        }

        val entity = resp.entity
        val jsonStr = if (entity != null) {
            EntityUtils.toString(entity)
        } else {
            throw RuntimeException("HTTP response entity is null.")
        }
        return readResourceVersion(jsonStr)
    }

    private fun readResourceVersion(jsonStr: String): Pair<String, JsonObject> {
        val reader = Json.createReader(StringReader(jsonStr))
        val obj = reader.readObject()
        val metadata = obj.getJsonObject("metadata")
        val resourceVersion = metadata.getString("resourceVersion")
        return Pair(resourceVersion, obj)
    }

}

data class SobaOrderResponse(
        var resourceVersion: String,
        var jsonObject: JsonObject
)
