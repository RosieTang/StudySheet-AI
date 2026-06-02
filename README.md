# StudySheet AI
### An AI-Powered Retrieval-Augmented Learning Assistant for Academic Documents

## Overview

StudySheet AI is a Retrieval-Augmented Generation (RAG) platform designed to help students efficiently study large collections of academic materials.

Instead of functioning as a generic "chat with PDF" application, StudySheet AI focuses on a real educational workflow:

1. Upload lecture slides, notes, assignments, and readings.
2. Index and retrieve relevant information using semantic search.
3. Generate grounded answers with citations.
4. Automatically create exam-ready cheat sheets, summaries, flashcards, and study guides.

The goal is to transform scattered course materials into a searchable knowledge base that helps students prepare for exams while remaining grounded in the original source documents.

---

# Key Features

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

---

## Cheat Sheet Generation

Generate condensed study materials under specific constraints:

- One-page exam cheat sheet
- 500-word summary
- Formula sheet
- Topic-focused summary

Example:

> "Generate a one-page midterm cheat sheet covering lectures 1-8."

---

## Flashcard Generation

Automatically create:

- Question-answer flashcards
- Key concept review cards
- Definition cards

Exportable to formats compatible with common spaced repetition tools.

---

## Course Analytics

Analyze uploaded materials to identify:

- Frequently occurring concepts
- Important formulas
- Topic relationships
- Coverage statistics

---

## Citation-Aware Responses

Every answer includes references to source documents and page locations whenever possible.

Example:

> According to Lecture 5, Page 12...

This increases trustworthiness and makes verification easy.

---

# System Architecture

```text
                ┌─────────────────┐
                │     Frontend    │
                │    Next.js UI   │
                └────────┬────────┘
                         │
                         ▼
                ┌─────────────────┐
                │    FastAPI      │
                │  Backend API    │
                └────────┬────────┘
                         │
        ┌────────────────┼────────────────┐
        ▼                ▼                ▼

 ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
 │ PDF Parsing │  │ Embeddings  │  │ Auth/User   │
 │ Processing  │  │ Generation  │  │ Management  │
 └──────┬──────┘  └──────┬──────┘  └─────────────┘
        │                │
        ▼                ▼

 ┌──────────────────────────────────┐
 │        PostgreSQL + pgvector     │
 │   Document Storage + Vectors     │
 └──────────────────────────────────┘
                     │
                     ▼

          ┌────────────────────┐
          │ Retrieval Pipeline │
          └──────────┬─────────┘
                     ▼

             ┌──────────────┐
             │ OpenAI / LLM │
             └──────────────┘
```

---

# Technology Stack

## Frontend

### Next.js

Purpose:

- Application framework
- Routing
- Server-side rendering

Why:

- Industry-standard modern frontend framework
- Strong internship relevance

---

### TypeScript

Purpose:

- Type safety
- Better maintainability

Why:

- Widely used in production environments

---

### Tailwind CSS

Purpose:

- Styling
- Responsive design

Why:

- Fast development
- Modern UI ecosystem

---

### shadcn/ui

Purpose:

- Reusable UI components

Examples:

- Chat interface
- File upload
- Tables
- Dialogs

---

# Backend

## FastAPI

Purpose:

- REST API
- Document processing
- Retrieval orchestration

Why:

- Excellent Python ecosystem integration
- High performance
- Easy deployment

---

# Database

## PostgreSQL

Purpose:

- User management
- Metadata storage
- Conversation history

---

## pgvector

Purpose:

- Embedding storage
- Similarity search

Why:

- Production-friendly
- Simpler deployment than separate vector databases

---

# AI Stack

## OpenAI Embeddings

Purpose:

- Semantic document indexing

Example:

- text-embedding-3-large

---

## OpenAI GPT Models

Purpose:

- Answer generation
- Summarization
- Cheat sheet creation

---

# Infrastructure

## Docker

Purpose:

- Consistent deployment
- Reproducible environments

---

## Vercel

Purpose:

- Frontend deployment

---

## Railway / Render

Purpose:

- Backend deployment

---

# Project Structure

```text
studysheet-ai/

├── frontend/
│
│   ├── app/
│   ├── components/
│   ├── lib/
│   ├── hooks/
│   └── types/
│
├── backend/
│
│   ├── api/
│   ├── services/
│   ├── retrieval/
│   ├── embeddings/
│   ├── parsers/
│   ├── models/
│   └── utils/
│
├── database/
│
│   ├── migrations/
│   └── schema/
│
├── evaluation/
│
│   ├── retrieval_tests/
│   ├── benchmarks/
│   └── metrics/
│
├── docs/
│
├── docker/
│
└── README.md
```

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

## Week 1 — Planning & Setup

### Goals

- Finalize project scope
- Create repository structure
- Set up development environment

### Deliverables

- GitHub repository
- Next.js frontend
- FastAPI backend
- PostgreSQL instance
- Docker setup

---

## Week 2 — Document Processing Pipeline

### Goals

Implement document ingestion.

### Tasks

- PDF upload
- Text extraction
- Metadata extraction
- File storage

### Deliverables

- Working upload flow
- Parsed document output

---

## Week 3 — Embeddings & Vector Search

### Goals

Create knowledge base.

### Tasks

- Chunking strategy
- Embedding generation
- pgvector integration
- Similarity search

### Deliverables

- Searchable document index

---

## Week 4 — Basic RAG Chat

### Goals

Build MVP.

### Tasks

- User query endpoint
- Retrieval pipeline
- Prompt construction
- LLM integration

### Deliverables

- Ask questions about uploaded documents

---

## Week 5 — Frontend Chat Interface

### Goals

Create usable product.

### Tasks

- Chat UI
- Conversation history
- Loading states
- Streaming responses

### Deliverables

- End-to-end working application

---

## Week 6 — Citation System

### Goals

Improve answer quality.

### Tasks

- Source tracking
- Citation generation
- Page references
- Chunk metadata

### Deliverables

- Grounded responses with citations

---

## Week 7 — Cheat Sheet Generator

### Goals

Build unique feature.

### Tasks

- Summary generation
- Compression prompts
- Formula extraction
- Exam-mode outputs

### Deliverables

- One-page cheat sheet generation

---

## Week 8 — Flashcards & Study Tools

### Goals

Expand learning functionality.

### Tasks

- Flashcard generation
- Concept extraction
- Key-term identification

### Deliverables

- Auto-generated study aids

---

## Week 9 — Evaluation & Optimization

### Goals

Add engineering depth.

### Tasks

- Retrieval benchmarking
- Chunk-size experiments
- Latency measurements
- Cost analysis
- Error tracking

### Deliverables

- Evaluation report
- Performance improvements

---

## Week 10 — Deployment & Resume Polish

### Goals

Productionize project.

### Tasks

- Deploy frontend
- Deploy backend
- Documentation
- Demo video
- Resume bullets

### Deliverables

- Public demo
- GitHub repository
- Technical documentation
- Resume-ready project

---

# Expected Learning Outcomes

By the end of the project, the developer will gain experience with:

- Retrieval-Augmented Generation (RAG)
- Embeddings and Vector Databases
- LLM Application Development
- Full-Stack Engineering
- Prompt Engineering
- Information Retrieval Systems
- Evaluation and Benchmarking
- Cloud Deployment
- Production Software Architecture

---

# Resume Positioning

StudySheet AI demonstrates a combination of:

- Full-stack software engineering
- Applied machine learning
- Information retrieval
- Product design
- Systems architecture

The project serves as a practical example of building an AI-powered application that solves a real-world educational problem while incorporating modern LLM engineering practices.
