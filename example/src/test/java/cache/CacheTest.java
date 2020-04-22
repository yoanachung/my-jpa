package cache;

import cache.domain.Article;
import cache.domain.ArticleRepository;
import cache.domain.Comment;
import cache.service.ArticleService;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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

    @Nested
    @DisplayName("조회 엔티티 캐시하지 않음")
    class GetWithoutCache {

        @Test
        @DisplayName("기본 조회")
        void getByHash() {
            Article article1 = articleService.getByHash("hash 1");
            Article article2 = articleService.getByHash("hash 1");

            assertThat(article1.getId()).isEqualTo(article2.getId());
        }

        @Test
        @DisplayName("트랜잭션 외부에서 엔티티 지연로딩시 LazyInitializationException이 발생한다")
        void getByHashLazyLoad() {
            Article article = articleService.getByHash("hash 1");

            assertThrows(LazyInitializationException.class, () -> {
                String comment = article.getComments().get(0).getContent();
            });
        }

        @Test
        @Transactional
        @DisplayName("트랜잭션 내부에서 엔티티 지연로딩시 LazyInitializationException이 발생하지 않는다")
        void getByHashLazyLoadInTransaction() {
            Article article = articleService.getByHash("hash 1");
            String comment = article.getComments().get(0).getContent();
            assertThat(comment).isEqualTo("test comment 1");
        }
    }

    @Nested
    @DisplayName("조회 엔티티 캐시")
    class GetWithCache {

        @Test
        @DisplayName("기본 조회")
        void getByHashInCache() {
            Article article1 = articleService.getByHashInCache("hash 1");
            Article article2 = articleService.getByHashInCache("hash 1");

            assertThat(article1.getId()).isEqualTo(article2.getId());
        }

        @Test
        @DisplayName("트랜잭션 외부에서 캐시된 엔티티 지연로딩시 LazyInitializationException이 발생한다")
        void cacheLazyLoadingEntity() {
            Article article1 = articleService.getByHashInCache("hash 1");
            Article article2 = articleService.getByHashInCache("hash 1");

            assertThrows(LazyInitializationException.class, () -> {
                String comment = article2.getComments().get(0).getContent();
            });
        }

        @Test
        @Transactional
        @DisplayName("트랜잭션 내부에서 캐시된 엔티티 지연로딩시 LazyInitializationException이 발생한다")
        void cacheLazyLoadingEntityInTransaction() {
            Article article1 = articleService.getByHashInCache("hash 1");
            Article article2 = articleService.getByHashInCache("hash 1");

            assertThrows(LazyInitializationException.class, () -> {
                String comment2 = article2.getComments().get(0).getContent();
            });
        }

        @Test
        @DisplayName("초기화가 완료된 캐시된 엔티티 사용시 LazyInitializationException이 발생하지 않는다")
        void cacheInitializedEntity() {
            Article article1 = articleService.getInitializedByHashInCache("hash 1");
            Article article2 = articleService.getInitializedByHashInCache("hash 1");

            assertThat(article2.getComments().get(0).getContent()).isEqualTo("test comment 1");
        }
    }
}
