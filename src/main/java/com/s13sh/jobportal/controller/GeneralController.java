package com.s13sh.jobportal.controller;

import java.time.LocalDate;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.s13sh.jobportal.dao.PortalUserDao;
import com.s13sh.jobportal.dto.PortalUser;

import jakarta.validation.Valid;

@Controller
public class GeneralController {

	@Autowired
	PortalUser portalUser;

	@Autowired
	PortalUserDao userDao;

	@GetMapping("/")
	public String loadHome() {
		return "home.html";
	}

	@GetMapping("/login")
	public String loadLogin() {
		return "login.html";
	}

	@GetMapping("/signup")
	public String loadSignup(ModelMap map) {
		map.put("portalUser", portalUser);
		return "signup.html";
	}

	@PostMapping("/signup")
	public String signup(@Valid PortalUser portalUser, BindingResult result, ModelMap map) {
		if (LocalDate.now().getYear() - portalUser.getDob().getYear() < 18)
			result.rejectValue("dob", "error.dob", "* Age should be Greater Than 18");
		if (!portalUser.getPassword().equals(portalUser.getConfirm_password()))
			result.rejectValue("confirm_password", "error.confirm_password",
					"* Password and Confirm Password Should be Matching");
		if (userDao.existsByEmail(portalUser.getEmail()))
			result.rejectValue("email", "error.email", "* Email Should be unique");

		if (result.hasErrors())
			return "signup.html";
		else {
			int otp = new Random().nextInt(100000, 999999);
			portalUser.setOtp(otp);
			userDao.saveUser(portalUser);
			// Send Mail to EMail
			map.put("msg", "Otp Sent Success");
			map.put("id", portalUser.getId());
			return "enter-otp.html";
		}
	}

	@PostMapping("/submit-otp")
	public String submitOtp(@RequestParam int otp, @RequestParam int id, ModelMap map) {
		PortalUser portalUser = userDao.findUserById(id);
		if (otp == portalUser.getOtp()) {
			portalUser.setVerified(true);
			userDao.saveUser(portalUser);
			map.put("msg", "Account Created Success");
			return "login.html";
		} else {
			map.put("msg", "Incorrect Otp! Try Again");
			map.put("id", portalUser.getId());
			return "enter-otp.html";
		}
	}

}
