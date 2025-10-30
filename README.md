# Spring AI Chatbot with Ollama

This project is a simple **Spring Boot chatbot** powered by **Ollama** and **Spring AI**. It demonstrates how to integrate a local LLM (such as Mistral) with RAG using Spring Boot REST API, and how to handle chat sessions and prompt history.

## Overview

This application provides a REST endpoint that accepts chat messages and returns model responses using **Ollama** as the local inference server.  

It supports multi-turn conversations through a simple **`history_id`** field that represents the chat session ID.

The RAG ingests the book Winnie-the-Pooh (public domain) as sample knowledge base data.

## Prerequisites

- **Java 21**
- **Spring Boot**
- **Ollama** (installed locally)
- **IntelliJ IDEA** (optional, for development)
- **Bruno** or any API testing tool (e.g., Postman, cURL)

## Setup

Install Ollama

```bash
brew install ollama
```

Start the Ollama server
```bash
ollama serve
```
Run or pull models

```bash
ollama run mistral
ollama pull nomic-embed-text
```

To run the application in IntelliJ, create a new configuration with the folowing:

- Type: Application 
- Main class: dev.fp.aichatbot.Application 
- JDK: Java 21

## Testing the Chat Endpoint
Use Bruno, Postman, or cURL to test the chat API.

Endpoints
- POST http://localhost:8080/api/bot/chat
- POST http://localhost:8080/api/bot/chat-rag

Request Body:
```JSON
{
"prompt_message": "What's the name of the donkey in Winnie-the-Pooh?",
"history_id": "1"
}
```

- The history_id acts as a chat session ID, allowing the chatbot to maintain context across multiple messages.

## References

- Winnie-the-Pooh by A. A. Milne, available from [Project Gutenberg](https://www.gutenberg.org/cache/epub/67098/pg67098.txt)
- [Baeldung: Spring AI Ollama ChatGPT-like Chatbot](https://www.baeldung.com/spring-ai-ollama-chatgpt-like-chatbot)
- [Infinite Circuits: Building a Simple RAG System in Spring Boot with Ollama](https://www.infinitecircuits.dev/blogs/blog/building-a-simple-rag-system-in-spring-boot-with-ollama-REYhPlHHAcEcMR0mMVYe)

## License
This project is distributed under the MIT License. See LICENSE for details.
