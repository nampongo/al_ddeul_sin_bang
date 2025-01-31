package com.timcook.capstone.common.auth.handler;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler{

	private static final String REDIRECT_URL = "http://localhost:8080/api/login/success";
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		HttpSession session = request.getSession();
		session.setMaxInactiveInterval(1800);
		addSameSiteCookieAttribute(response);
		response.sendRedirect(REDIRECT_URL);
	}

	private void addSameSiteCookieAttribute(HttpServletResponse response) {
		Collection<String> headers = response.getHeaders(HttpHeaders.SET_COOKIE);
		boolean firstHeader = true;
		
		for (String header : headers) {
	        if (firstHeader) {
	            response.setHeader(HttpHeaders.SET_COOKIE,
	                    String.format("%s; %s", header, "SameSite=Strict"));
	            firstHeader = false;
	            continue;
	        }
	        response.addHeader(HttpHeaders.SET_COOKIE,
	                String.format("%s; %s", header, "SameSite=Strict"));
	    }
	}
	
}
