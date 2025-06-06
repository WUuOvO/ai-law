package com.wu.ailaw.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SpringAi_ai_test implements CommandLineRunner {
    @Resource
    private ChatModel deepseekChatmodel;

    @Override
    public void run(String... args) throws Exception {
        AssistantMessage message = deepseekChatmodel.call(new Prompt("你好，你是什么模型"))
                .getResult()
                .getOutput();
        System.out.println(message.getText());
    }
}
