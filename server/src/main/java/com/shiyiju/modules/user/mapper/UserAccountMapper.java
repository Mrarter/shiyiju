package com.shiyiju.modules.user.mapper;

import com.shiyiju.modules.user.entity.UserAccountEntity;
import org.apache.ibatis.annotations.Param;

public interface UserAccountMapper {

    UserAccountEntity findByOpenid(@Param("openid") String openid);

    UserAccountEntity findCurrentUser(@Param("userId") Long userId);

    Long findArtistIdByUserId(@Param("userId") Long userId);

    Long findDistributorIdByUserId(@Param("userId") Long userId);

    int insertUserAccount(UserAccountEntity entity);

    int insertWechatAuth(@Param("userId") Long userId,
                         @Param("openid") String openid,
                         @Param("unionid") String unionid,
                         @Param("sessionKey") String sessionKey,
                         @Param("appId") String appId);

    int insertUserRole(@Param("userId") Long userId, @Param("roleCode") String roleCode);

    int updateLoginProfile(@Param("userId") Long userId,
                           @Param("nickname") String nickname,
                           @Param("avatarUrl") String avatarUrl,
                           @Param("gender") Integer gender);

    int updateWechatAuth(@Param("userId") Long userId,
                         @Param("sessionKey") String sessionKey,
                         @Param("unionid") String unionid);

    int updateProfile(@Param("userId") Long userId,
                      @Param("nickname") String nickname,
                      @Param("avatarUrl") String avatarUrl,
                      @Param("mobile") String mobile,
                      @Param("gender") Integer gender);
}
