# RAG-First Learning Roadmap

This project is primarily a learning and resume-demonstration project for building a real RAG system. The product idea is StudySheet AI, but the technical center is learning how document ingestion, retrieval, prompt construction, grounded generation, and evaluation work together.

## Guiding Principle

Build the system in thin, working stages. Each stage should produce something demoable and teach one important backend or AI engineering concept.

Avoid adding product polish, frontend complexity, agents, or deployment before the basic RAG loop works end to end.

## Recommended Path

### Step 1: PDF Parsing and Chunking

Goal: Load a PDF, extract text, split it into chunks, and preserve source metadata such as document name and page number.

Teaches:
- File upload or local file loading in Spring Boot
- PDF text extraction with PDFBox
- Chunking strategy
- Metadata design for citations

RAG status: Foundation only. No retrieval or generation yet.

### Step 2: Scope-Based Keyword Retrieval

Goal: Given an exam or study scope, find relevant text chunks across one or more uploaded documents using simple keyword matching.

Teaches:
- Basic retrieval
- Scope-based retrieval for cheatsheet generation
- Separating document ingestion from retrieval
- Why retrieval quality controls later LLM output
- Why cheatsheet RAG values broad coverage more than tiny top-K precision

RAG status: Early RAG-like prototype. Useful for inspecting whether the system can find relevant study material before adding embeddings or LLM generation.

StudySheet AI is not a normal Q&A-only RAG app. A user may upload several PDFs and ask for a cheatsheet based on an exam scope. For this reason, retrieval should support multiple `documentIds` and return enough relevant material for later compression:

```text
exam scope
  -> keyword retrieval across selected documents
  -> broad relevant chunk set
  -> source-ordered output with page citations
```

The first retrieval API is:

```text
POST /api/search
```

It accepts `documentIds`, `scope`, `mode`, `minScore`, and `maxChunks`. `maxChunks` is a safety limit, not the core retrieval strategy.

### Step 3: Vector Embeddings and Vector Search

Goal: Convert chunks into embeddings, store them, and retrieve semantically relevant chunks for a query.

Teaches:
- Embeddings
- Vector similarity search
- PGvector or an equivalent vector store
- Semantic search versus keyword search

RAG status: Core retrieval layer becomes modern RAG-ready.

### Step 4: Full Basic RAG With Citations

Goal: Complete the full loop:

```text
Document
  -> Parse text
  -> Chunk text
  -> Embed chunks
  -> Store chunks and metadata
  -> User query
  -> Retrieve relevant chunks
  -> Build prompt with retrieved context
  -> Generate grounded answer
  -> Return answer with citations
```

Teaches:
- End-to-end RAG architecture
- Grounded answer generation
- Citation-aware responses
- Backend service orchestration

RAG status: This is the first holistic working RAG system. Everything after this is improvement, polish, or production hardening.

### Step 5: Hybrid Retrieval

Goal: Combine vector search with keyword/BM25 search, then merge results with a ranking strategy such as Reciprocal Rank Fusion.

Teaches:
- Why semantic search alone is not always enough
- Exact-match retrieval for formulas, names, acronyms, and definitions
- Retrieval ranking and result merging

RAG status: Quality improvement.

### Step 6: Evaluation and Logging

Goal: Store queries, retrieved chunks, generated answers, retrieval latency, generation latency, and token usage.

Teaches:
- RAG evaluation mindset
- Observability
- Debugging retrieval failures
- Resume-friendly production engineering

RAG status: Quality and engineering maturity improvement.

### Step 7: Agent and Tool Calling

Goal: Add LangChain4j agent behavior where the system can call tools such as summarize, extract table, compress content, or format output.

Teaches:
- Agent orchestration
- Tool calling
- When agents are useful versus unnecessary
- Multi-step AI workflows

RAG status: Advanced extension. Not required for basic RAG.

### Step 8: Cheatsheet PDF Rendering

Goal: Convert generated study content into compact HTML and render it as a printable PDF.

Teaches:
- HTML-to-PDF rendering
- Layout constraints
- Productization of AI output

RAG status: Product feature. Valuable for the StudySheet AI concept, but not core RAG.

### Step 9: Product and Deployment Features

Goal: Add frontend, user authentication, sessions, chat history, analytics dashboard, Docker, AWS deployment, and other production features.

Teaches:
- Full-stack integration
- Production backend patterns
- Deployment and cloud architecture
- Portfolio polish

RAG status: Product and production readiness.

## Main Milestone

The first major milestone is:

> A citation-aware RAG backend that ingests PDFs, chunks them, embeds them, retrieves relevant chunks for a query, and generates grounded answers.

Once this exists, the project already demonstrates the core technical skill: building a working RAG system.

## Resume Positioning

The strongest resume framing should emphasize:

- Built an end-to-end RAG backend using Spring Boot.
- Implemented document ingestion, chunking, embeddings, vector retrieval, prompt construction, and citation-aware generation.
- Improved retrieval quality with hybrid vector and keyword search.
- Added logging and evaluation data for retrieval/debugging analysis.
- Extended the system with agent tool calling and compact PDF generation after the core RAG pipeline was working.
