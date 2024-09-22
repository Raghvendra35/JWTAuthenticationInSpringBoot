package com.login.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.login.repository.MainRepository;
import com.login.service.MainService;

@Service
public class CustomUserDetailsService implements UserDetailsService 
{

    @Autowired
    MainRepository mainRepository;


	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		List<Map<String,Object>> listuser=new ArrayList<>();
		List<SimpleGrantedAuthority> roles=null;
		
		
		try {
		    listuser=mainRepository.findByUsername(username);
		}catch (RuntimeException e) {
			e.printStackTrace();
		}
		
		Map<String,Object> user=listuser.get(0);
		System.out.println("user ==>"+ user);
		if(user !=null)
		{
			roles=Arrays.asList(new SimpleGrantedAuthority("Admin"));
			return new User(user.get("username").toString(),user.get("password").toString(),roles);
		}
		
		
		throw new UsernameNotFoundException("User not found with the name " + username);	

	}

}
