package com.bncnews.news.controller;

import com.bncnews.news.repository.ArticleRepository;
import com.bncnews.news.entity.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class NewsController {

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping("/api/test")
    public String testConnection() {
        return "Backend is alive and connected to MySQL!";
    }

    @GetMapping("/api/articles")
    public List<Article> getArticles() {
        return articleRepository.findAll();
    }

    @PostMapping("/api/articles")
    public ResponseEntity<Article> createArticle(@RequestBody Article newArticle) {
        // THIS IS THE DEBUG LINE
        logger.info("INCOMING ARTICLE DATA: Title={}, ImageURL={}", newArticle.getTitle(), newArticle.getImageUrl());

        if (newArticle.getImageUrl() == null) {
            logger.warn("WARNING: ImageURL is null! Check JSON payload from frontend.");
        }

        newArticle.setStatus("pending");
        Article savedArticle = articleRepository.save(newArticle);
        return new ResponseEntity<>(savedArticle, HttpStatus.CREATED);
    }

    @GetMapping("/api/articles/pending")
    public List<Article> getPendingArticles() {
        return articleRepository.findByStatus("pending");
    }

    @PostMapping("/api/articles/{id}/publish")
    public ResponseEntity<String> publishArticle(@PathVariable Long id) {
        Article article = articleRepository.findById(id).orElseThrow();
        article.setStatus("published");
        articleRepository.save(article);
        return ResponseEntity.ok("Article published!");
    }

    @PutMapping("/api/articles/{id}/status")
    public ResponseEntity<String> updateArticleStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Article article = articleRepository.findById(id).orElseThrow(() -> new RuntimeException("Article not found"));
        String newStatus = body.get("status");
        article.setStatus(newStatus);
        if (body.containsKey("remark")) {
            article.setRemark(body.get("remark"));
        }
        articleRepository.save(article);
        return ResponseEntity.ok("Article status updated to: " + newStatus);
    }

    @GetMapping("/api/articles/{id}")
    public Article getArticleById(@PathVariable Long id) {
        return articleRepository.findById(id).orElseThrow();
    }
}