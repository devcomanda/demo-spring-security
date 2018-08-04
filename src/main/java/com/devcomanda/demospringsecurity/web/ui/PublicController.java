package com.devcomanda.demospringsecurity.web.ui;

import com.devcomanda.demospringsecurity.model.DemoModel;
import com.devcomanda.demospringsecurity.utils.DemoModelFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@Controller
public class PublicController {

    @GetMapping("/public/demomodels")
    public String displayListDemoModels(
            Model model
    ) {

        List<DemoModel> demoModels = IntStream
                .range(1, 5)
                .mapToObj(DemoModelFactory::createRandomDemoModel)
                .collect(Collectors.toList());

        model.addAttribute("models", demoModels);
        return "public/listDemoModels";
    }
}
