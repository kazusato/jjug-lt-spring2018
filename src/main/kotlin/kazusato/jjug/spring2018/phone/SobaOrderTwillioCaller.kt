package kazusato.jjug.spring2018.phone

import org.slf4j.LoggerFactory
import java.util.concurrent.BlockingQueue
import javax.json.JsonObject

class SobaOrderTwillioCaller(queue: BlockingQueue<JsonObject>) {

    companion object {
        @JvmStatic private val callIntervalMillis = 60_000L
        @JvmStatic private val logger = LoggerFactory.getLogger(SobaOrderTwillioCaller::class.java)
    }

    private val queue: BlockingQueue<JsonObject>

    private val fromNumber = System.getenv("FROM_NUMBER")
    private val toNumber = System.getenv("TO_NUMBER")
    private val accountSid: String = System.getenv("ACCOUNT_SID")
    private val accessToken: String = System.getenv("ACCESS_TOKEN")

    init {
        this.queue = queue
    }

    fun watchQueue() {
        val twilio = TwilioCaller()
        while (true) {
            val jsonObj = queue.take()
            val dataList = retrieveData(jsonObj)
            dataList.forEach {
                logger.info("Phone call: ${generatePhoneMessage(it)}")
                twilio.call(fromNumber, toNumber, accountSid, accessToken, generatePhoneMessage(it))
                Thread.sleep(callIntervalMillis)
            }
        }
    }

    fun retrieveData(jsonObj: JsonObject): List<JsonObject> {
        val list = mutableListOf<JsonObject>()
        val items = jsonObj.getJsonArray("items")
        if (items != null) {
            // initail call data
            items.forEach { list.add(it as JsonObject) }
        } else {
            // chunk data
            val targetObj = jsonObj.getJsonObject("object")
            list.add(targetObj)
        }

        return list
    }

    fun generatePhoneMessage(jsonObj: JsonObject): String {
        val spec = jsonObj.getJsonObject("spec")
        val from = spec.getJsonString("from").string

        val items = spec.getJsonArray("items")

        val sb = StringBuilder()
        sb.append("${from}です。")
        items.forEach {
            val item = it as JsonObject
            sb.append("${item.getJsonString("type").string}、")
            sb.append("${item.getJsonNumber("count").intValue()}にんまえ、")
        }
        sb.append("お願いします。")

        return sb.toString()
    }

}
