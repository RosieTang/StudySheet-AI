package com.studysheetai.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.studysheetai.entity.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
