package org.project.simproject.domain;

import jakarta.persistence.*;
import lombok.*;
import org.project.simproject.dto.ModifyRequest;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
public class User /*implements UserDetails*/ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "email", unique = true)
    private String email;

    private String password;

    @Column(unique = true)
    private String nickname;

    private int articlesCount;

    /*@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }*/

    @Builder
    public User(String email, String password, String nickname){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.articlesCount = 0;
    }

    /*@Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }*/

    public void modify(ModifyRequest modifyRequest) {
        if(modifyRequest.getPassword().equals("")) {
            this.nickname = modifyRequest.getNickname();
        }
        else if(modifyRequest.getNickname().equals("")) {
            this.password = modifyRequest.getPassword();
        }
    }

    public void articleAdd(){
        this.articlesCount++;
    }

    public void articleDelete(){
        this.articlesCount--;
    }
}
