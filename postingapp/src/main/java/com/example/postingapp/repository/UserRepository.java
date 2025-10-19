package com.example.postingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.postingapp.entity.User;

//JpaRepository<エンティティのクラス型, 主キーのデータ型>
public interface UserRepository extends JpaRepository<User, Integer> {
	// メールアドレスでユーザーを検索するシンプルなメソッド
	public User findByEmail(String email);
}