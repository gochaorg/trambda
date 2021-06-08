package xyz.cofe.trchat;

import xyz.cofe.trambda.tcp.Publisher;

public interface ChatNotification {
    Publisher<ChatMessage> messages();
}
