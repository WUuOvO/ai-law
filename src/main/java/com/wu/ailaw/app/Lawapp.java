package com.wu.ailaw.app;

import com.wu.ailaw.advisor.MyLoggerAdvisor;

import com.wu.ailaw.advisor.ReReadingAdvisor;
import com.wu.ailaw.chatmemory.FileBasedChatMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import java.util.List;

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

    /**
     * 初始化构造chatclient
     * @param deepseekChatmodel
     */
    public Lawapp(ChatModel deepseekChatmodel) {
        ChatMemory chatInMemory=new InMemoryChatMemory();//通过内存方式存储对话

            // 初始化基于文件的对话记忆
            String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
            ChatMemory chatFileMemory = new FileBasedChatMemory(fileDir);

        chatClient= ChatClient.builder(deepseekChatmodel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatInMemory)//增强记忆（多轮对话）
                        ,new MyLoggerAdvisor()//用户日志记录，advisor(顾问)相当于拦截器，在用户输入，系统输出内容时候会进行处理
                        //,new ReReadingAdvisor()//提高大型语言模型的推理能力（适合长问题）
                )
                .build();
    }

    /**
     * 自然语言输出
     * @param message
     * @param chatId
     * @return
     */

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

//    record LawReport(String title, List<String> message){}
//    /**
//     * 法律报告（结构化输出）通过文档存信息，配置文件format: json（问题：无法多轮记忆，输出结果随机性很强）
//     * @param message
//     * @param chatId
//     * @return
//     */
//    public LawReport doChatWithReport (String message,String chatId) {
//                LawReport lawReport = chatClient
//                .prompt()
//                .system(SYSTEM_PROMPT+"你是一个法律顾问，能够基于用户上下文生成结构化建议,每次对话后都要生成结果，标题为{用户名}的法律报告")
//                .user(message)
//                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
//                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
//                .call()
//                .entity(LawReport.class);
//        log.info("content{}",lawReport);
//        return lawReport;
//    }

}
