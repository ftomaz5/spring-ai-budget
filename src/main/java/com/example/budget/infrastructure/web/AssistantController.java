package com.example.budget.infrastructure.web;

import com.example.budget.infrastructure.ai.AssistantService;
import com.example.budget.infrastructure.ai.SpeechService;
import com.example.budget.infrastructure.ai.TranscriptionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * API REST do assistente de orçamento.
 * Fluxo completo: áudio -> transcrição -> LLM (Tool Calling) -> resposta (texto ou voz).
 */
@RestController
@RequestMapping("/api/assistant")
public class AssistantController {

    private final AssistantService assistantService;
    private final TranscriptionService transcriptionService;
    private final SpeechService speechService;

    public AssistantController(AssistantService assistantService,
                              TranscriptionService transcriptionService,
                              SpeechService speechService) {
        this.assistantService = assistantService;
        this.transcriptionService = transcriptionService;
        this.speechService = speechService;
    }

    /** Entrada por texto: interpreta e responde em texto. */
    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String answer = assistantService.handle(request.message());
        return new ChatResponse(request.message(), answer);
    }

    /** Entrada por áudio: transcreve, interpreta via LLM e responde em texto. */
    @PostMapping(value = "/voice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ChatResponse voice(@RequestParam("audio") MultipartFile audio) throws IOException {
        String transcript = transcriptionService.transcribe(audio.getBytes(), audio.getOriginalFilename());
        String answer = assistantService.handle(transcript);
        return new ChatResponse(transcript, answer);
    }

    /** Entrada por áudio com resposta falada (TTS): devolve um MP3. */
    @PostMapping(value = "/voice-to-voice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = "audio/mpeg")
    public ResponseEntity<byte[]> voiceToVoice(@RequestParam("audio") MultipartFile audio) throws IOException {
        String transcript = transcriptionService.transcribe(audio.getBytes(), audio.getOriginalFilename());
        String answer = assistantService.handle(transcript);
        byte[] speech = speechService.synthesize(answer);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"resposta.mp3\"")
                .body(speech);
    }

    public record ChatRequest(String message) {}
    public record ChatResponse(String input, String answer) {}
}
