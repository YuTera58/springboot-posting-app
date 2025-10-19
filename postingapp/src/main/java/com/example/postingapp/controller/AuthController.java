package com.example.postingapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.postingapp.entity.User;
import com.example.postingapp.entity.VerificationToken;
import com.example.postingapp.event.SignupEventPublisher;
import com.example.postingapp.form.SignupForm;
import com.example.postingapp.service.UserService;
import com.example.postingapp.service.VerificationTokenService;

import jakarta.servlet.http.HttpServletRequest;


@Controller
public class AuthController {
    private final UserService userService;
    private final SignupEventPublisher signupEventPublisher;
    private final VerificationTokenService verificationTokenService;

    public AuthController(UserService userService, SignupEventPublisher signupEventPublisher, VerificationTokenService verificationTokenService) {
        this.userService = userService; 
        this.signupEventPublisher = signupEventPublisher;
        this.verificationTokenService = verificationTokenService;
    }
    
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
    
    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("signupForm", new SignupForm());
        return "auth/signup";
    }

    // コントローラ内ではメソッドの引数に@ModelAttributeアノテーションをつけることで、フォームから送信されたデータ（フォームクラスのインスタンス） をその引数にバインドする（割り当てる）ことができます。
    // コントローラ内ではメソッドの引数に@Validatedアノテーションをつけることで、その引数（フォームクラスのインスタンス）に対してバリデーションを行うことができます。
    //  ↳ なお、エラーが存在する場合は後述するBindingResultオブジェクトに格納されます。
    // BindingResultは、バリデーションの結果を保持するためのインターフェースです。メソッドにBindingResult型の引数を設定することで、バリデーションのエラー内容がその引数に格納されます。
    //  ↳ また、BindingResultインターフェースが提供するhasErrors()メソッドを使うことで、エラーが存在するかどうかをチェックすることができます。
    @PostMapping("/signup")
    public String signup(@ModelAttribute @Validated SignupForm signupForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         HttpServletRequest httpServletRequest,
                         Model model)
    {
        // メールアドレスが登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
        if (userService.isEmailRegistered(signupForm.getEmail())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
            bindingResult.addError(fieldError);
        }

        // パスワードとパスワード（確認用）の入力値が一致しなければ、BindingResultオブジェクトにエラー内容を追加する
        if (!userService.isSamePassword(signupForm.getPassword(), signupForm.getPasswordConfirmation())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "password", "パスワードが一致しません。");
            bindingResult.addError(fieldError);
        }

        // if文の条件式にhasErrors()メソッドを使い、エラーが存在する場合は会員登録ページを表示します。
        // なお、会員登録ページではth:errors属性を設定したdiv要素を各フィールドに作成しているので、その位置にエラーメッセージが表示されます
        if (bindingResult.hasErrors()) {
            model.addAttribute("signupForm", signupForm);

            return "auth/signup";
        }

        // エラーが存在しない場合は、サービスクラスに定義したcreateUser()メソッドを実行し、データベースに会員情報を登録します。
        // なお、引数としてフォームクラスのインスタンスを渡します。
        // （旧コード）userService.createUser(signupForm);
        User createdUser = userService.createUser(signupForm);
        
        // コントローラではメソッドの引数でHttpServletRequestオブジェクトを受け取ることで、HTTPリクエストに関するさまざまな情報を取得できる
        String requestUrl = new String(httpServletRequest.getRequestURL());
        
        // SignupEventPublisherクラスに定義したpublishSignupEvent()メソッドを実行すれば、ユーザーの会員登録が完了したタイミングでイベントを発行できます。
        // 引数には、会員登録したユーザーの UserオブジェクトとString型のリクエストURLを渡します。
        signupEventPublisher.publishSignupEvent(createdUser, requestUrl);
        
        // 会員登録に成功した場合はログインページにリダイレクトさせますが、その際、以下のように「会員登録が完了しました。」というメッセージを表示させたい場合。
        // リダイレクト先にデータを渡したい場合はまず、メソッドにRedirectAttributes型の引数を設定します。
        // RedirectAttributesは、リダイレクト先にデータを渡すための機能を提供するインターフェースです。
        // （旧コード）redirectAttributes.addFlashAttribute("successMessage", "会員登録が完了しました。");
        redirectAttributes.addFlashAttribute("successMessage", "ご入力いただいたメールアドレスに認証メールを送信しました。メールに記載されているリンクをクリックし、会員登録を完了してください。");
        
        // ビューを呼び出すのではなくリダイレクトさせたい場合は、return "redirect:ルートパス"のように記述します。
        // 今回はログインページ（https://ドメイン名/login）にリダイレクトさせたいので、return "redirect:/login"と記述します。
        return "redirect:/login";
    }
    
    // メソッドの引数に@RequestParamアノテーションをつけることで、リクエストパラメータの値をその引数にバインドする（割り当てる）ことができます。
    // 以下の様に引数に@RequestParam(name = "token")を設定しておけば、「https://ドメイン名/signup/verify?token=トークン」の中の「トークン」の部分を取得できるということです。
    @GetMapping("/signup/verify")
    public String verify(@RequestParam(name = "token") String token, Model model) {
        VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);
        
        if (verificationToken != null) {
            User user = verificationToken.getUser();  
            userService.enableUser(user);
            String successMessage = "会員登録が完了しました。";
            model.addAttribute("successMessage", successMessage);            
        } else {
            String errorMessage = "トークンが無効です。";
            model.addAttribute("errorMessage", errorMessage);
        }
        
        return "auth/verify";         
    }
}