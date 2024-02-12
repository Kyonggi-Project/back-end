package org.project.simproject.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.project.simproject.dto.ModifyRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "email", unique = true)
    private String email;

    private String password;

    @Column(unique = true)
    private String nickname;

    private int articlesCount;

    @JsonIgnore
    @OneToMany(mappedBy = "followee")
    private List<Follow> followers = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "follower")
    private List<Follow> following = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Bookmark> bookmarks = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<ArticleLike> articleLikes = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Builder
    public User(String email, String password, String nickname){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.articlesCount = 0;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void modify(ModifyRequest modifyRequest) {
        if(modifyRequest.getPassword().equals("")) {
            this.nickname = modifyRequest.getNickname();
        }
        else if(modifyRequest.getNickname().equals("")) {
            this.password = modifyRequest.getPassword();
        }
    }

    public User update(String nickname){
        this.nickname = nickname;
        return this;
    }

    public void articleAdd(){
        this.articlesCount++;
    }

    public void articleDelete(){
        this.articlesCount--;
    }
}
