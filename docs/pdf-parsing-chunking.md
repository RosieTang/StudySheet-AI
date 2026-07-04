# PDF Parsing and Chunking Milestone

This milestone implements the first durable RAG ingestion path:

```text
PDF file
  -> page-aware text extraction
  -> line-aware chunking
  -> H2 persistence
  -> API/CLI inspection
  -> scope-based keyword retrieval
```

It includes basic keyword retrieval, but does not include embeddings, vector search, LLM generation, agents, or frontend UI yet.

## Run the Backend

From the `backend/` directory:

```bash
mvn spring-boot:run
```

The app starts on `http://localhost:8080`.

Check backend health:

```bash
curl http://localhost:8080/actuator/health
```

Expected response:

```json
{"status":"UP"}
```

The dev H2 console is available at:

```text
http://localhost:8080/h2-console
```

Use this JDBC URL:

```text
jdbc:h2:file:./data/studysheet-ai-dev
```

## Parse a PDF Through the API

```bash
curl -F "file=@/absolute/path/to/lecture.pdf" \
  http://localhost:8080/api/documents/parse
```

The response includes the saved document id, page count, chunk count, and a few chunk previews.

## Inspect Saved Chunks

```bash
curl http://localhost:8080/api/documents/{documentId}/chunks
```

Each chunk includes:

- `documentId`
- `chunkIndex`
- `pageNumber`
- `characterCount`
- `content`

These fields are the citation foundation for later RAG retrieval.

## Search Relevant Chunks Across Documents

```bash
curl -X POST http://localhost:8080/api/search \
  -H "Content-Type: application/json" \
  -d '{
    "documentIds": [1, 2],
    "task": "Generate a compact exam cheatsheet",
    "scope": "KNN, Naive Bayes, distance functions, Bayes rule, posterior probability, precision and recall",
    "mode": "CHEATSHEET",
    "minScore": 1,
    "maxChunks": 40
  }'
```

The response returns relevant chunks with document name, page number, chunk index, matched terms, and content. This is still keyword retrieval, not vector search.

## Parse a PDF Through the CLI Runner

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--app.ingest=/absolute/path/to/lecture.pdf"
```

This runs the same `DocumentProcessingService` as the API and prints a short ingestion summary.

## Why This Matters for RAG

This milestone creates the raw knowledge units that retrieval will search later.

The important design choices are:

- Keep page numbers from the parser so future answers can cite sources.
- Chunk within page boundaries so unrelated pages do not merge.
- Group short lines and bullets together so lecture slides keep enough context.
- Store chunks in a database so retrieval can operate over persisted text.
- Search across selected documents so future project/session retrieval can reuse the same core.
