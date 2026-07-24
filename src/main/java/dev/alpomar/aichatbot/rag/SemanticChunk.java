package dev.alpomar.aichatbot.rag;

import java.util.HashMap;
import java.util.Map;

public class SemanticChunk {

    // "source_file", fileName ; "chunk_index", chunkIndex
    private Map<String, Object> metadata = new HashMap<>();
    private String filename;
    private String text;
    private String heading;

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public SemanticChunk(String heading, String text, String filename) {
        this.heading = heading;
        this.filename = filename;
        this.text = text;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }
}
