package jpabook.jpashop.cache.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article implements Serializable {

    @Id @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    private String hash;
    private String title;
    private String content;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public Article(String hash, String title, String content) {
        this.hash = hash;
        this.title = title;
        this.content = content;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setArticle(this);
    }
}
