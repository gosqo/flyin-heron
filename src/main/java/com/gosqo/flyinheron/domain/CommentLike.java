package com.gosqo.flyinheron.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "member_id_comment_id_unique"
                , columnNames = {"member_id", "comment_id"}
        )
)
@NoArgsConstructor
@AllArgsConstructor
public class CommentLike extends IdentityBaseEntity {
    @ManyToOne(
            targetEntity = Comment.class
            , optional = false
            , fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "comment_id"
            , nullable = false
            , referencedColumnName = "id"
            , foreignKey = @ForeignKey(name = "fk_comment_like_comment_comment_id")
    )
    private Comment comment;

    @ManyToOne(
            targetEntity = Member.class
            , optional = false
            , fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "member_id"
            , nullable = false
            , referencedColumnName = "id"
            , foreignKey = @ForeignKey(name = "fk_comment_like_member_member_id")
    )
    private Member member;

    // 추후 @PrePersist 에 해당 로직이 필요하다면,
    // 상위클래스에 abstract prePersistDetail() 같은 추상 메서드 선언해서
    // 하위 클래스에서 필요한 메서드 구현하고,
    // 상위 클래스에 prePersist 에 prePersistDetail() 메서드 실행을 추가하는 방법도 있음.
    @PostPersist
    public void postPersist() {
        this.comment.addLikeCount();
    }

    @Override
    public String toString() {
        return "CommentLike{" +
                "\nid=" + id +
                "\n, member=" + member +
                "\n, comment=" + comment +
                "\n, status=" + status +
                "\n, registeredAt=" + registeredAt +
                "\n, updatedAt=" + updatedAt +
                "\n, contentModifiedAt=" + contentModifiedAt +
                "\n, deletedAt=" + deletedAt +
                '}';
    }
}
