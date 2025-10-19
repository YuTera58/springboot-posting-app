package com.example.postingapp.event;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.postingapp.entity.User;
import com.example.postingapp.service.VerificationTokenService;

// Listenerクラスには@Componentアノテーションをつけ、ListenerクラスのインスタンスがDIコンテナに登録されるようにします。
// こうすることで、後述する@EventListenerアノテーションがついたメソッドをSpring Boot側が自動的に検出し、イベント発生時に実行してくれます。
@Component
public class SignupEventListener {
    private final VerificationTokenService verificationTokenService;
    private final JavaMailSender javaMailSender;

    public SignupEventListener(VerificationTokenService verificationTokenService, JavaMailSender mailSender) {
        this.verificationTokenService = verificationTokenService;
        this.javaMailSender = mailSender;
    }

    // Listenerクラス内では、イベント発生時に実行したいメソッドに対して@EventListenerアノテーションをつけます。
    @EventListener
    private void onSignupEvent(SignupEvent signupEvent) {
        User user = signupEvent.getUser();
        String token = UUID.randomUUID().toString();
        verificationTokenService.create(user, token);

        String senderAddress = "springboot.postingapp@example.com";
        String recipientAddress = user.getEmail();
        String subject = "メール認証";
        String confirmationUrl = signupEvent.getRequestUrl() + "/verify?token=" + token;
        String message = "以下のリンクをクリックして会員登録を完了してください。";

        // SimpleMailMessageクラスを使ってメール内容を作成する
        // Spring Frameworkが提供するSimpleMailMessageクラスを使うことで、シンプルなメールメッセージをオブジェクトとして作成できます。
        // 今回使っているのは以下のメソッドです。
        // setFrom()：送信元のメールアドレスをセットする
        // setTo()：送信先のメールアドレスをセットする
        // setSubject()：件名をセットする
        // setText()：本文をセットする
        // なお、作成したオブジェクトは後述するjavaMailSenderインターフェースのsend()メソッドに渡します。
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(senderAddress);
        mailMessage.setTo(recipientAddress);
        mailMessage.setSubject(subject);
        mailMessage.setText(message + "\n" + confirmationUrl);
        javaMailSender.send(mailMessage);
    }
}