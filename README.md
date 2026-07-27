# Spring AI Chatbot with Ollama

A companion to my [digital twin](https://alpomar.dev) - where the twin grounds responses via prompt stuffing, this project explores what a RAG pipeline looks like beneath the abstractions.

A from-scratch Retrieval-Augmented Generation (RAG) pipeline built on **Spring Boot**, **Spring AI**, and **Ollama**, running entirely on local models - no API keys, no cloud inference.

Most RAG demos stop at "call the vector store library." This project exists to work through the parts that are usually hidden behind a one-liner: how source documents get split into retrievable units, how chunk size is bounded against a model's context window, and what actually changes in a response when retrieval is added versus removed. The knowledge base is a public-domain text (Winnie-the-Pooh) so the retrieval behavior is easy to verify by hand.

## What This Demonstrates

- **Heading-aware semantic chunking** - [`SemanticParser`](src/main/java/dev/alpomar/aichatbot/rag/SemanticParser.java) walks a Markdown AST (via flexmark) and splits content at heading boundaries instead of fixed character windows, tagging each chunk with its full heading breadcrumb (e.g. `Chapter 1 > Subchapter 1.11`) so retrieved passages keep their document context.
- **Token-aware chunk sizing** - [`EmbeddingDocumentPreparer`](src/main/java/dev/alpomar/aichatbot/rag/EmbeddingDocumentPreparer.java) re-packs each semantic chunk sentence-by-sentence up to a configurable token budget, using a real tokenizer (JTokkit) rather than a character-count proxy, so chunks stay within the embedding model's limits without cutting sentences mid-thought.
- **Local embeddings with a persisted vector store** - chunks are embedded with Ollama's `nomic-embed-text` model into an in-memory [`SimpleVectorStore`](src/main/java/dev/alpomar/aichatbot/service/VectorStoreService.java) that's snapshotted to disk (`vector_store.json`), so re-embedding only happens once, not on every application restart.
- **Grounded vs. ungrounded chat, side by side** - the app exposes two endpoints against the same underlying model so the effect of retrieval is directly observable: `/api/bot/chat` is a plain multi-turn chatbot with manually managed conversation history, while `/api/bot/chat-rag` wires the vector store into the prompt via Spring AI's `QuestionAnswerAdvisor`.

## Architecture

**Ingestion (once, on startup if no snapshot exists):**
```
Markdown file → SemanticParser (heading-scoped chunks)
             → EmbeddingDocumentPreparer (token-bounded sentence packing)
             → Ollama (nomic-embed-text) → SimpleVectorStore → vector_store.json
```

**Query:**
```
POST /api/bot/chat      → OllamaChatModel + in-memory history          → response
POST /api/bot/chat-rag  → QuestionAnswerAdvisor (vector search) + LLM  → grounded response
```

## Prerequisites

- **Java 21**
- **Spring Boot** (via Maven - the runtime hosting the Spring AI integration and REST API)
- **Ollama** (installed locally)
- **Bruno** or any API testing tool (e.g., Postman, cURL) - sample requests are in [`docs/bruno-endpoints`](docs/bruno-endpoints)

## Setup

Install Ollama:

```bash
brew install ollama
```

Start the Ollama server:

```bash
ollama serve
```

Pull the models used by this project (chat model configured in `application.yml`, embedding model used for retrieval):

```bash
ollama pull gemma:2b
ollama pull nomic-embed-text
```

Run the app:

```bash
mvn spring-boot:run
```

Or in IntelliJ, create an Application configuration with:

- Main class: `dev.alpomar.aichatbot.Application`
- JDK: Java 21

On first run, the app parses, chunks, and embeds `src/main/resources/data/Winnie-the-Pooh (A. A. Milne).md` and writes the resulting vectors to `vector_store.json`. Subsequent runs load that file directly.

## Testing the Chat Endpoints

Use Bruno, Postman, or cURL.

| Endpoint | Behavior |
|---|---|
| `POST /api/bot/chat` | Plain chat with the local model, no retrieval - answers come only from the model's own knowledge plus conversation history. |
| `POST /api/bot/chat-rag` | Retrieves relevant chunks from the vector store and injects them into the prompt before calling the model. |

Request body (same shape for both):

```json
{
  "prompt_message": "What's the name of the donkey in Winnie-the-Pooh?",
  "history_id": "1"
}
```

`history_id` is a session identifier used to look up prior turns so the bot maintains context across multiple messages.

Try the same prompt against both endpoints - `/chat` will typically be vague or invent an answer, while `/chat-rag` should cite the book's own text (Eeyore), which is the concrete difference RAG is meant to demonstrate.

## References

- Winnie-the-Pooh by A. A. Milne, available from [Project Gutenberg](https://www.gutenberg.org/cache/epub/67098/pg67098.txt)
- [Baeldung: Spring AI Ollama ChatGPT-like Chatbot](https://www.baeldung.com/spring-ai-ollama-chatgpt-like-chatbot)
- [Infinite Circuits: Building a Simple RAG System in Spring Boot with Ollama](https://www.infinitecircuits.dev/blogs/blog/building-a-simple-rag-system-in-spring-boot-with-ollama-REYhPlHHAcEcMR0mMVYe)

## License

This project is distributed under the MIT License. See LICENSE for details.

# About the Author
I'm Filipe Albero Pomar. Engineering manager, sometime product manager, still hands-on with the code. Curious how? More at [alpomar.dev](https://alpomar.dev)
