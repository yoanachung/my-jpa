package cache.service;

import cache.domain.Article;
import cache.domain.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public Article getByHash(String hash) {
        return articleRepository.findByHash(hash)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Cacheable(value = "articleList1")
    public Article getByHashInCache(String hash) {
        return articleRepository.findByHash(hash)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Cacheable(value = "articleList2")
    public Article getInitializedByHashInCache(String hash) {
        Article article = articleRepository.findByHash(hash)
                .orElseThrow(EntityNotFoundException::new);

        article.getComments().forEach(Hibernate::initialize);

        return article;
    }
}
