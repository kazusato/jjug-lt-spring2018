package kazusato.jjug.spring2018

import kazusato.jjug.spring2018.kubeapi.SobaOrderController
import org.junit.jupiter.api.Test

class CallKubeApiTest {

    @Test
    fun callKubeApi() {
        val controller = SobaOrderController()
        controller.callKubeApi()
    }

}
