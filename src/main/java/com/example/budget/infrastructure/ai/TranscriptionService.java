package com.example.budget.infrastructure.ai;

import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/** Converte áudio em texto usando o modelo de transcrição da OpenAI (Whisper). */
@Service
public class TranscriptionService {

    private final OpenAiAudioTranscriptionModel transcriptionModel;

    public TranscriptionService(OpenAiAudioTranscriptionModel transcriptionModel) {
        this.transcriptionModel = transcriptionModel;
    }

    public String transcribe(byte[] audio, String filename) {
        Resource resource = new ByteArrayResource(audio) {
            @Override
            public String getFilename() {
                return filename == null ? "audio.mp3" : filename;
            }
        };
        return transcriptionModel.call(new AudioTranscriptionPrompt(resource)).getResult().getOutput();
    }
}
