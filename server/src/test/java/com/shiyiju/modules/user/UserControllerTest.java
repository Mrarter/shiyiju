package com.shiyiju.modules.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shiyiju.config.GlobalExceptionHandler;
import com.shiyiju.modules.user.controller.UserController;
import com.shiyiju.modules.user.dto.UpdateCurrentUserDTO;
import com.shiyiju.modules.user.service.UserService;
import com.shiyiju.modules.user.vo.CurrentUserVO;
import com.shiyiju.modules.user.vo.UserAuthorizationVO;
import com.shiyiju.security.CurrentUserHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        CurrentUserHolder.set(1L);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @AfterEach
    void tearDown() {
        CurrentUserHolder.clear();
    }

    @Test
    void shouldGetAuthorization() throws Exception {
        UserAuthorizationVO authorization = new UserAuthorizationVO();
        authorization.setUserId(1L);
        authorization.setDefaultRole("ARTIST");
        authorization.setRoles(List.of("COLLECTOR", "ARTIST"));
        authorization.setPermissions(List.of("PROFILE_EDIT", "ARTIST_HOME_VIEW"));
        authorization.setArtistId(9L);
        when(userService.getAuthorization(1L)).thenReturn(authorization);

        mockMvc.perform(get("/api/v1/users/me/authorization"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.defaultRole").value("ARTIST"))
            .andExpect(jsonPath("$.data.artistId").value(9));
    }

    @Test
    void shouldUpdateCurrentUser() throws Exception {
        CurrentUserVO user = new CurrentUserVO();
        user.setUserId(1L);
        user.setNickname("新昵称");
        when(userService.updateCurrentUser(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any(UpdateCurrentUserDTO.class)))
            .thenReturn(user);

        UpdateCurrentUserDTO request = new UpdateCurrentUserDTO();
        request.setNickname("新昵称");

        mockMvc.perform(put("/api/v1/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.nickname").value("新昵称"));
    }
}
