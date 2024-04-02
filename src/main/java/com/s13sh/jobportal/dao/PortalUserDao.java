package com.s13sh.jobportal.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.s13sh.jobportal.dto.PortalUser;
import com.s13sh.jobportal.repository.PortalUserRepository;

@Repository
public class PortalUserDao {

	@Autowired
	PortalUserRepository userRepository;

	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	public void saveUser(PortalUser portalUser) {
		userRepository.save(portalUser);
	}

	public PortalUser findUserById(int id) {
		return userRepository.findById(id).orElse(null);
	}
}
