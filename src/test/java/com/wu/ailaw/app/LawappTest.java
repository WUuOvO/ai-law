package com.wu.ailaw.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class LawappTest {
    @Resource
    private Lawapp lawapp;

    @Test
    void testChat() {
        String chatId= UUID.randomUUID().toString();
        String message="你好我名字是wgh";
        String answer=lawapp.doChat(message,chatId);
        Assertions.assertNotNull(answer);
         message="我是一名大学生，在university of glasgow";
         answer=lawapp.doChat(message,chatId);
        Assertions.assertNotNull(answer);
         message="我就读的大学名字是什么";
         answer=lawapp.doChat(message,chatId);
        Assertions.assertNotNull(answer);
    }
}