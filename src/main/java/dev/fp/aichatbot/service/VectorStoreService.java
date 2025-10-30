package dev.fp.aichatbot.service;

import dev.fp.aichatbot.rag.EmbeddingDocumentPreparer;
import dev.fp.aichatbot.rag.SemanticParser;
import jakarta.annotation.PostConstruct;
import dev.fp.aichatbot.rag.SemanticChunk;
import dev.fp.aichatbot.rag.FileManager;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.tokenizer.JTokkitTokenCountEstimator;
import org.springframework.ai.tokenizer.TokenCountEstimator;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@Service
public class VectorStoreService {

    private OllamaEmbeddingModel embeddingModel;

    private TokenCountEstimator tokenEstimator = new JTokkitTokenCountEstimator();
    private SimpleVectorStore vectorStore;

    @Value("${ai.chatbot.rag.folder.raw}")
    private String rawFolder;

    @Value("${ai.chatbot.rag.vectorstore.cannonicalpath}")
    private String vectorStoreCannonicalPath;

    @Value("${ai.chatbot.rag.maxtokens}")
    private int maxTokens;

    public VectorStoreService(OllamaEmbeddingModel embeddingClient) {
        this.embeddingModel = embeddingClient;
    }

    @PostConstruct
    public void populateVectorStore() {

        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        File vectorStoreFile = new File(vectorStoreCannonicalPath);

        if (vectorStoreFile.exists()) {
            System.out.println("Loading existing vectorstore from file");
            vectorStore.load(vectorStoreFile);

        } else {
            System.out.println("Loading new vectorstore from raw files");

            for (File file : FileManager.getFiles(rawFolder)) {

                try {
                    String fileContent = Files.readString(file.toPath(), StandardCharsets.UTF_8);

                    // Extract
                    SemanticParser mdParser = new SemanticParser();
                    List<SemanticChunk> semanticChucnks = mdParser.parseMarkdown(file.getName(), fileContent);

                    // Transform
                    EmbeddingDocumentPreparer embeddingDocumentPreparer = new EmbeddingDocumentPreparer();
                    List<Document> documents = embeddingDocumentPreparer.convertSemanticChunksToDocument(semanticChucnks, file.getName(), maxTokens, tokenEstimator);

                    // Load
                    vectorStore.add(documents); // does the Embedding

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            File outFile = new File(vectorStoreCannonicalPath);
            vectorStore.save(outFile);
        }

        this.vectorStore=vectorStore;
    }

    public SimpleVectorStore getVectorStore() {
        return vectorStore;
    }

}
