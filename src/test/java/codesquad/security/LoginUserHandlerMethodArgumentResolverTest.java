package codesquad.security;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;

import codesquad.UnAuthorizedException;
import codesquad.domain.Member;

@RunWith(MockitoJUnitRunner.class)
public class LoginUserHandlerMethodArgumentResolverTest {
    @Mock
    private MethodParameter parameter;

    @Mock
    private NativeWebRequest request;

    @Mock
    private LoginUser annotedLoginUser;

    private LoginUserHandlerMethodArgumentResolver loginUserHandlerMethodArgumentResolver;

    @Before
    public void setup() {
        loginUserHandlerMethodArgumentResolver = new LoginUserHandlerMethodArgumentResolver();
    }

    @Test
    public void loginUser_normal() throws Exception {
        Member sessionUser = new Member("password", "name", "hue@korea.kr");
        when(request.getAttribute(HttpSessionUtils.USER_SESSION_KEY, WebRequest.SCOPE_SESSION)).thenReturn(sessionUser);

        Member loginUser = (Member) loginUserHandlerMethodArgumentResolver.resolveArgument(parameter, null, request, null);

        assertThat(loginUser, is(sessionUser));
    }

    @Test(expected = UnAuthorizedException.class)
    public void loginUser_required_guest() throws Exception {
        when(annotedLoginUser.required()).thenReturn(true);
        when(parameter.getParameterAnnotation(LoginUser.class)).thenReturn(annotedLoginUser);
        when(request.getAttribute(HttpSessionUtils.USER_SESSION_KEY, WebRequest.SCOPE_SESSION))
                .thenReturn(Member.GUEST_MEMBER);

        loginUserHandlerMethodArgumentResolver.resolveArgument(parameter, null, request, null);
    }

    @Test
    public void loginUser_not_required_guest() throws Exception {
        when(annotedLoginUser.required()).thenReturn(false);
        when(parameter.getParameterAnnotation(LoginUser.class)).thenReturn(annotedLoginUser);
        when(request.getAttribute(HttpSessionUtils.USER_SESSION_KEY, WebRequest.SCOPE_SESSION))
                .thenReturn(Member.GUEST_MEMBER);

        Member guestUser = (Member) loginUserHandlerMethodArgumentResolver.resolveArgument(parameter, null, request, null);
        assertThat(guestUser, is(Member.GUEST_MEMBER));
    }

    @Test
    public void supportsParameter_false() {
        when(parameter.hasParameterAnnotation(LoginUser.class)).thenReturn(false);

        assertThat(loginUserHandlerMethodArgumentResolver.supportsParameter(parameter), is(false));
    }

    @Test
    public void supportsParameter_true() {
        when(parameter.hasParameterAnnotation(LoginUser.class)).thenReturn(true);

        assertThat(loginUserHandlerMethodArgumentResolver.supportsParameter(parameter), is(true));
    }
}
