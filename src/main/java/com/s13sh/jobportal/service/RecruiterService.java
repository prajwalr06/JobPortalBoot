package com.s13sh.jobportal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.s13sh.jobportal.dao.PortalUserDao;
import com.s13sh.jobportal.dto.PortalUser;
import com.s13sh.jobportal.dto.RecruiterDetails;

import jakarta.servlet.http.HttpSession;

@Service
public class RecruiterService {

	@Autowired
	PortalUserDao userDao;

	public String saveRecruiterDetails(RecruiterDetails details, HttpSession session, ModelMap map) {
		PortalUser portalUser = (PortalUser) session.getAttribute("portalUser");
		if (portalUser == null) {
			session.setAttribute("failure", "Invalid Session");
			return "redirect:/";
		} else {
			portalUser.setRecruiterDetails(details);
			userDao.saveUser(portalUser);
			session.setAttribute("success", "Profile Updated Success");
			return "redirect:/";
		}
	}

	public String checkProfile(ModelMap map, HttpSession session) {
		PortalUser portalUser = (PortalUser) session.getAttribute("portalUser");
		if (portalUser == null) {
			session.setAttribute("failure", "Invalid Session");
			return "redirect:/";
		} else {
			if (portalUser.getRecruiterDetails() == null) {
				return "recruiter-profile.html";
			} else {
				session.setAttribute("failure", "Wait for Admins Approval");
				return "redirect:/";
			}
		}
	}

	public String postJob(HttpSession session) {
		PortalUser portalUser = (PortalUser) session.getAttribute("portalUser");
		if (portalUser == null) {
			session.setAttribute("failure", "Invalid Session");
			return "redirect:/";
		} else {
			if (!portalUser.isProfileComplete()) {
				session.setAttribute("failure", "First Complete Your Profile");
				return "redirect:/";
			} else {
				return "post-job.html";
			}
		}
	}

}
