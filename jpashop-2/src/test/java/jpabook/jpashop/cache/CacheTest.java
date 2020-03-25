package jpabook.jpashop.cache;

import jpabook.jpashop.cache.domain.Article;
import jpabook.jpashop.cache.domain.ArticleRepository;
import jpabook.jpashop.cache.domain.Comment;
import jpabook.jpashop.cache.service.ArticleService;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CacheTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleService articleService;

    @BeforeAll
    void initDB() {
        articleRepository.deleteAll();
        Article article = new Article("hash 1", "test title 1", "test content 1");
        Comment comment1 = new Comment("test comment 1");
        Comment comment2 = new Comment("test comment 2");
        Comment comment3 = new Comment("test comment 3");
        article.addComment(comment1);
        article.addComment(comment2);
        article.addComment(comment3);
        articleRepository.save(article);
    }

    @Test
    void getByHash() {
        Article article1 = articleService.getByHash("hash 1");
        Article article2 = articleService.getByHash("hash 1");

        assertThat(article1.getId()).isEqualTo(article2.getId());
    }

    @Test
    void getByHashInCache() {
        Article article1 = articleService.getByHashInCache("hash 1");
        Article article2 = articleService.getByHashInCache("hash 1");

        assertThat(article1.getId()).isEqualTo(article2.getId());
    }

    @Test
    void cacheLazyLoadingEntity() {
        Article article1 = articleService.getByHashInCache("hash 1");
        Article article2 = articleService.getByHashInCache("hash 1");

        assertThrows(LazyInitializationException.class, () -> {
            String comment = article2.getComments().get(0).getContent();
        });
    }

    @Test
    void cacheInitializedEntity() {
        Article article1 = articleService.getInitializedByHashInCache("hash 1");
        Article article2 = articleService.getInitializedByHashInCache("hash 1");

        assertThat(article2.getComments().get(0).getContent()).isEqualTo("test comment 1");
    }
}
