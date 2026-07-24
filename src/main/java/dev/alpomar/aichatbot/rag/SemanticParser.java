package dev.alpomar.aichatbot.rag;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class SemanticParser {

    public List<SemanticChunk> parseMarkdown(String filename, String markdown) {

        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        Node document = parser.parse(markdown);

        List<SemanticChunk> chunks = new ArrayList<>();
        String currentHeading = "";
        String currentHeadingWithCrumbs = "";

        Stack<String> stack = new Stack();
        int currentLevel = 1;
        int previousLevel = 1;

        StringBuilder currentBlock = new StringBuilder();

        for (Node node : document.getChildren()) {

            if (node instanceof Paragraph paragraph) {
                String text = extractText(paragraph).trim();

                if (!text.isEmpty()) {
                    currentBlock.append(text).append(" ");
                }

            } else if (node instanceof Heading heading) {

                if (currentBlock.length() > 0) {
                    chunks.add(new SemanticChunk(currentHeadingWithCrumbs, currentBlock.toString().trim(), filename));
                    currentBlock.setLength(0);
                }

                currentHeading = extractText(heading).trim();
                currentLevel = heading.getLevel();

                if (stack.isEmpty()) {
                    stack.push(currentHeading);
                }
                if (previousLevel < currentLevel) {
                    stack.push(currentHeading);
                }
                if (previousLevel > currentLevel) {
                    int levelsToPop = previousLevel - currentLevel + 1;
                    for (int i = 0; i < levelsToPop; i++) {
                        stack.pop();
                    }
                    stack.push(currentHeading);
                }
                else {
                    stack.pop();
                    stack.push(currentHeading);
                }

                currentHeadingWithCrumbs = getHeadingWithBreadcrumbs(stack);
                previousLevel = currentLevel;

            }
        }

        if (currentBlock.length() > 0) { // Add the last block if it has content
            chunks.add(new SemanticChunk(currentHeadingWithCrumbs, currentBlock.toString().trim(), filename));
        }

        return chunks;

    }

    private String getHeadingWithBreadcrumbs(Stack<String> stack) {

        if (stack.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            Iterator<String> iterator = stack.iterator();

            while (iterator.hasNext()) {
                String heading = iterator.next();
                sb.append(heading);

                if (iterator.hasNext()) {
                    sb.append(" > ");
                }
            }

            return sb.toString();
        }

    }

    private String extractText(Node node) {
        StringBuilder sb = new StringBuilder();
        for (Node child : node.getChildren()) {
            if (child instanceof Text text) {
                sb.append(text.getChars());
            } else {
                sb.append(extractText(child));
            }
        }
        return sb.toString();
    }


}
