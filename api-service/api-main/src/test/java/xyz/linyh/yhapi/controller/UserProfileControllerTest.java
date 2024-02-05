package xyz.linyh.yhapi.controller;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.model.user.entitys.User;
import xyz.linyh.model.user.vo.UserProfileVo;
import xyz.linyh.yhapi.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserProfileControllerTest {

    @InjectMocks private UserProfileController userProfileController;

    @Mock private UserService userService;

    private MockHttpServletRequest request;

    @BeforeEach
    public void setUp() {
        request = new MockHttpServletRequest();
        request.setMethod("GET");
    }

    @Test
    public void testGetUserProfile() {
        Mockito.when(userService.getLoginUser(request)).thenReturn(new User());
        Mockito.when(userService.getUserProfile(Mockito.any(User.class), Mockito.anyString()))
                .thenReturn(new UserProfileVo());

        BaseResponse response = userProfileController.getUserProfile("1", request);
        Assertions.assertEquals(200, response.getCode());
        Assertions.assertNotNull(response.getData());
        UserProfileVo userProfileVo =
                JSON.parseObject(JSON.toJSONString(response.getData()), UserProfileVo.class);
        Assertions.assertNotNull(userProfileVo);
    }

    @Test
    public void testGetUserProfileWithNullAccount() {
        BaseResponse response = userProfileController.getUserProfile(null, request);
        Assertions.assertEquals(500, response.getCode());
        Assertions.assertNull(response.getData());
    }
}
