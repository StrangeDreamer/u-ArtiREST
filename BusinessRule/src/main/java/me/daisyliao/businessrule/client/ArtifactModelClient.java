package me.daisyliao.businessrule.client;

import me.daisyliao.businessrule.domain.ArtifactModel;
import me.daisyliao.businessrule.domain.ProcessModel;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@AuthorizedFeignClient(name = "artifactmodel")
    //fallback=ArtifactModelFeignClientFallback.class)
public interface ArtifactModelClient {
    @RequestMapping(method = RequestMethod.GET, value = "/api/artifact-models/{id}")
    ArtifactModel getArtifactModelFromArtifactModelService(@PathVariable(value = "id") String id);

}
