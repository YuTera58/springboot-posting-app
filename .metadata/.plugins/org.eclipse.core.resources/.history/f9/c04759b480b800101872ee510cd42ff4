package com.example.postingapp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.postingapp.entity.Post;
import com.example.postingapp.entity.User;
import com.example.postingapp.form.PostEditForm;
import com.example.postingapp.form.PostRegisterForm;
import com.example.postingapp.security.UserDetailsImpl;
import com.example.postingapp.service.PostService;

/* クラスに@RequestMappingアノテーションをつけることで、ルートパスの基準値を設定できる。
　　例：@RequestMapping("/posts")と指定すれば、そのコントローラ内の各メソッドが担当するURLは、
　　「https://ドメイン名/posts/○○○」になる。
*/
@Controller
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }
    
	/* コントローラのメソッドでは、Spring Securityが提供する@AuthenticationPrincipalアノテーションを引数につけることで、
	   現在ログイン中のユーザー情報を取得できる。
	*/ 
    @GetMapping
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
        User user = userDetailsImpl.getUser();
        List<Post> posts = postService.findPostsByUserOrderedByCreatedAtDesc(user);

        model.addAttribute("posts", posts);

        return "posts/index";
    }
    
	/*
	■ポイントは以下の3つです。
	1.引数に@PathVariableアノテーションをつける
	　コントローラ内ではメソッドの引数に@PathVariableアノテーションをつけることで、
	　URLの一部をその引数にバインドする（割り当てる）ことができます。
	　これにより、URLの一部を変数のように扱って、コントローラ内でその値を利用することができます。
	　※例えば、以下のようなURLにアクセスしたとします。
	　--------------------------------
	　https://ドメイン名/posts/3
	　--------------------------------
	　コントローラのメソッドは以下のコードの様に定義されていた場合、
	　URLの{id}の部分にある値（3）がshow()メソッドの引数idにバインドされます。
	　これにより、show()メソッド内ではidの値を利用して処理を行うことができます。
　　　なお、@PathVariableアノテーションのname属性にはバインドさせたいURLの{}内の文字列（今回は/posts/{id}なので、"id"）を指定します。
	2.投稿が存在しない場合の処理を書く（*1）
	3.Optional<Post>型をPost型に変換する（*2）
	**/
    @GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
        Optional<Post> optionalPost  = postService.findPostById(id);

        if (optionalPost.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "投稿が存在しません。"); //*1

            return "redirect:/posts";
        }

        Post post = optionalPost.get(); //*2
        model.addAttribute("post", post);

        return "posts/show";
    }
    
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("postRegisterForm", new PostRegisterForm());

        return "posts/register";
    }
    
    @PostMapping("/create")
    public String create(@ModelAttribute @Validated PostRegisterForm postRegisterForm,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                         RedirectAttributes redirectAttributes,
                         Model model)
    {
    	//フォームの入力内容に対してバリデーションを行い、エラーが存在すれば投稿作成ページを再度表示します。
        if (bindingResult.hasErrors()) {
            model.addAttribute("postRegisterForm", postRegisterForm);

            return "posts/register";
        }

        //エラーが存在しなければ、サービスクラスに定義したcreatePost()メソッドを実行し、投稿をpostsテーブルに追加します。
        User user = userDetailsImpl.getUser();
        postService.createPost(postRegisterForm, user);
        
        //「投稿が完了しました。」というフラッシュメッセージとともに、投稿一覧ページにリダイレクトさせます。
        redirectAttributes.addFlashAttribute("successMessage", "投稿が完了しました。");

        return "redirect:/posts";
    }
    
    /*
    ■ポイントは以下の2つです。
    1.他人の投稿編集ページにアクセスできないようにする (*1)
    2.フォームの初期値をビューに渡す (*2)
    **/
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable(name = "id") Integer id,
                       @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                       RedirectAttributes redirectAttributes,
                       Model model)
    {
        Optional<Post> optionalPost = postService.findPostById(id);
        User user = userDetailsImpl.getUser();

        //*1
        //条件式の先頭に論理否定演算子（!）をつけて反転させているため、
        //投稿を作成したユーザーがログイン中のユーザーと等しくない場合にtrueを返します。
        if (optionalPost.isEmpty() || !optionalPost.get().getUser().equals(user)) {
            redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");

            return "redirect:/posts";
        }

        //*2
        //更新前の投稿の各フィールドの値を使ってフォームクラスをインスタンス化し、ビューに渡します。
        Post post = optionalPost.get();
        model.addAttribute("post", post);
        model.addAttribute("postEditForm", new PostEditForm(post.getTitle(), post.getContent()));

        return "posts/edit";
    }
    
    //URLのidに一致する投稿が存在しない場合だけでなく、その投稿を作成したユーザーが、
    //ログイン中のユーザーと一致しない場合にも投稿一覧ページにリダイレクトさせています。（他人の投稿を更新できないようにするため）
    @PostMapping("/{id}/update")
    public String update(@ModelAttribute @Validated PostEditForm postEditForm,
                         BindingResult bindingResult,
                         @PathVariable(name = "id") Integer id,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                         RedirectAttributes redirectAttributes,
                         Model model)
    {
        Optional<Post> optionalPost = postService.findPostById(id);
        User user = userDetailsImpl.getUser();

        if (optionalPost.isEmpty() || !optionalPost.get().getUser().equals(user)) {
            redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");

            return "redirect:/posts";
        }

        Post post = optionalPost.get();

        if (bindingResult.hasErrors()) {
            model.addAttribute("post", post);
            model.addAttribute("postEditForm", postEditForm);

            return "posts/edit";
        }

        postService.updatePost(postEditForm, post);
        redirectAttributes.addFlashAttribute("successMessage", "投稿を編集しました。");

        return "redirect:/posts/" + id;
    }
    
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable(name = "id") Integer id,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                         RedirectAttributes redirectAttributes,
                         Model model)
    {
        Optional<Post> optionalPost = postService.findPostById(id);
        User user = userDetailsImpl.getUser();

        if (optionalPost.isEmpty() || !optionalPost.get().getUser().equals(user)) {
            redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");

            return "redirect:/posts";
        }

        Post post = optionalPost.get();
        postService.deletePost(post);
        redirectAttributes.addFlashAttribute("successMessage", "投稿を削除しました。");

        return "redirect:/posts";
    }
}