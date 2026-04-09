package com.shiyiju.modules.admin.mapper;

import com.shiyiju.modules.admin.entity.AdminArtistEntity;
import com.shiyiju.modules.admin.entity.AdminArtworkEntity;
import com.shiyiju.modules.admin.entity.AdminOperationEntity;
import com.shiyiju.modules.admin.entity.AdminOrderEntity;
import com.shiyiju.modules.admin.entity.AdminUserEntity;
import com.shiyiju.modules.admin.entity.AdminDistributorEntity;
import com.shiyiju.modules.user.entity.UserAccountEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMapper {

    List<AdminOperationEntity> findOperations();

    int insertOperation(AdminOperationEntity entity);

    int updateOperation(AdminOperationEntity entity);

    int updateOperationStatus(@Param("id") Long id, @Param("status") String status);

    List<AdminArtistEntity> findArtists();

    int insertArtistUser(UserAccountEntity entity);

    int insertArtist(AdminArtistEntity entity);

    int updateArtist(AdminArtistEntity entity);

    int updateArtistUserAvatar(@Param("artistId") Long artistId,
                               @Param("nickname") String nickname,
                               @Param("avatarUrl") String avatarUrl);

    int updateArtistStatus(@Param("id") Long id, @Param("status") String status);

    List<AdminArtworkEntity> findArtworks();

    int insertArtwork(AdminArtworkEntity entity);

    int updateArtwork(AdminArtworkEntity entity);

    int updateArtworkCover(@Param("artworkId") Long artworkId, @Param("coverUrl") String coverUrl);

    int insertArtworkCover(@Param("artworkId") Long artworkId, @Param("coverUrl") String coverUrl);

    int deleteArtworkCover(@Param("artworkId") Long artworkId);

    int updateArtworkStatus(@Param("id") Long id, @Param("status") String status);

    List<AdminUserEntity> findUsers();

    int updateUserStatus(@Param("id") Long id, @Param("status") String status);

    String findConfigValue(@Param("configKey") String configKey);

    int upsertConfig(@Param("configKey") String configKey,
                     @Param("configValue") String configValue,
                     @Param("configGroup") String configGroup,
                     @Param("remark") String remark);

    List<AdminOrderEntity> findOrders();

    int updateOrderShipment(@Param("orderId") Long orderId,
                            @Param("company") String company,
                            @Param("trackingNo") String trackingNo);

    int insertOrderShipment(@Param("orderId") Long orderId,
                            @Param("company") String company,
                            @Param("trackingNo") String trackingNo);

    int updateOrderStatus(@Param("orderId") Long orderId, @Param("status") String status);

    int updateOrderRemark(@Param("orderId") Long orderId, @Param("remark") String remark);

    List<AdminDistributorEntity> findDistributors();

    int insertDistributor(AdminDistributorEntity entity);

    int insertUserRoleRelation(@Param("userId") Long userId, @Param("roleCode") String roleCode);

    int updateDistributor(AdminDistributorEntity entity);

    int updateDistributorStatus(@Param("id") Long id, @Param("status") String status);

    int insertInvitationCode(@Param("userId") Long userId, @Param("code") String code, @Param("codeType") String codeType);

    String findInvitationCodeByUserId(@Param("userId") Long userId);

    int updateInvitationCodeStatus(@Param("userId") Long userId, @Param("status") String status);

    List<AdminUserEntity> findUsersWithoutDistributor();
}
