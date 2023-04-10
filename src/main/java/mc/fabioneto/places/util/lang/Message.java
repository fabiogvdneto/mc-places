package mc.fabioneto.places.util.lang;

import org.bukkit.command.CommandSender;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.function.Function;

public class Message {

    private String content;

    public Message(String content) {
        this.content = Objects.requireNonNull(content);
    }

    public String getContent() {
        return content;
    }

    public void send(CommandSender sender) {
        sender.sendMessage(content);
    }

    public Message format(Object... args) {
        this.content = MessageFormat.format(content, args);
        return this;
    }

    public Message apply(Function<String, String> func) {
        this.content = Objects.requireNonNull(func.apply(content));
        return this;
    }
}
