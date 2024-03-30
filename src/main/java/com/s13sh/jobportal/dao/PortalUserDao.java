package com.s13sh.jobportal.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.s13sh.jobportal.repository.PortalUserRepository;

@Component
public class PortalUserDao {

	@Autowired
	PortalUserRepository userRepository;

	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}
}
