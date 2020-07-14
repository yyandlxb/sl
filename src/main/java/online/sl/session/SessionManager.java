package online.sl.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
//import org.springframework.session.FindByIndexNameSessionRepository;
//import org.springframework.session.Session;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

@Component
public class SessionManager extends HandlerInterceptorAdapter implements HandlerMethodArgumentResolver {


//    private FindByIndexNameSessionRepository<? extends Session> sessionRepository;
//
//    @Autowired
//    public void setSessionRepository(FindByIndexNameSessionRepository<? extends Session> sessionRepository) {
//        this.sessionRepository = sessionRepository;
//    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod method = (HandlerMethod) handler;
        boolean needAuthenticated = AnnotationUtils.findAnnotation(method.getBeanType(), Authenticated.class) != null;
        if (!needAuthenticated) {
            needAuthenticated = method.hasMethodAnnotation(Authenticated.class);

            if (!needAuthenticated) {
                needAuthenticated = Stream.of(method.getMethodParameters()).anyMatch(
                    p -> p.hasParameterAnnotation(Authenticated.class)
                );
            }
        }

        if (!needAuthenticated) {
            return true;
        }

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        Object value = session.getAttribute(Authenticated.class.getName());
        if (value == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        request.setAttribute(Authenticated.class.getName(), value);
        return true;

    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Authenticated.class) ;
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory
    ) {
        return webRequest.getAttribute(Authenticated.class.getName(), RequestAttributes.SCOPE_REQUEST);
    }

    public void bind(HttpSession session, Object user) {
        session.setAttribute(Authenticated.class.getName(), user);
//        session.setAttribute(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, getIndexValue(user));
    }

    private String getIndexValue(Object userId) {
        return (userId instanceof Indexed) ? ((Indexed) userId).getIndexValue() : String.valueOf(userId);
    }

    public interface Indexed {

        String getIndexValue();
    }

//    public void invalidate(String token) {
//        Map<String, ? extends Session> byIndexNameAndIndexValue =
//            sessionRepository.findByIndexNameAndIndexValue(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, token);
//        byIndexNameAndIndexValue.forEach((key, value) -> {
//            sessionRepository.deleteById(key);
//        });
//    }

}
