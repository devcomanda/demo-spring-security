package com.devcomanda.demospringsecurity.web.api;

import com.devcomanda.demospringsecurity.model.DemoModel;
import com.devcomanda.demospringsecurity.utils.DemoModelFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@RestController
public class PrivateResource {

    @GetMapping("/api/private/demomodels")
    public List<DemoModel> loadDemoModels() {

        return IntStream
                .range(1, 5)
                .mapToObj(DemoModelFactory::createRandomDemoModel)
                .collect(Collectors.toList());

    }
}
