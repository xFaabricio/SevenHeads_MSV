package br.com.sevenheads.userService.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "sh_product")
public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = -6280465386650043243L;

    @Id
    @GeneratedValue
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private UUID id;

    @Column
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String pictureURL;

    @Column(columnDefinition = "TEXT")
    private String htmlEN;

    @Column(columnDefinition = "TEXT")
    private String htmlPT;

    @Column(columnDefinition = "TEXT")
    private String html;

    @Column
    private int accessCount;

    @Column
    private Boolean useCustomRedirect;

    @Column
    private String customRedirectPT;

    @Column
    private String customRedirectEN;

    @Column
    private String customRedirect;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "sh_product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @JsonIgnore // Evita loop na serialização JSON
    private Set<Category> categories;
}