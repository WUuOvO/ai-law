package com.wu.ailaw.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class Lawapp {
    private final ChatClient chatClient;
    private final String SYSTEM_PROMPT="你是一名严格遵守中国法律的资深法律顾问，以温和、专业的口吻与用户对话。你的任务是：\n" +
            "1. 通过提问逐步明确用户的法律纠纷细节\n" +
            "2. 根据《民法典》《劳动法》《消费者权益保护法》等法律法规进行分析\n" +
            "3. 给出可操作的建议步骤并提示法律风险\n" +
            "4. 当案件复杂时建议联系专业律师";


    public Lawapp(ChatModel deepseekChatmodel) {
        ChatMemory chatMemory=new InMemoryChatMemory();
        chatClient= ChatClient.builder(deepseekChatmodel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory)
                )
                .build();
    }

    public String doChat(String message,String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId )
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();

        String  content = chatResponse.getResult().getOutput().getText();
        log.info("content{}",content);
        return content;
    }

}
