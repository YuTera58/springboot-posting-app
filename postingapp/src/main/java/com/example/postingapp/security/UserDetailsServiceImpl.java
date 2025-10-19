package com.example.postingapp.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.postingapp.entity.User;
import com.example.postingapp.repository.UserRepository;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    // コンストラクタインジェクションを使う場合、通常は@Autowiredアノテーションをつけます。
    // ただし、コンストラクタが1つしかない場合は以下のように省略が可能です。
    // @Autowired　※省略可能
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // loadUserByUsername()メソッド内では以下の処理をおこなっています。
    // ・フォームから送信されたメールアドレスに一致するユーザーを取得する
    // ・そのユーザーのロールを取得する
    // ・上記2つの情報をUserDetailsImplクラスのコンストラクタに渡し、インスタンスを生成する
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
        	// UserRepositoryインターフェースのfindByEmail()メソッドを使い、指定したメールアドレスに一致するユーザーを取得
            User user = userRepository.findByEmail(email);
            String userRoleName = user.getRole().getName();
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(userRoleName));
            return new UserDetailsImpl(user, authorities);
        } catch (Exception e) {
            throw new UsernameNotFoundException("ユーザーが見つかりませんでした。");
        }
    }
}