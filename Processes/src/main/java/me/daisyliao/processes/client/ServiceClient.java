package me.daisyliao.processes.client;

import me.daisyliao.processes.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AuthorizedFeignClient(name = "service")
public interface ServiceClient {
    @RequestMapping(method = RequestMethod.POST, value = "/api/service/invoke_human_service")
    BusinessRuleModel invokeHumanService(@RequestParam Map<String, String> map);

    @RequestMapping(method = RequestMethod.GET, value = "/api/service-models/{id}")
    ServiceModel getService(@PathVariable(value = "id") String id);
}
