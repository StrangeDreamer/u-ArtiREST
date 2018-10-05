package me.daisyliao.businessrule.client;

import me.daisyliao.businessrule.domain.Artifact;
import me.daisyliao.businessrule.domain.ArtifactModel;
import me.daisyliao.businessrule.domain.ProcessModel;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@AuthorizedFeignClient(name = "processes")
public interface ProcessClient {
    @RequestMapping(method = RequestMethod.GET, value = "/api/artifacts/{id}")
    Artifact getArtifact(@PathVariable(value = "id") String id);

}
