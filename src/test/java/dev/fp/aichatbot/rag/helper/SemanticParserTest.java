package dev.fp.aichatbot.rag.helper;

import dev.fp.aichatbot.rag.SemanticParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SemanticParserTest {

    final String markdown = """
            # Book Title
            
            The quick brown fox jumped over the lazy dog. The quick brown fox jumped over the lazy dog. The quick brown fox jumped over the lazy dog. The quick brown fox jumped over the lazy dog.             
            
            ## Chapter 1
            
            ### Subchapter 1.11
            
            The quick brown fox jumped over the lazy dog. The quick brown fox jumped over the lazy dog. The quick brown fox jumped over the lazy dog. The quick brown fox jumped over the lazy dog.             
            
            The quick brown fox jumped over the lazy dog. The quick brown fox jumped over the lazy dog. The quick brown fox jumped over the lazy dog. The quick brown fox jumped over the lazy dog.             
            
            """;

    final String md_wrong_order = """
            ### Book Title
            
            The quick brown fox jumped over the lazy dog. The quick brown fox jumped over the lazy dog. The quick brown fox jumped over the lazy dog. The quick brown fox jumped over the lazy dog.             
            
            # Chapter 1
            
            # Subchapter 1.11
            
            The quick brown fox jumped over the lazy dog. The quick brown fox jumped over the lazy dog. The quick brown fox jumped over the lazy dog. The quick brown fox jumped over the lazy dog.             
            """;

    final String fileName = "Winnie-the-Pooh (A. A. Milne).md";

    @Test
    public void shouldParseMarkdownEmptyMD() {
        SemanticParser parser = new SemanticParser();
        var chunks = parser.parseMarkdown("", "");
        assertNotNull(chunks);
        assertEquals(chunks.size(),0);
    }

    @Test
    public void shouldParseMarkdownWrongOrderMD() {
        SemanticParser parser = new SemanticParser();
        assertThrows(Exception.class, () -> parser.parseMarkdown(fileName, md_wrong_order));
    }

    @Test
    public void shouldParseMarkdownInChunks() {
        SemanticParser parser = new SemanticParser();
        var chunks = parser.parseMarkdown(fileName, markdown);
        assertEquals(2, chunks.size());
        assertEquals(fileName, chunks.get(1).getFilename());
        assertEquals("Book Title > Chapter 1 > Subchapter 1.11", chunks.get(1).getHeading()); // TODO: refactor to be The Way > Character > 1
        assertEquals(true, chunks.get(1).getText().startsWith("The quick brown fox jumped over the lazy dog."));
    }
}
