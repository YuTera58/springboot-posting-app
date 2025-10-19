package com.example.postingapp.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.postingapp.entity.User;

// Publisherクラスはコントローラなど、イベントを発生させたい処理（例：AuthControllerクラスのsignup()メソッド）の中で呼び出して使います。
// そこで、@ComponentアノテーションをつけてDIコンテナに登録し、呼び出すクラス（今回はコントローラ）に対して依存性の注入（DI） を行えるようにします。
@Component
public class SignupEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public SignupEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    // イベントを発行するには、ApplicationEventPublisherインターフェースが提供するpublishEvent()メソッドを使います。
    // 引数には発行したいEventクラスのインスタンス（今回はSignupEventクラスのインスタンス）を渡します。
    // つまり、イベントを発生させたいタイミングで以下のpublishSignupEvent()メソッドを呼び出せばOKです。
    public void publishSignupEvent(User user, String requestUrl) {
    	
    	// なお、SignupEventクラスのコンストラクタの第1引数には、自分自身（SignupEventPublisherクラス）のインスタンスを渡している点に注目してください。
    	// SignupEventクラスには、このインスタンスがイベントのソース（発生源） として渡されます。
        applicationEventPublisher.publishEvent(new SignupEvent(this, user, requestUrl));
    }
}