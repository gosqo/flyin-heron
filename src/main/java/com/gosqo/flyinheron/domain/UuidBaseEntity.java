package com.gosqo.flyinheron.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class UuidBaseEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected String id;
}
