package ninja.soroosh.chatbot.core.telegram;

import ninja.soroosh.chatbot.core.runner.Command;
import ninja.soroosh.chatbot.core.runner.CommandRunner;
import ninja.soroosh.chatbot.core.runner.Context;
import ninja.soroosh.chatbot.core.runner.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Optional;

@RestController
public class TelegramController {
    @Autowired
    private RestTemplateBuilder restTemplateBuilder;
    @Autowired
    private CommandRunner commandRunner;
    @Autowired
    private TelegramCommandBuilder telegramCommandBuilder;

    @Value("${chatops.connector.telegram.key}")
    private String key;

    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        this.restTemplate = restTemplateBuilder.build();
    }


    @PostMapping(path = "/connectors/telegram")
    public String webhook(@RequestBody TelegramRequest telegramRequest) {
        final long chatId = telegramRequest.getMessage().getChat().getId();
        final Command command = telegramCommandBuilder.build(telegramRequest);
        final Response commandResponse = commandRunner.run(command, new Context(Optional.empty(), "telegram"));

        Object response = restTemplate.postForEntity(
                String.format("https://api.telegram.org/bot%s/sendMessage", key),
                new TelegramSendMessage(chatId, commandResponse.message()), Object.class);

        System.out.println(response);
        return "ok";
    }
}
