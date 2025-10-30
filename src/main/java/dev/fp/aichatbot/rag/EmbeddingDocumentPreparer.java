package dev.fp.aichatbot.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.tokenizer.TokenCountEstimator;
import java.util.*;

public class EmbeddingDocumentPreparer {

    public List<Document> convertSemanticChunksToDocument(List<SemanticChunk> chunkslist, String filename, int maxTokens, TokenCountEstimator tokenCountEstimator) {

        List<Document> documents = new ArrayList<>();
        int currIndex = 0;

        for (SemanticChunk chunk : chunkslist) {

            String[] sentences = chunk.getText().split("(?<=[.!?])\\s+");
            StringBuilder chunkBuilder = new StringBuilder();
            int currentTokens = 0;

            for (String sentence : sentences) {

                sentence = sentence.trim();
                if (sentence.isEmpty()) continue;

                int estimatedTokens = tokenCountEstimator.estimate(sentence);

                // Sentence is too long — skip and log
                if (estimatedTokens > maxTokens) {
                    System.err.printf("Skipping oversized sentence in '%s' (heading: %s): %s [%d tokens > max %d]%n",
                            filename, chunk.getHeading(), sentence, estimatedTokens, maxTokens);
                    continue;
                }

                // Sentence would overflow current chunk — flush current chunk
                if (currentTokens + estimatedTokens > maxTokens) {
                    documents.add(new Document(chunkBuilder.toString().trim(), createMetadata(filename, chunk.getHeading(), currIndex++)));
                    chunkBuilder.setLength(0);
                    currentTokens = 0;
                }

                // Add sentence to current chunk
                chunkBuilder.append(sentence).append(" ");
                currentTokens += estimatedTokens;
            }

            // Flush final chunk if it has content
            if (chunkBuilder.length() > 0) {
                documents.add(new Document(chunkBuilder.toString().trim(), createMetadata(filename, chunk.getHeading(), currIndex++)));
            }
        }

        return documents;
    }


    private Map<String, Object> createMetadata(String fileName, String heading, int chunkIndex) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source_file", fileName);
        metadata.put("heading", heading);
        metadata.put("chunk_index", chunkIndex);
        return metadata;
    }
}
