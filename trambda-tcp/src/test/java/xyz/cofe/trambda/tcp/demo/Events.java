package xyz.cofe.trambda.tcp.demo;

import xyz.cofe.trambda.tcp.Publisher;

public interface Events {
    Publisher<ServerDemoEvent> defaultPublisher();
    Publisher<ServerDemoEvent2> timedEvents();
}
