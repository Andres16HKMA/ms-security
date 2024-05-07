package com.ucaldas.mssecurity.Interceptors;

import com.ucaldas.mssecurity.services.JwtService;
import com.ucaldas.mssecurity.services.ValidatorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class SecurityInterceptor implements HandlerInterceptor {
    @Autowired
    private ValidatorService validatorService;
    
    @Autowired
    private JwtService jwtService;

    private static final String Bearer_Prefix = "Bearer";
    
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler)
            throws Exception {
        boolean success=true;
                String authorizationHeader = request.getHeader("Authorization");
                if (authorizationHeader != null && authorizationHeader.startsWith(Bearer_Prefix)){
                    String token = authorizationHeader.substring(Bearer_Prefix.length());
                    System.out.println("Bearer Token" + token);
                    success = jwtService.validateToken(token);
                }else{
                    success=false;
                }
                return success;
            }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // Lógica a ejecutar después de que se haya manejado la solicitud por el controlador
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
        // Lógica a ejecutar después de completar la solicitud, incluso después de la renderización de la vista
    }
}