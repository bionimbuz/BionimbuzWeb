package app.controllers;

import app.models.InfoModel;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/info")
    public InfoModel info(@RequestParam(value="name", defaultValue="World") String name) {
        return new InfoModel(counter.incrementAndGet(),
                            String.format(template, name));
    }
}
