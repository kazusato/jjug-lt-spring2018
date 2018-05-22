package kazusato.jjug.spring2018.kubeapi

import org.slf4j.LoggerFactory
import javax.ws.rs.client.ClientBuilder

fun main(args: Array<String>) {
    SobaOrderController().callKubeApi()
}

class SobaOrderController {

    companion object {
        @JvmStatic private val logger = LoggerFactory.getLogger(SobaOrderController::class.java)
    }

    fun callKubeApi() {
        logger.info("Connecting Kubernetes API server through localhost:8001.")

        while (true) {
            val client = ClientBuilder.newClient()
            val target = client.target("http://localhost:8001")
                    .path("apis/kazusato.local/v1alpha1/sobaorders")
            val resp = target.request().get()
            logger.info("Response: ${resp}")
            logger.info("Body: ${resp.readEntity(String::class.java)}")

            Thread.sleep(10_000)
            logger.info("Sleep: 10 seconds")
        }
    }

}