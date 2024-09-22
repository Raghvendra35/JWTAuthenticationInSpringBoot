package com.login.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class MainRepository {

	
	@Autowired
	JdbcTemplate writeJdbc;
	
	@Autowired
	private PasswordEncoder bcryptEncoder;
	
	public String saveData(Map<String,Object> map)
	{
		String query="Insert into login (username,password,mobileno) values(?,?,?)";
		Object ar[]= {map.get("username").toString(),bcryptEncoder.encode(map.get("password").toString()),map.get("mobileno")};
		
		writeJdbc.update(query,ar);
		return null;
	}
	
	public List<Map<String,Object>> findByUsername(String username)
	{
		List<Map<String,Object>> list=new ArrayList<>();
		
		String q="select * from login where username=?";
		
		list=writeJdbc.queryForList(q,username);
		return list;
	}
}
