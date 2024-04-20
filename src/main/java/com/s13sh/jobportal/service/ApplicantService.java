package com.s13sh.jobportal.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.s13sh.jobportal.dao.PortalUserDao;
import com.s13sh.jobportal.dto.ApplicantDetails;
import com.s13sh.jobportal.dto.PortalUser;

import jakarta.servlet.http.HttpSession;

@Service
public class ApplicantService {

	@Autowired
	PortalUserDao userDao;

	public String completeProfile(ApplicantDetails details, MultipartFile resume, HttpSession session, ModelMap map) {
		PortalUser portalUser = (PortalUser) session.getAttribute("portalUser");
		if (portalUser == null) {
			session.setAttribute("failure", "Invalid Session");
			return "home.html";
		} else {
			String resumePath = uploadToCloudinary(resume);
			details.setResumePath(resumePath);
			portalUser.setApplicantDetails(details);
			portalUser.setProfileComplete(true);
			userDao.saveUser(portalUser);
			session.setAttribute("success", "Account Verified Success");
			return "redirect:/";
		}
	}

	public String uploadToCloudinary(MultipartFile file) {
		Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap("cloud_name", "djkyoabl5", "api_key",
				"297695696273364", "api_secret", "4bQWA8ZVWVftu83HUe57moGk5Q4"));

		Map resume = null;
		try {
			Map<String, Object> uploadOptions = new HashMap<String, Object>();
			uploadOptions.put("folder", "Resumes");
			resume = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (String) resume.get("url");
	}

}
