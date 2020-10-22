package ninja.soroosh.chatopia.core.connectors.telegram;

import ninja.soroosh.chatopia.core.runner.Command;
import ninja.soroosh.chatopia.core.runner.CommandRunner;
import ninja.soroosh.chatopia.core.runner.Context;
import ninja.soroosh.chatopia.core.runner.responses.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Optional;

@RestController
@ConditionalOnProperty(value = "chatopia.connector.telegram.enabled", havingValue = "true", matchIfMissing = true)
class TelegramController {
    @Autowired
    private RestTemplateBuilder restTemplateBuilder;
    @Autowired
    private CommandRunner commandRunner;
    @Autowired
    private TelegramCommandBuilder telegramCommandBuilder;

    @Autowired
    private ResponseHandler responseHandler;

    @Value("${chatopia.connector.telegram.key}")
    private String key;

    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        this.restTemplate = restTemplateBuilder.build();
    }


    @PostMapping(path = "/connectors/telegram")
    public String webhook(@RequestBody TelegramRequest telegramRequest) throws IOException {
        var message = telegramRequest.getMessage() != null ? telegramRequest.getMessage() : telegramRequest.getCallbackQuery().getMessage();
        var commandText = telegramRequest.getCallbackQuery() == null ? message.getText() : telegramRequest.getCallbackQuery().getData();
        final long chatId = message.getChat().getId();
        final Command command = telegramCommandBuilder.build(commandText);
        final String sessionId = "telegram-" + chatId;
        final Response commandResponse = commandRunner.run(
                command,
                new Context(Optional.of(sessionId), "telegram")
        );

        Object response = responseHandler.handle(message, commandResponse);

        System.out.println(response);
        return "ok";
    }


}

