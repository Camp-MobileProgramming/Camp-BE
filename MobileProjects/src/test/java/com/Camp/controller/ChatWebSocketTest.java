package com.Camp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatWebSocketTest {

    @LocalServerPort
    int port;

    @Test
    void testChatFlowExecute() throws Exception {
        WebSocketClient client = new StandardWebSocketClient();
        List<String> received = new CopyOnWriteArrayList<>();

        WebSocketSession session = client.execute(
                new TextWebSocketHandler() {
                    @Override
                    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                        session.sendMessage(new TextMessage("""
                        {"type":"join","myNickname":"동녕","targetNickname":"동뇽뇽"}
                    """));

                        session.sendMessage(new TextMessage("""
                        {"type":"chat","content":"hi"}
                    """));
                    }

                    @Override
                    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                        received.add(message.getPayload());
                    }
                },
                new WebSocketHttpHeaders(),
                URI.create("ws://localhost:"+port+"/chat-ws")
        ).get(); // <- handshake 완료까지 기다림

        Thread.sleep(1000);

        assertThat(received)
                .anyMatch(j -> j.contains("\"type\":\"chat\"") && j.contains("hi"));
    }

}
