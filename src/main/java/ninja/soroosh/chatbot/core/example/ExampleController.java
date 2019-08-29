package ninja.soroosh.chatbot.core.example;

import ninja.soroosh.chatbot.core.runner.Command;
import ninja.soroosh.chatbot.core.runner.CommandRunner;
import ninja.soroosh.chatbot.core.runner.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {
    @Autowired
    private CommandRunner commandRunner;
    @Autowired
    private ExampleCommandBuilder exampleCommandBuilder;

    @PostMapping(name = "/example")
    public ExampleResponse run(@RequestBody ExampleRequest exampleRequest) {
        final Command command = exampleCommandBuilder.build(exampleRequest);
        final Response resp = commandRunner.run(command);
        return new ExampleResponse(resp.message());
    }
}