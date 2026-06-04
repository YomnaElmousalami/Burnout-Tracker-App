package com.burnoutdetector.demo.service;

import com.burnoutdetector.demo.model.CalendarSummary;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private final ChatClient chatClient;

    public AiService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String getRecommendations(CalendarSummary summary, int score) {
        String prompt = """
                You are a workplace wellness coach. Based on this person's calendar data from the last 2 weeks:
                - Total meetings: %d
                - Hours in meetings: %.1f
                - Earliest meeting: %s
                - Latest meeting: %s
                - Weekend meetings: %d
                - Burnout risk score: %d/100
                
                Give 3 short, practical recommendations to reduce burnout risk. Be direct and specific.
                """.formatted(
                summary.getTotalMeetings(),
                summary.getTotalHours(),
                summary.getEarliestMeeting(),
                summary.getLatestMeeting(),
                summary.getWeekendMeetings(),
                score
        );

        return chatClient.prompt(prompt).call().content();
    }

}