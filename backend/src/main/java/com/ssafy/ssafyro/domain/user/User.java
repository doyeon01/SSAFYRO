package com.ssafy.ssafyro.domain.user;

import com.ssafy.ssafyro.domain.BaseEntity;
import com.ssafy.ssafyro.domain.MajorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String providerId;

    private String providerName;

    private String nickname;

    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private MajorType majorType;

    @Builder
    private User(String providerId, String providerName, String nickname, String profileImageUrl, MajorType majorType) {
        this.providerId = providerId;
        this.providerName = providerName;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.majorType = majorType;
    }
}
