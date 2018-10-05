package me.daisyliao.service.client;

import me.daisyliao.service.domain.Artifact;
import me.daisyliao.service.domain.Process;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@AuthorizedFeignClient(name = "processes")
public interface ProcessClient {
    @RequestMapping(method = RequestMethod.GET, value = "/api/processes/{id}")
    Process getProcessFromProcessService(@PathVariable(value = "id") String id);

    @RequestMapping(method = RequestMethod.GET, value = "/api/artifacts/{id}")
    Artifact getArtifact(@PathVariable(value = "id") String id);
}
