# StudySheet AI

## Project Vision

**StudySheet AI** is an AI-powered exam preparation platform that transforms course materials into searchable knowledge bases, citation-grounded answers, cheat sheets, and study resources using Retrieval-Augmented Generation (RAG).

Unlike general-purpose document assistants, StudySheet AI focuses specifically on the student workflow of preparing for exams and reviewing large collections of academic content.

The project serves as both an educational productivity tool and an exploration of modern AI system design, information retrieval, and full-stack software engineering.

---

# Tech Stack

## Frontend

### Next.js

Responsibilities:
- Routing
- Dashboard UI
- Chat Interface
- Authentication Pages

### TypeScript

Responsibilities:
- Type Safety
- API Contracts

### Tailwind CSS

Responsibilities:
- Styling
- Responsive Design

### shadcn/ui

Responsibilities:
- Reusable Components
- Tables
- Dialogs
- Forms

---

## Backend

### Java 21

Primary Language

Responsibilities:
- Business Logic
- Retrieval Pipeline
- API Development

Why:
- Industry-standard backend language
- Strong internship relevance
- Opportunity to learn enterprise Java development

### Spring Boot

Responsibilities:
- REST APIs
- Dependency Injection
- Service Architecture
- Configuration Management

Modules:
- Spring Web
- Spring Data JPA
- Spring Security
- Spring Validation

### Spring AI

Responsibilities:
- LLM Integration
- Embeddings
- Prompt Templates
- RAG Components

---

## Database Layer

### PostgreSQL

Stores:
- Users
- Documents
- Metadata
- Conversations
- Retrieval Logs

### pgvector

Stores:
- Document Embeddings
- Semantic Search Index

---

## AI Layer

### OpenAI API

Uses:
- Embedding Generation
- Answer Generation
- Cheat Sheet Creation
- Flashcard Generation

---

## Infrastructure

### Docker

Purpose:
- Containerization

### Vercel

Purpose:
- Frontend Deployment

### Railway / Render

Purpose:
- Backend Deployment

### GitHub Actions

Purpose:
- CI/CD Pipeline

---

# System Architecture

```text
┌─────────────────────────────┐
│        Next.js Frontend     │
└──────────────┬──────────────┘
               │
               ▼

┌─────────────────────────────┐
│      Spring Boot API        │
└──────────────┬──────────────┘
               │

    ┌──────────┼──────────┐
    ▼          ▼          ▼

Document   Retrieval   User/Auth
Service    Service     Service

    │
    ▼

┌─────────────────────────────┐
│         PostgreSQL          │
│         + pgvector          │
└──────────────┬──────────────┘
               │
               ▼

      Semantic Search

               │
               ▼

         Spring AI

               │
               ▼

          OpenAI API
```

---

# Project Structure

```text
studysheet-ai/

├── frontend/
│
│   ├── app/
│   ├── components/
│   ├── hooks/
│   ├── services/
│   └── types/
│
├── backend/
│
│   └── src/main/java/com/studysheet/
│
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── entity/
│       ├── dto/
│       ├── retrieval/
│       ├── embedding/
│       ├── parser/
│       ├── security/
│       ├── config/
│       └── exception/
│
├── database/
│
├── evaluation/
│
├── docker/
│
└── docs/
```

---

# Core Features

## Document Knowledge Base

- Upload PDFs, lecture slides, notes, and study guides
- Automatic document parsing and chunking
- Semantic indexing using embeddings
- Persistent storage of course materials

## Retrieval-Augmented Question Answering

- Natural language querying
- Context-aware retrieval
- Source citations
- Hallucination reduction through document grounding

Example:

> "Explain gradient descent using examples from Lecture 7."

The system retrieves relevant lecture content before generating a response.

## Cheat Sheet Generation

Generate condensed study materials under specific constraints:

- One-page exam cheat sheet
- 500-word summary
- Formula sheet
- Topic-focused summary

Example:

> "Generate a one-page midterm cheat sheet covering lectures 1-8."

## Flashcard Generation

Automatically create:

- Question-answer flashcards
- Key concept review cards
- Definition cards

Exportable to formats compatible with common spaced repetition tools.

## Course Analytics

Analyze uploaded materials to identify:

- Frequently occurring concepts
- Important formulas
- Topic relationships
- Coverage statistics

## Citation-Aware Responses

Every answer includes references to source documents and page locations whenever possible.

Example:

> According to Lecture 5, Page 12...

This increases trustworthiness and makes verification easy.

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

---

# 10-Week Development Timeline

## Week 1 — Java & Spring Boot Fundamentals

Goals:
- Learn Spring Boot basics
- Set up project architecture
- Create REST API skeleton

Deliverables:
- Spring Boot application
- GitHub repository
- Docker setup

---

## Week 2 — Database Layer

Goals:
- Set up PostgreSQL
- Learn Spring Data JPA
- Design schema

Tasks:
- User entity
- Document entity
- Conversation entity
- Repository layer

Deliverables:
- Working database integration

---

## Week 3 — Document Ingestion Pipeline

Goals:
- Upload and process PDFs

Tasks:
- PDF upload
- Text extraction
- Metadata extraction
- Storage pipeline

Deliverables:
- Parsed document output

---

## Week 4 — Embeddings & Vector Search

Goals:
- Build semantic retrieval system

Tasks:
- Chunking strategy
- OpenAI embeddings
- pgvector integration
- Similarity search

Deliverables:
- Searchable document index

---

## Week 5 — Basic RAG Chat

Goals:
- Build MVP

Tasks:
- Retrieval pipeline
- Prompt construction
- OpenAI integration
- Chat endpoint

Deliverables:
- Ask questions about uploaded documents

---

## Week 6 — Frontend Development

Goals:
- Build user interface

Tasks:
- Dashboard
- File upload
- Chat interface
- API integration

Deliverables:
- Full-stack working application

---

## Week 7 — Authentication & Citations

Goals:
- Improve security and answer quality

Tasks:
- JWT Authentication
- Spring Security
- Citation generation
- Source tracking

Deliverables:
- User accounts
- Citation-aware responses

---

## Week 8 — Cheat Sheet Generator

Goals:
- Build differentiation feature

Tasks:
- Exam cheat sheet generation
- Formula extraction
- Summary generation
- Study guide generation

Deliverables:
- One-page exam cheat sheet feature

---

## Week 9 — Evaluation & Optimization

Goals:
- Add engineering depth

Tasks:
- Retrieval benchmarking
- Latency measurements
- Cost analysis
- Chunk-size experiments

Deliverables:
- Evaluation report
- Performance improvements

---

## Week 10 — Deployment & Documentation

Goals:
- Production deployment

Tasks:
- Frontend deployment
- Backend deployment
- Documentation
- Demo video
- Resume preparation

Deliverables:
- Public application
- GitHub repository
- Technical documentation
- Resume-ready project

---

# Success Criteria

## Must Have

- Java 21
- Spring Boot
- PostgreSQL
- pgvector
- OpenAI Integration
- RAG Pipeline
- Authentication
- Citation Support
- Deployment

## Nice To Have

- Flashcards
- Analytics Dashboard
- Hybrid Retrieval
- Reranking
- Evaluation Benchmarks
- Cost Monitoring
- Admin Dashboard

---

# Resume Positioning

StudySheet AI demonstrates:

- Full-Stack Software Engineering
- Java Backend Development
- Spring Boot Architecture
- AI Engineering
- Retrieval-Augmented Generation (RAG)
- Information Retrieval Systems
- Database Design
- Production Deployment
- Evaluation and Benchmarking

Example Resume Bullet:

> Built a full-stack AI-powered exam preparation platform using Java, Spring Boot, PostgreSQL, pgvector, and OpenAI APIs, enabling citation-grounded question answering and automated study guide generation from academic documents.

> Implemented a Retrieval-Augmented Generation (RAG) pipeline with semantic search, vector embeddings, and document chunking to improve answer relevance and reduce hallucinations.

> Designed and deployed a production-style architecture featuring JWT authentication, analytics dashboards, retrieval evaluation, and cloud deployment.
