package com.s13sh.jobportal.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.s13sh.jobportal.dao.PortalUserDao;
import com.s13sh.jobportal.dto.ApplicantDetails;
import com.s13sh.jobportal.dto.PortalUser;

import jakarta.servlet.http.HttpSession;

@Service
public class ApplicantService {

	@Autowired
	PortalUserDao userDao;

	public String completeProfile(ApplicantDetails details, File resume, HttpSession session, ModelMap map) {
		PortalUser portalUser = (PortalUser) session.getAttribute("portalUser");
		if (portalUser == null) {
			map.put("msg", "Invalid Session");
			return "home.html";
		} else {
			String resumePath = "cloudPath";// Get from cloudinary
			details.setResumePath(resumePath);
			portalUser.setApplicantDetails(details);
			portalUser.setProfileComplete(true);
			userDao.saveUser(portalUser);
			map.put("msg", "Profile is Completed");
			return "applicant-home.html";
		}
	}

}
