package com.s13sh.jobportal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.s13sh.jobportal.dao.PortalUserDao;
import com.s13sh.jobportal.dto.PortalUser;
import com.s13sh.jobportal.helper.AES;

import jakarta.servlet.http.HttpSession;

@Service
public class AdminService {
	
	@Autowired
	PortalUser portalUser;

	@Autowired
	PortalUserDao userDao;
	
	public String createAdmin(String email, String password, HttpSession session) {
		if (userDao.existsByEmail(email)) {
			session.setAttribute("failure","Account Already Exists");
			return "redirect:/";
		}
		else {
		portalUser.setEmail(email);
		portalUser.setPassword(encrypt(password));
		portalUser.setRole("admin");
		portalUser.setVerified(true);
		userDao.saveUser(portalUser);
		session.setAttribute("success", "Admin Account Created Success");
		return "redirect:/";
		}
	}
	
	public String encrypt(String password) {
		return AES.encrypt(password, "123");
	}

}
