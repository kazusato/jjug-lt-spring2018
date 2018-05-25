package kazusato.jjug.spring2018

import kazusato.jjug.spring2018.kubeapi.SobaOrderControllerOkhttp
import org.junit.jupiter.api.Test

class CallKubeApiOkhttpTest {

    @Test
fun callKubeApi() {
        val controller = SobaOrderControllerOkhttp()
        controller.callKubeApi()
    }

}
