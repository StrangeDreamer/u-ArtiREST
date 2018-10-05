package me.daisyliao.processes.client;

import me.daisyliao.processes.domain.ArtifactModel;
import me.daisyliao.processes.domain.ProcessModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@AuthorizedFeignClient(name = "artifactmodel")
    //,fallback=ArtifactModelFeignClientFallback.class)
public interface ArtifactModelClient {
    @RequestMapping(method = RequestMethod.GET, value = "/api/process-models/{id}")
    ProcessModel getProcessModelFromArtifactModelService(@PathVariable(value = "id") String id);

    @RequestMapping(method = RequestMethod.GET, value = "/api/artifact-models/{id}")
    ArtifactModel getArtifactModelFromArtifactModelService(@PathVariable(value = "id") String id);
}
