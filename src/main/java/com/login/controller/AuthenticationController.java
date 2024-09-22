package com.login.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.login.config.CustomUserDetailsService;
import com.login.config.JwtUtil;
import com.login.entities.AuthenticationRequest;
import com.login.entities.AuthenticationResponse;
import com.login.service.MainService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.http.HttpServletRequest;


@RestController
public class AuthenticationController 
{

	@Autowired
	MainService mainService;
	
	@Autowired
	BCryptPasswordEncoder encoder;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private CustomUserDetailsService userDetailsService;	
	
	@Autowired
	JwtUtil jwtUtil;
	
	
	
	@PostMapping("/save/data")
	public String saveData(@RequestBody Map<String,Object> map)
	{
		mainService.saveData(map);
		
		return null;
	}
	
	@PostMapping("/authentication")
	public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest,HttpServletRequest req) throws BadRequestException
	{
		System.out.println("Controller is calling !!!");
		List<Map<String, Object>> username = new ArrayList<>();
           username=mainService.findByUsername(authenticationRequest.getUsername());
           System.out.println("Data ==>"+ username);
           if(username.isEmpty())
           {
   			throw new BadRequestException("Wrong Credentials \r\n Invalid email or password");

           }
           
   		boolean res = encoder.matches(authenticationRequest.getPassword(), username.get(0).get("password").toString());
		System.out.println(res);
   		if (!res)
   		{
   			throw new BadRequestException("Wrong Credentials \r\n Invalid email or password");
   		}
         System.out.println("Password match "); 
         Authentication authentication = authenticationManager
                 .authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),authenticationRequest.getPassword()));
         

         
         System.out.println(authenticationManager);
         AuthenticationResponse response = null;
         if (authentication.isAuthenticated()){
 
        		UserDetails userdetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        		String token = jwtUtil.generateToken(userdetails);
        		
        		Map<String, Object> expectedMap = new HashMap<String, Object>();
        		expectedMap.put("role", "REFRESH");
        		String refreshtoken=jwtUtil.doGenerateRefreshToken(expectedMap, authenticationRequest.getUsername().toString());
        		
                System.out.println("token ==>"+ token);
                response=AuthenticationResponse.builder()
            		   .token(token)
            		   .refreshToken(refreshtoken).
            		   build();
                      
        	 
         }else {
           System.out.println("Else...");
         }
           
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/refresh/token", method = RequestMethod.GET)
	public ResponseEntity<?> refreshtoken(HttpServletRequest request) throws BadRequestException, Exception {

	    // Extract the refresh token from the header
	    String refreshtoken = request.getHeader("isRefreshToken");
	    
	    if (refreshtoken == null || refreshtoken.isEmpty()) {
	        return ResponseEntity.badRequest().body("Refresh token is missing");
	    }

	    // Extract claims from request attributes
	    DefaultClaims claims = (DefaultClaims) request.getAttribute("claims");
	    
	    if (claims == null) {
	        return ResponseEntity.badRequest().body("Claims are missing");
	    }
	    
	    Map<String, Object> expectedMap = getMapFromIoJsonwebtokenClaims(claims);
	    
	    // Ensure that the 'sub' key is present in the claims
	    if (!expectedMap.containsKey("sub")) {
	        return ResponseEntity.badRequest().body("Subject is missing in claims");
	    }

	    // Generate new refresh token
	    String token = jwtUtil.doGenerateRefreshToken(expectedMap, expectedMap.get("sub").toString());

	    // Return response with new token and old refresh token
	    return ResponseEntity.ok(new AuthenticationResponse(token, refreshtoken));
	    
	    //OR
/*		String refreshtoken = request.getHeader("isRefreshToken");
		DefaultClaims claims = (io.jsonwebtoken.impl.DefaultClaims) request.getAttribute("claims");
		Map<String, Object> expectedMap = getMapFromIoJsonwebtokenClaims(claims);
		String token = jwtUtil.doGenerateRefreshToken(expectedMap, expectedMap.get("sub").toString());
		return ResponseEntity.ok(new AuthenticationResponse(token, refreshtoken));
	*/
	}


	
	
	
	private Map<String, Object> getMapFromIoJsonwebtokenClaims(Claims claims) {
	
		 System.out.println("Calling ===========");
		Map<String, Object> expectedMap = new HashMap<String, Object>();
		for (Entry<String, Object> entry : claims.entrySet()) {
			expectedMap.put(entry.getKey(), entry.getValue());
		}
		System.out.println("expectedMap  ====>"+ expectedMap);
		
		return expectedMap;
	}

	
}
