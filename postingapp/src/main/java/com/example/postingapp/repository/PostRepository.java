package com.example.postingapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.postingapp.entity.Post;
import com.example.postingapp.entity.User;

public interface PostRepository extends JpaRepository<Post, Integer> {
	//独自のメソッドの定義方法については、Spring Data JPAの公式リファレンスを参照。
	//Spring Data JPAの公式リファレンス：https://spring.pleiades.io/spring-data/jpa/reference/jpa/query-methods.html
	
	//引数に指定したユーザーに紐づく投稿を作成日時が新しい順に取得します。
	public List<Post> findByUserOrderByCreatedAtDesc(User user);
	
	//課題用に追加：引数に指定したユーザーに紐づく投稿を更新日時が古い順に取得します。
	public List<Post> findByUserOrderByUpdatedAtAsc(User user);
	
	//findFirstByOrderByIdDesc()は idカラムの値で降順に並べ替え、最初の1件を取得するメソッドです。
	//つまり、idが最も大きい投稿を取得できます。
	public Post findFirstByOrderByIdDesc();
}