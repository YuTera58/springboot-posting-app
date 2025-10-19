package com.example.postingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.postingapp.entity.Role;

//JpaRepository<エンティティのクラス型, 主キーのデータ型>
public interface RoleRepository extends JpaRepository<Role, Integer> {
	public Role findByName(String name);
}