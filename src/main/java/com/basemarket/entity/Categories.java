//categoriesテーブルに対応・野球用品のカテゴリ情報 
package com.basemarket.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categories {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// カテゴリ名
	@Column(nullable = false, unique = true)
	private String name;

	// このカテゴリに属する商品
	@OneToMany(mappedBy = "category")
	private List<Items> items;
}
