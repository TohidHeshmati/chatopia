package ninja.soroosh.chatbot.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

@Getter
@AllArgsConstructor
public class Rule {
    private String commandName;
    private Object object;
    private Method method;
}
