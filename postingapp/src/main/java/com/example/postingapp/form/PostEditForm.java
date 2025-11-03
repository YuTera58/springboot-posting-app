package com.example.postingapp.form;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

//クラスに@AllArgsConstructorアノテーションをつけることで、
//全フィールドに値をセットするための引数つきコンストラクタを自動生成することができます（Lombokの機能）。
@Data
@AllArgsConstructor
public class PostEditForm {
    @NotBlank(message = "タイトルを入力してください。")
    @Length(max = 40, message = "タイトルは40文字以内で入力してください。")
    private String title;

    @NotBlank(message = "本文を入力してください。")
    @Length(max = 200, message = "本文は200文字以内で入力してください。") //課題用に追加
    private String content;
}