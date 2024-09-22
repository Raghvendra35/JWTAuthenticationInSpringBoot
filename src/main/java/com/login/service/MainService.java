package com.login.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.login.repository.MainRepository;

@Service
public class MainService {

	 @Autowired
     MainRepository mainrepo;	
	
	public String saveData(Map<String,Object> map)
	{
		mainrepo.saveData(map);
		return null;
	}
	
	
	public List<Map<String,Object>> findByUsername(String username)
	{
		System.out.println("Service !!!");
		return mainrepo.findByUsername(username);
	}
}

