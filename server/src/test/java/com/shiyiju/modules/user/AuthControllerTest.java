package com.shiyiju.modules.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shiyiju.config.GlobalExceptionHandler;
import com.shiyiju.modules.user.controller.AuthController;
import com.shiyiju.modules.user.dto.WxLoginRequestDTO;
import com.shiyiju.modules.user.service.AuthService;
import com.shiyiju.modules.user.vo.CurrentUserVO;
import com.shiyiju.modules.user.vo.LoginVO;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        CurrentUserVO user = new CurrentUserVO();
        user.setUserId(1L);
        user.setUserNo("U001");
        user.setNickname("测试用户");
        user.setStatus("ENABLED");
        user.setRoles(List.of("COLLECTOR"));

        LoginVO loginVO = new LoginVO();
        loginVO.setToken("token-001");
        loginVO.setUser(user);
        when(authService.wxLogin(any(WxLoginRequestDTO.class))).thenReturn(loginVO);

        WxLoginRequestDTO request = new WxLoginRequestDTO();
        request.setCode("demo-code");

        mockMvc.perform(post("/api/v1/auth/wx/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.token").value("token-001"))
            .andExpect(jsonPath("$.data.user.userId").value(1));
    }
}
