package org.example;

import org.example.DocumentManager.Author;
import org.example.DocumentManager.Document;
import org.example.DocumentManager.SearchRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class DocumentManagerTest {

    private DocumentManager documentManager;

    @BeforeEach
    void setUp() {

        Map<String, Document> initialDocuments = new HashMap<>();

        Author author1 = Author.builder().id("1").name("Author One").build();
        Author author2 = Author.builder().id("2").name("Author Two").build();
        Author author3 = Author.builder().id("3").name("Author Three").build();
        Author author4 = Author.builder().id("4").name("Author Four").build();

        Document doc1 = Document.builder()
                .id("doc1")
                .title("Introduction to Java")
                .content("Java is a high-level programming language.")
                .author(author1)
                .created(Instant.parse("2023-01-01T10:00:00Z"))
                .build();

        Document doc2 = Document.builder()
                .id("doc2")
                .title("Spring Framework")
                .content("Spring is a popular Java framework.")
                .author(author2)
                .created(Instant.parse("2023-06-01T12:00:00Z"))
                .build();

        Document doc3 = Document.builder()
                .id("doc3")
                .title("Microservices Architecture")
                .content("Microservices are a type of architectural style.")
                .author(author3)
                .created(Instant.parse("2023-03-15T08:30:00Z"))
                .build();
        Document doc4 = Document.builder()
                .id("doc4")
                .title("Docker Basics")
                .content("Docker is a tool designed to make it easier to create, deploy, and run applications.")
                .author(author4)
                .created(Instant.parse("2023-07-20T14:45:00Z"))
                .build();

        initialDocuments.put(doc1.getId(), doc1);
        initialDocuments.put(doc2.getId(), doc2);
        initialDocuments.put(doc3.getId(), doc3);
        initialDocuments.put(doc4.getId(), doc4);

        documentManager = new DocumentManager(initialDocuments);

    }

    @Test
    void save_Works_Properly() {
        Author author4 = Author.builder().id("4").name("Author Four").build();
        Document doc4 = Document.builder()
                .title("Docker Basics")
                .content("Docker is a tool designed to make it easier to create, deploy, and run applications.")
                .author(author4)
                .created(Instant.parse("2023-07-20T14:45:00Z"))
                .build();

        documentManager.save(doc4);

        Document doc5 = Document.builder()
                .id(doc4.getId())
                .title("Docker Basics")
                .content("Docker is a tool designed to make it easier to create, deploy, and run applications.")
                .author(author4)
                .created(Instant.parse("2023-07-20T14:45:00Z"))
                .build();

        assertEquals(doc4, doc5);

    }

    @Test
    void search_nullRequest_returnsAllDocuments() {
        List<Document> results = documentManager.search(null);
        assertEquals(4, results.size());
    }

    @Test
    void search_byTitlePrefix_returnsMatchingDocuments() {
        SearchRequest request = SearchRequest.builder()
                .titlePrefixes(List.of("Introduction", "Docker"))
                .build();

        List<Document> results = documentManager.search(request);
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(doc -> doc.getTitle().equals("Introduction to Java")));
        assertTrue(results.stream().anyMatch(doc -> doc.getTitle().equals("Docker Basics")));
    }

    @Test
    void search_byContentSubstring_returnsMatchingDocuments() {
        SearchRequest request = SearchRequest.builder()
                .containsContents(List.of("Java", "framework"))
                .build();

        List<Document> results = documentManager.search(request);
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(doc -> doc.getContent().contains("Java")));
        assertTrue(results.stream().anyMatch(doc -> doc.getContent().contains("framework")));
    }

    @Test
    void search_byAuthorId_returnsMatchingDocuments() {
        DocumentManager.SearchRequest request = SearchRequest.builder()
                .authorIds(List.of("1", "3"))
                .build();

        List<Document> results = documentManager.search(request);
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(doc -> doc.getAuthor().getId().equals("1")));
        assertTrue(results.stream().anyMatch(doc -> doc.getAuthor().getId().equals("3")));
    }

    @Test
    void search_byCreationDateRange_returnsMatchingDocuments() {
        SearchRequest request = SearchRequest.builder()
                .createdFrom(Instant.parse("2023-03-01T00:00:00Z"))
                .createdTo(Instant.parse("2023-07-01T00:00:00Z"))
                .build();

        List<Document> results = documentManager.search(request);
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(doc -> doc.getCreated().isAfter(Instant.parse("2023-03-01T00:00:00Z"))));
        assertTrue(results.stream().anyMatch(doc -> doc.getCreated().isBefore(Instant.parse("2023-07-01T00:00:00Z"))));
    }

    @Test
    void findById_Works_Properly() {
        Author author1 = Author.builder().id("1").name("Author One").build();
        Document doc1 = Document.builder()
                .id("doc1")
                .title("Introduction to Java")
                .content("Java is a high-level programming language.")
                .author(author1)
                .created(Instant.parse("2023-01-01T10:00:00Z"))
                .build();
        Assertions.assertEquals(documentManager.findById("doc1"), Optional.of(doc1));

    }
}