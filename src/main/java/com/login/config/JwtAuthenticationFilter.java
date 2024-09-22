package com.login.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter
{

	
	@Autowired
	JwtUtil jwtHelper;
	
	@Autowired
	CustomUserDetailsService userDetailsService;
	
	private String secret;

	
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//			throws ServletException, IOException 
//	{
//        String requestHeader=request.getHeader("Authorization");
//        System.out.println("header  ===>"+ requestHeader);
//		String username=null;
//		String token=null;
//		
//		if(requestHeader !=null && requestHeader.startsWith("Bearer"))
//		{
//			token=requestHeader.substring(7);
//			try
//			{
//				username=this.jwtHelper.getUsernameFromToken(token);
//				
//			}catch(IllegalArgumentException e)
//			{
//				logger.info("Illegal Argument while fetching the username !!");
//				e.printStackTrace();
//			}catch(ExpiredJwtException e)
//			{
//				logger.info("Given jwt token is expired !!");
//				//e.printStackTrace();
//				//Generate Refresh token
//				 String isRefreshToken = request.getHeader("isRefreshToken");
//			
//					
//					Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(isRefreshToken).getBody();
//
//					// allow for Refresh Token creation if following conditions are true.
//					String rol=claims.get("role").toString();
//				//	Integer exp=(Integer)claims.get("exp");
//
//					
//					if (isRefreshToken != null && rol.equals("REFRESH")) {
//						allowForRefreshToken(e, request);
//					} else
//						request.setAttribute("exception", e);
//				
//				
//			}catch(MalformedJwtException e)
//			{
//				logger.info("Some changed has done token !! Invalid Token ");
//			}
//		}else
//		{
//			logger.info("Invalid Header Value !!");
//		}
//		
//		if(username !=null && SecurityContextHolder.getContext().getAuthentication() == null)
//		{
//			//Fetch user detail from username
//			UserDetails userDetails=this.userDetailsService.loadUserByUsername(username);
//			Boolean validateToken=this.jwtHelper.validateToken(token, userDetails);
//			if(validateToken)
//			{
//				//Set the authentication
//				UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
//				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//				
//				SecurityContextHolder.getContext().setAuthentication(authentication);
//			}else
//			{
//				logger.info("Validation fails !!");
//			}
//				
//		}
//		filterChain.doFilter(request, response);
//	}

	
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	        throws ServletException, IOException {

	    String requestHeader = request.getHeader("isRefreshToken");
	    String username = null;
	    String token = null;

	    if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
	        token = requestHeader.substring(7);
	        try {
	            username = this.jwtHelper.getUsernameFromToken(token);
	        } catch (IllegalArgumentException e) {
	            logger.info("Illegal Argument while fetching the username !!");
	        } catch (ExpiredJwtException e) {
	            logger.info("Given JWT token is expired !!");
	            String isRefreshToken = request.getHeader("isRefreshToken");

	            if (isRefreshToken != null) {
	                try {
	                    Claims claims = Jwts.parser()
	                            .setSigningKey(secret)
	                            .parseClaimsJws(isRefreshToken)
	                            .getBody();
	                    logger.info(claims);
	                    if ("REFRESH".equals(claims.get("role"))) {
	                        allowForRefreshToken(e, request);
	                    } else {
	                        request.setAttribute("exception", e);
	                    }
	                } catch (Exception ex) {
	                    logger.error("Failed to parse refresh token", ex);
	                    request.setAttribute("exception", ex);
	                }
	            }
	        } catch (MalformedJwtException e) {
	            logger.info("Malformed JWT token");
	        }
	    } else {
	        logger.info("Invalid Header Value !!");
	    }

	    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
	        if (this.jwtHelper.validateToken(token, userDetails)) {
	            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
	                    userDetails, null, userDetails.getAuthorities());
	            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	            SecurityContextHolder.getContext().setAuthentication(authentication);
	        } else {
	            logger.info("Validation fails !!");
	        }
	    }

	    filterChain.doFilter(request, response);
	}


	private void allowForRefreshToken(ExpiredJwtException e, HttpServletRequest request) 
	{

		// create a UsernamePasswordAuthenticationToken with null values.
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
				null, null, null);
		// After setting the Authentication in the context, we specify
		// that the current user is authenticated. So it passes the
		// Spring Security Configurations successfully.
		SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
		// Set the claims so that in controller we will be using it to create
		// new JWT
		request.setAttribute("claims", e.getClaims());
	}

}
