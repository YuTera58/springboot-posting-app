package com.example.postingapp.form;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

//クラスに@AllArgsConstructorアノテーションをつけることで、
//全フィールドに値をセットするための引数つきコンストラクタを自動生成することができます（Lombokの機能）。
@Data
@AllArgsConstructor
public class PostEditForm {
    @NotBlank(message = "タイトルを入力してください。")
    private String title;

    @NotBlank(message = "本文を入力してください。")
    private String content;
}