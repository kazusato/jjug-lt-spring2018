package kazusato.jjug.spring2018

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.InputStreamReader
import javax.json.Json

class ReadChunkTest {

    @Test
    fun readApiResp() {
        InputStreamReader(this.javaClass.classLoader.getResourceAsStream("chunk.json")).use {
            val reader = Json.createReader(it)
            val obj = reader.readObject()
            val targetObj = obj.getJsonObject("object")
            val metadata = targetObj.getJsonObject("metadata")
            val resourceVersion = metadata.getString("resourceVersion")
            assertEquals("922183", resourceVersion)

            val array = obj.getJsonArray("items")
            assertNull(array)
        }
    }

}