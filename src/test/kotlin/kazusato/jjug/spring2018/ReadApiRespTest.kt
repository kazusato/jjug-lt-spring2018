package kazusato.jjug.spring2018

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.InputStreamReader
import javax.json.Json

class ReadApiRespTest {

    @Test
    fun readApiResp() {
        InputStreamReader(this.javaClass.classLoader.getResourceAsStream("apiresp.json")).use {
            val reader = Json.createReader(it)
            val obj = reader.readObject()
            val metadata = obj.getJsonObject("metadata")
            val resourceVersion = metadata.getString("resourceVersion")
            assertEquals("617259", resourceVersion)
        }
    }

}