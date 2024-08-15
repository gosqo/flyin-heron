package com.vong.manidues.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Builder
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "member_id_comment_id_unique"
                , columnNames = {"member_id", "comment_id"}
        )
)
public class CommentLike extends IdentityBaseEntity {
    @ManyToOne(
            targetEntity = Comment.class
            , optional = false
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
    )
    @JoinColumn(
            name = "member_id"
            , nullable = false
            , referencedColumnName = "id"
            , foreignKey = @ForeignKey(name = "fk_comment_like_member_member_id")
    )
    private Member member;

    protected void prePersist() {
        super.status = EntityStatus.ACTIVE;
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
