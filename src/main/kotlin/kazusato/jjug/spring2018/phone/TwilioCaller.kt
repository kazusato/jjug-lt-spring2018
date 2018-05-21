package kazusato.jjug.spring2018.phone

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedHashMap

fun main(args: Array<String>) {
    val fromNumber = System.getenv("FROM_NUMBER")
    val toNumber = System.getenv("TO_NUMBER")
    val accountSid = System.getenv("ACCOUNT_SID")
    val accessToken = System.getenv("ACCESS_TOKEN")

    TwilioCaller().call(fromNumber, toNumber, accountSid, accessToken, "こんにちは、世界!")
}

class TwilioCaller {

    fun call(fromNumber: String,
            toNumber: String,
            accountSid: String,
            accessToken: String,
            message: String) {
        val client = ClientBuilder.newClient()
        val target = client.target("https://api.twilio.com/2010-04-01")
                .path("Accounts/${accountSid}/Calls")

        val form = MultivaluedHashMap<String, String>()
        form.putSingle("From", fromNumber)
        form.putSingle("To", toNumber)
        form.putSingle("Url", toTwimletsUrl(message))

        val resp = target.register(HttpAuthenticationFeature.basic(accountSid, accessToken))
                .request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String::class.java)
        println(resp)
    }

    fun toTwimletsUrl(message: String): String
            = "http://twimlets.com/echo?Twiml=${toTwimlXml(message)}"

    fun toTwimlXml(message: String): String = URLEncoder.encode(
            """<?xml version="1.0" encoding="UTF-8"?>
                <Response>
                    <Say language="ja-jp">${message}</Say>
                </Response>""",
            StandardCharsets.UTF_8.name())

}