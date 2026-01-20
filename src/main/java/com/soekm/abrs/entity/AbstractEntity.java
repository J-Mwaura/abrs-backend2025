package com.soekm.abrs.entity;

import jakarta.persistence.*;
import jakarta.persistence.metamodel.StaticMetamodel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

/**
 * @author atjkm
 *
 */
@StaticMetamodel(AbstractEntity.class)
@MappedSuperclass
public abstract class AbstractEntity {
	
	@Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @Getter
    @Column(name = "date_created", nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime created;

    @Setter
    @Column(name = "date_modified")
    @UpdateTimestamp
    private OffsetDateTime  modified;


}

