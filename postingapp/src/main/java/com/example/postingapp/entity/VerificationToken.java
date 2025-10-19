package com.example.postingapp.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "verification_tokens")
@Data
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // ユーザーとメール認証用トークンは、「1人のユーザーは1つのメール認証用トークンを持ち、1つのメール認証用トークンは1人のユーザーに属する」という一対一のリレーションシップを持っています。
    // 実際に schema.sqlファイルに記述したSQL文では、外部キーであるuser_idカラムにUNIQUE制約をつけています。
    // エンティティの外部キーに対応するフィールドには@OneToOneアノテーションをつけます。
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "token")
    private String token;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;
}