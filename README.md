# 📝 StudySheet AI

## About the Project

**StudySheet AI** leverages RAG + Agent technologies to automatically compress messy, lengthy documents (PDF/PPT/Word) into compact, high-density cheatsheets for exam use, with multi-turn dialogue support.

---

## 📖 Project Overview

This project is an **intelligent knowledge card generation tool**. Its core goal is to transform user-uploaded **unstructured, inconsistently formatted documents** (e.g., course PPTs, textbook PDFs, meeting notes) into **visually compact, information-dense** note cards using **RAG (Retrieval-Augmented Generation)** and **Agent** technologies. The output is ideal for exam settings where students are allowed limited-page notes (e.g., a double-sided A4 cheatsheet).

The backend is built with **Spring Boot**, integrated with **LangChain4j** for Agent orchestration, and is designed for deployment on **AWS** following a Serverless-first, cost-conscious approach.

---

## ✨ Core Features

| Feature Module | Description |
| :--- | :--- |
| **Multi-format Document Upload** | Supports PDF, PPT, Word, images (MVP phase prioritizes PDF) |
| **Intelligent Document Parsing** | Extracts text, handles messy formatting and fragmented info (e.g., PPT bullet points) |
| **RAG-Enhanced Retrieval** | Retrieves the most relevant knowledge chunks based on user queries or exam scope |
| **Agent Tool Calling** | Agent autonomously decides to call tools like "Summarize," "Extract Table," "Format HTML" for smart compression |
| **Compact PDF Generation** | Generates **double-column, small-font, table-layout** PDFs to maximize page utilization |
| **Multi-turn Dialogue** | Users can iteratively refine content, adjust focus, and trim redundancy with the Agent |
| **Project Persistence** | Documents and chat histories are organized by Project/Session for long-term access |
| **Citation-Aware Responses** | Every answer includes references to source documents and page locations whenever possible |

---

## 🛠️ Tech Stack

### Backend Core
| Technology | Purpose | Version |
| :--- | :--- | :--- |
| **Java 17+** | Programming Language | 17+ |
| **Spring Boot** | Web Framework | 3.2.x |
| **Spring Security + JWT** | Authentication & Authorization | — |
| **LangChain4j** | Agent Orchestration, LLM Integration | 0.34.0+ |
| **Apache PDFBox** | PDF Text Extraction | 3.0.2 |
| **Apache POI** | Word / PPT Text Extraction | 5.2.x |
| **Flying Saucer** | HTML → PDF Rendering | 9.1.22 |

### Data & AI
| Technology | Purpose | Deployment |
| :--- | :--- | :--- |
| **PostgreSQL + PGvector** | Vector Database (document embeddings) | Docker / AWS RDS |
| **OpenAI API** | Large Language Model (optional) | Cloud |
| **Ollama** | Local LLM (optional) | Local Docker |
| **BM25 (Lucene)** | Keyword Search (hybrid retrieval) | Embedded |

### Deployment & Ops (AWS)
| Service | Purpose | Rationale |
| :--- | :--- | :--- |
| **AWS Lambda** | Serverless compute (backend logic) | Scales to zero, extremely low cost |
| **Amazon API Gateway** | HTTP API Gateway | Pairs with Lambda, pay-per-invocation |
| **Amazon S3** | Raw document storage | Negligible storage cost |
| **Aurora Serverless (pgvector)** | Vector database | Scales to zero, ideal for testing |
| **Amazon Bedrock** | LLM API (optional) | Pay-per-token, no reserved cost |

---

## 🏗️ System Architecture (Data Flow)

### Overall Architecture
```text
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                    Frontend                                         │
│                              (Web App / Mobile / CLI)                               │
└─────────────────────────────────────┬───────────────────────────────────────────────┘
                                      │ HTTP / REST API
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              Spring Boot Backend                                    │
│                                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────────────┐   │
│  │                          Controller Layer                                    │   │
│  │  ┌────────────────────┐  ┌────────────────────┐  ┌────────────────────┐    │   │
│  │  │ DocumentController │  │ SessionController  │  │ GenerateController │    │   │
│  │  │  (Upload/Delete)   │  │ (Create/Switch)    │  │ (Generate/Download)│    │   │
│  │  └────────────────────┘  └────────────────────┘  └────────────────────┘    │   │
│  └─────────────────────────────────┬───────────────────────────────────────────┘   │
│                                    │                                               │
│  ┌─────────────────────────────────▼───────────────────────────────────────────┐   │
│  │                           Service Layer                                      │   │
│  │  ┌─────────────────────┐  ┌─────────────────────┐  ┌─────────────────────┐ │   │
│  │  │DocumentProcessing   │  │  RetrievalService   │  │AgentOrchestration   │ │   │
│  │  │  Service            │  │  (Hybrid Search)    │  │  Service            │ │   │
│  │  └──────────┬──────────┘  └──────────┬──────────┘  └──────────┬──────────┘ │   │
│  └─────────────┼─────────────────────────┼─────────────────────────┼────────────┘   │
│                │                         │                         │                │
│  ┌─────────────▼─────────────────────────▼─────────────────────────▼────────────┐   │
│  │                           Core Modules (High Cohesion)                       │   │
│  │                                                                              │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐       │   │
│  │  │    Parser   │  │  Retrieval  │  │    Agent    │  │  Renderer   │       │   │
│  │  │   Module    │  │   Module    │  │   Module    │  │   Module    │       │   │
│  │  │             │  │             │  │             │  │             │       │   │
│  │  │ • PDFBox    │  │ • PGvector  │  │ • LangChain │  │ • Flying    │       │   │
│  │  │ • POI       │  │ • Lucene    │  │ • Tools     │  │   Saucer    │       │   │
│  │  │ • Factory   │  │ • Hybrid    │  │ • ChatMem   │  │ • HTML→PDF  │       │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘       │   │
│  └─────────────────────────────────┬───────────────────────────────────────────┘   │
│                                    │                                               │
└────────────────────────────────────┼───────────────────────────────────────────────┘
                                     │
                                     ▼
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              External Systems                                       │
│                                                                                     │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │
│  │  Amazon S3  │  │  PGvector   │  │OpenAI/Ollama│  │  Bedrock    │              │
│  │ (Raw Files) │  │ (Vectors)   │  │   (LLM)     │  │   (LLM)     │              │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘              │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### Core Data Flow (Upload → Generate → PDF)
```text
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                          1. UPLOAD PHASE (Offline)                                  │
└─────────────────────────────────────────────────────────────────────────────────────┘

  User Uploads PDF/PPT/Word
           │
           ▼
  ┌─────────────────────────────────────────────────────────────────────────────────┐
  │  DocumentParserService.extractText(file)                                       │
  │  ┌────────────┐  ┌────────────┐  ┌────────────┐                               │
  │  │  PDFBox    │  │    POI     │  │  Factory   │                               │
  │  │ (PDF)      │  │ (PPT/Word) │  │ (Strategy) │                               │
  │  └────────────┘  └────────────┘  └────────────┘                               │
  └──────────────────────────────────────┬──────────────────────────────────────────┘
                                         │ rawText (String)
                                         ▼
  ┌─────────────────────────────────────────────────────────────────────────────────┐
  │  TextChunkerService.chunkText(rawText)                                         │
  │  • Chunk Size: ~500 characters                                                 │
  │  • Overlap: 50 characters                                                      │
  │  • Output: List<String> chunks                                                │
  └──────────────────────────────────────┬──────────────────────────────────────────┘
                                         │ chunks
                                         ▼
  ┌─────────────────────────────────────────────────────────────────────────────────┐
  │  EmbeddingService.embed(chunks) → VectorStoreService.store(vectors)           │
  │  ┌────────────────────┐                                                        │
  │  │  PGvector Database │                                                        │
  │  │  (Vector + Metadata)│                                                        │
  │  └────────────────────┘                                                        │
  └─────────────────────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────────────────┐
│                          2. GENERATION PHASE (Online)                               │
└─────────────────────────────────────────────────────────────────────────────────────┘

  User Input: "Focus on Chapter 3, generate double-column cheatsheet"
           │
           ▼
  ┌─────────────────────────────────────────────────────────────────────────────────┐
  │  RetrievalService.hybridSearch(query)                                          │
  │  ┌────────────────────────────────────┐  ┌────────────────────────────────┐   │
  │  │  VectorStoreService.similarity()   │  │  KeywordSearchService.bm25()  │   │
  │  │  (Semantic Search, Top-20)         │  │  (Exact Match, Top-20)         │   │
  │  └─────────────────┬──────────────────┘  └───────────────┬────────────────┘   │
  │                    └─────────────────┬──────────────────┘                       │
  │                                      ▼                                          │
  │                    ┌────────────────────────────────────┐                       │
  │                    │  HybridSearchService.merge()      │                       │
  │                    │  (RRF - Reciprocal Rank Fusion)   │                       │
  │                    │  Output: Top-5 relevant chunks    │                       │
  │                    └────────────────────────────────────┘                       │
  └──────────────────────────────────────┬──────────────────────────────────────────┘
                                         │ relevantChunks
                                         ▼
  ┌─────────────────────────────────────────────────────────────────────────────────┐
  │  AgentOrchestrationService.execute(query, relevantChunks)                      │
  │                                                                                 │
  │  ┌───────────────────────────────────────────────────────────────────────────┐ │
  │  │                      CheatsheetAgent (LangChain4j)                       │ │
  │  │                                                                           │ │
  │  │  System Prompt: "You are an expert at creating compact exam cheatsheets." │ │
  │  │                                                                           │ │
  │  │  Tools Available:                                                         │ │
  │  │  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐       │ │
  │  │  │  SummarizeTool   │  │ ExtractTableTool │  │  FormatHtmlTool  │       │ │
  │  │  │  (Abbreviate)    │  │ (Extract Tables) │  │ (Compact Layout) │       │ │
  │  │  └──────────────────┘  └──────────────────┘  └──────────────────┘       │ │
  │  │                                                                           │ │
  │  │  Agent autonomously decides which tools to call (ReAct Loop)              │ │
  │  └───────────────────────────────────────────────────────────────────────────┘ │
  │                                                                                 │
  │  Output: Compact HTML String (double-column, small font, table-based layout)   │
  └──────────────────────────────────────┬──────────────────────────────────────────┘
                                         │ htmlContent
                                         ▼
  ┌─────────────────────────────────────────────────────────────────────────────────┐
  │  PdfRenderingService.renderPdf(htmlContent)                                    │
  │                                                                                 │
  │  ┌───────────────────────────────────────────────────────────────────────────┐ │
  │  │  Flying Saucer (OpenHTMLtoPDF)                                            │ │
  │  │  • Parses HTML + CSS                                                      │ │
  │  │  • Applies double-column layout (column-count: 2)                         │ │
  │  │  • Enforces small font size (8-9pt)                                       │ │
  │  │  • Outputs PDF byte stream                                                │ │
  │  └───────────────────────────────────────────────────────────────────────────┘ │
  └──────────────────────────────────────┬──────────────────────────────────────────┘
                                         │ pdfBytes
                                         ▼
                       Return to Frontend as downloadable PDF


┌─────────────────────────────────────────────────────────────────────────────────────┐
│                          3. POST-GENERATION CHECK                                  │
└─────────────────────────────────────────────────────────────────────────────────────┘

  ┌─────────────────────────────────────────────────────────────────────────────────┐
  │  Page Count Check                                                              │
  │                                                                                 │
  │  ┌───────────────────────────────────────────────────────────────────────────┐ │
  │  │  if (pageCount > 2) {                                                     │ │
  │  │      // Option 1: Recursively call SummarizeTool to compress further     │ │
  │  │      // Option 2: Physically truncate and mark with "[TRUNCATED]"        │ │
  │  │      // Option 3: Ask user for preference (future enhancement)           │ │
  │  │  }                                                                        │ │
  │  └───────────────────────────────────────────────────────────────────────────┘ │
  └─────────────────────────────────────────────────────────────────────────────────┘
```

### Module Interaction Diagram (Sequence View)

Frontend    Controller    Service    Parser    Retrieval    Agent    Renderer    DB
   │            │           │          │           │         │         │         │
   │──Upload───▶│           │          │           │         │         │         │
   │            │──extract─▶│─────────▶│           │         │         │         │
   │            │           │          │──text────▶│         │         │         │
   │            │           │──chunk──▶│           │         │         │         │
   │            │           │──embed────────────────────────▶│         │         │
   │            │           │─────────────store───────────────────────────────────▶│
   │◀──Success──│           │          │           │         │         │         │
   │            │           │          │           │         │         │         │
   │──Generate─▶│           │          │           │         │         │         │
   │            │──search──────────────────────────▶│         │         │         │
   │            │           │          │           │──query─▶│         │         │
   │            │           │          │           │◀─results│         │         │
   │            │──execute───────────────────────────────────▶│         │         │
   │            │           │          │           │         │──tool──▶│         │
   │            │           │          │           │         │◀─html───│         │
   │            │──render──────────────────────────────────────▶│         │         │
   │            │           │          │           │         │         │──pdf──▶│
   │◀──PDF──────│           │          │           │         │         │         │
   │            │           │          │           │         │         │         │


---

## 🔄 Core Flow Walkthrough (Upload → PDF)

| Step | Trigger | Execution Logic | Modules Involved |
| :--- | :--- | :--- | :--- |
| 1 | User uploads PDF | Parse text → Chunk → Generate embeddings → Store in PGvector | `parser/` + `retrieval/` |
| 2 | User inputs query/scope | Hybrid retrieval (vector + BM25) → Return Top-5 relevant chunks | `retrieval/` |
| 3 | Agent receives results | Autonomously decides: call "Summarize" / "Extract Table" / "Format HTML" tools | `agent/` |
| 4 | Agent outputs HTML | Generates compact HTML (double-column, small font, table layout) | `agent/tools/` |
| 5 | Backend renders PDF | Flying Saucer converts HTML to PDF byte stream | `renderer/` |
| 6 | Returns to frontend | Provides PDF download link or directly streams bytes | `controller/` |


---


# Project Structure

This section tracks the real repository structure. Future modules should be added here only when they are implemented.

## Current Structure

```text
StudySheet-AI/
├── README.md
├── .gitignore
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/studysheetai/
│       │   │       ├── StudySheetAiApplication.java
│       │   │       ├── chunking/       # Text chunking logic for RAG ingestion
│       │   │       │   ├── ChunkingProperties.java
│       │   │       │   ├── TextChunk.java
│       │   │       │   └── TextChunkerService.java
│       │   │       ├── config/         # Spring/application configuration
│       │   │       ├── controller/     # REST API entrypoints
│       │   │       │   ├── DocumentController.java
│       │   │       │   └── SearchController.java
│       │   │       ├── dto/            # API request/response objects
│       │   │       │   ├── ChunkPreview.java
│       │   │       │   ├── DocumentChunkResponse.java
│       │   │       │   ├── DocumentProcessingResult.java
│       │   │       │   ├── ErrorResponse.java
│       │   │       │   ├── SearchRequest.java
│       │   │       │   ├── SearchResponse.java
│       │   │       │   └── SearchResult.java
│       │   │       ├── entity/         # JPA entities
│       │   │       │   ├── Document.java
│       │   │       │   └── DocumentChunk.java
│       │   │       ├── exception/      # Application error handling
│       │   │       │   ├── DocumentProcessingException.java
│       │   │       │   ├── GlobalExceptionHandler.java
│       │   │       │   ├── InvalidDocumentException.java
│       │   │       │   └── ResourceNotFoundException.java
│       │   │       ├── parser/         # PDF/document parsing logic
│       │   │       │   ├── ParsedPdfPage.java
│       │   │       │   └── PdfParserService.java
│       │   │       ├── repository/     # Spring Data repositories
│       │   │       │   ├── DocumentChunkRepository.java
│       │   │       │   └── DocumentRepository.java
│       │   │       ├── retrieval/      # Keyword retrieval for study scopes
│       │   │       │   ├── KeywordRetrievalService.java
│       │   │       │   ├── RetrievalMode.java
│       │   │       │   └── RetrievedChunk.java
│       │   │       └── service/        # Business orchestration services
│       │   │           ├── DocumentProcessingRequest.java
│       │   │           ├── DocumentProcessingService.java
│       │   │           └── LocalPdfIngestionRunner.java
│       │   └── resources/
│       │       ├── application.yml
│       │       └── application-dev.yml
│       └── test/
│           ├── java/
│           │   └── com/studysheetai/
│           │       ├── chunking/
│           │       │   └── TextChunkerServiceTest.java
│           │       ├── controller/
│           │       │   ├── DocumentControllerTest.java
│           │       │   ├── HealthEndpointTest.java
│           │       │   └── SearchControllerTest.java
│           │       ├── parser/
│           │       │   └── PdfParserServiceTest.java
│           │       ├── repository/
│           │       │   └── DocumentRepositoryTest.java
│           │       ├── retrieval/
│           │       │   └── KeywordRetrievalServiceTest.java
│           │       ├── service/
│           │       │   └── DocumentProcessingServiceTest.java
│           │       └── testutil/
│           │           └── PdfTestFixtures.java
│           └── resources/
│               ├── application-test.yml
│               ├── mockito-extensions/
│               │   └── org.mockito.plugins.MockMaker
│               └── test-data/          # Repeatable test PDF fixtures
└── docs/
    ├── pdf-parsing-chunking.md
    └── rag-first-roadmap.md
```

## Planned Additions

The README architecture still points toward a full RAG + agent system, but these directories will be added incrementally as the project reaches each phase:

- Maven wrapper for reproducible local builds.
- More backend classes inside the current packages as retrieval and generation are added.
- Vector search and hybrid retrieval upgrades inside `retrieval/`.
- `agent/` for LangChain4j orchestration and tool calling.
- `renderer/` for compact HTML-to-PDF generation.
- `frontend/` after the backend RAG flow is useful enough to expose through a UI.
- `scripts/`, `.github/`, Docker, and deployment infrastructure after the local backend is stable.

---

# Additional Engineering Features

## User Authentication

Using Spring Security.

Features:
- Registration
- Login
- JWT Authentication
- Protected Endpoints

Benefits:
- Multi-user support
- Production-style architecture
- Better system design experience

---

## Retrieval Analytics Dashboard

Track:

- Number of uploaded documents
- Query count
- Retrieval latency
- Most searched topics

Benefits:
- Demonstrates observability
- Shows understanding of production systems

---

## Query History

Store:

- User Question
- Retrieved Chunks
- Generated Response
- Retrieval Latency
- Token Usage

Benefits:
- Enables evaluation
- Supports future optimization

---

## Admin Metrics

Track:

- Average Retrieval Time
- Average Generation Time
- Total Documents
- Storage Usage

Benefits:
- Adds production engineering depth
- Creates strong discussion points during interviews

---

# Retrieval Pipeline

```text
Upload PDF
      │
      ▼
Text Extraction
      │
      ▼
Chunking
      │
      ▼
Embedding Generation
      │
      ▼
Vector Storage
      │
      ▼
User Question
      │
      ▼
Similarity Search
      │
      ▼
Top-K Relevant Chunks
      │
      ▼
Prompt Construction
      │
      ▼
LLM Generation
      │
      ▼
Answer + Citations
```


## 🚀 Roadmap (MVP → Full Version)

| Phase | Goal | Key Technologies |
| :--- | :--- | :--- |
| **Phase 0 (MVP)** | Upload PDF → Keyword retrieval → LLM generates plain-text answer (console output) | PDFBox, Keyword matching, OpenAI/Ollama |
| **Phase 1** | Upgrade to vector retrieval + BM25 hybrid search, introduce PGvector | PGvector, Lucene |
| **Phase 2** | Introduce LangChain4j Agent, implement tool calling (Summarize/Table Extraction) | LangChain4j, @Tool annotation |
| **Phase 3** | HTML → Compact PDF rendering, support double-column & small fonts | Flying Saucer, CSS |
| **Phase 4** | Multi-turn dialogue + Project persistence (Session management) | Spring Data JPA, ChatMemory |
| **Phase 5** | AWS deployment (Lambda + API Gateway + S3 + Aurora Serverless) | AWS SDK, Serverless |
