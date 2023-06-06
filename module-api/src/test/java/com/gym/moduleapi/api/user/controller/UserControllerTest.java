package com.gym.moduleapi.api.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.moduleapi.api.user.request.UserJoinRequest;
import com.gym.moduleapi.api.user.request.UserLoginRequest;
import com.gym.moduleapi.security.annotation.WithMockCustomUser;
import com.gym.modulecore.core.user.model.dto.TokenInfo;
import com.gym.modulecore.core.user.model.dto.User;
import com.gym.modulecore.core.user.service.UserService;
import com.gym.modulecore.exception.CommunityException;
import com.gym.modulecore.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("local")
@AutoConfigureMockMvc
@SpringBootTest(properties = {"jasypt.encryptor.password=communitySystem"}) // VM 옵션값
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void 회원가입() throws Exception {
        String userName = "userName";
        String password = "password";

        // mocking
        when(userService.join(userName, password)).thenReturn(mock(User.class));

        mockMvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName, password)))
                )
                .andDo(print())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS")) // response body 요소의 기대값 확인
                .andExpect(status().isOk());
    }

    @Test
    void 회원가입시_이미_회원가입된_userName으로_회원가입을_하는경우_에러반환() throws Exception {
        String userName = "userName";
        String password = "password";

        // mocking
        when(userService.join(userName, password)).thenThrow(new CommunityException(ErrorCode.DUPLICATED_USER_NAME));

        mockMvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName, password)))
                ).andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void 로그인() throws Exception {
        String userName = "userName";
        String password = "password";

        // mocking
        when(userService.login(userName, password)).thenReturn(mock(TokenInfo.class));

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void 로그인시_회원가입이_안된_userName을_입력할경우_에러반환() throws Exception {
        String userName = "userName";
        String password = "password";

        // mocking
        when(userService.login(userName, password)).thenThrow(new CommunityException(ErrorCode.USER_NOT_FOUND));

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void 로그인시_틀린_password를_입력할경우_에러반환() throws Exception {
        String userName = "userName";
        String password = "password";

        // mocking
        when(userService.login(userName, password)).thenThrow(new CommunityException(ErrorCode.INVALID_PASSWORD));

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser
    void 알람기능() throws Exception {
        // mocking
        when(userService.alarmList(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/users/alarm")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void 알람리스트_요청시_로그인하지_않은경우() throws Exception {
        // mocking
        when(userService.alarmList(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/users/alarm")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}