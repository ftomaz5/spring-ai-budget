package com.example.budget.infrastructure.ai;

import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.stereotype.Service;

/** Converte texto em áudio (Text-to-Speech) usando a OpenAI. */
@Service
public class SpeechService {

    private final OpenAiAudioSpeechModel speechModel;

    public SpeechService(OpenAiAudioSpeechModel speechModel) {
        this.speechModel = speechModel;
    }

    public byte[] synthesize(String text) {
        return speechModel.call(text);
    }
}
