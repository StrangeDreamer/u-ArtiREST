package me.daisyliao.service.client;

import me.daisyliao.service.domain.BusinessRuleModel;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AuthorizedFeignClient(name = "businessrule")
public interface BusinessRuleClient {
    @RequestMapping(method = RequestMethod.POST, value = "/api/business-rules/verify_atom_conditions")
    Boolean verifyAtomConditions(@RequestParam Map<String, String> map);

    @RequestMapping(method = RequestMethod.GET, value = "/api/business-rules/{id}")
    BusinessRuleModel getBusinessRule(@PathVariable(value = "id") String id);
}
