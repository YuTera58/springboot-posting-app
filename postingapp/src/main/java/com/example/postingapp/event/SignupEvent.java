package com.example.postingapp.event;

import org.springframework.context.ApplicationEvent;

import com.example.postingapp.entity.User;

import lombok.Getter;

// 一般的に、EventクラスはApplicationEventクラスを継承して作成します。
// ApplicationEventクラスはイベントを作成するための基本的なクラスで、イベントのソース（発生源） などを保持します。
// Eventクラスはイベントに関する情報を保持するので、外部（具体的にはListenerクラス）からそれらの情報を取得できるようにゲッターを定義します。
// Lombokが提供する@Getterアノテーションをクラスにつけることで、ゲッターのみが自動的に定義されます。
@Getter
public class SignupEvent extends ApplicationEvent {
    private User user;
    private String requestUrl;

    // Eventクラスの役割はListenerクラスにイベントが発生したことを知らせることですが、イベントに関する情報も保持できます。
    // このイベントでは、会員登録したユーザーの情報（Userオブジェクト）とリクエストを受けたURL（https://ドメイン名/signup）を保持します。
    // また、superでスーパークラス（親クラス）のコンストラクタを呼び出し、イベントのソース（発生源） を渡します。
    // イベントのソースとは、具体的にはこのあと作成する Publisherクラスのインスタンスのことです。
    public SignupEvent(Object source, User user, String requestUrl) {
        super(source);

        this.user = user;
        this.requestUrl = requestUrl;
    }
}