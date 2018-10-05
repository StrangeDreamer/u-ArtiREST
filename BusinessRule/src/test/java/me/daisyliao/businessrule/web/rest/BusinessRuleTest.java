package me.daisyliao.businessrule.web.rest;


import me.daisyliao.businessrule.BusinessRuleApp;
import me.daisyliao.businessrule.config.SecurityBeanOverrideConfiguration;
import me.daisyliao.businessrule.domain.BusinessRuleModel;
import me.daisyliao.businessrule.repository.BusinessRuleModelRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BusinessRuleApp.class, SecurityBeanOverrideConfiguration.class})
public class BusinessRuleTest {

    @Autowired
    private BusinessRuleModelRepository businessRuleModelRepository;


    @Test
    public void test1() {
//        List<BusinessRuleModel> rules = businessRuleModelRepository.findByBrc("aaa");
//        System.out.println("wenyu test: "+rules.size());
        List<BusinessRuleModel>  rules = businessRuleModelRepository.findAll();

        System.out.println("wenyu test: "+rules.size()+ " ");
    }
}
