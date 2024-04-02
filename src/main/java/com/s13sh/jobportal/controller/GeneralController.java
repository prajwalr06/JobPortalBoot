package com.s13sh.jobportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.s13sh.jobportal.dto.PortalUser;
import com.s13sh.jobportal.service.PortalUserService;

import jakarta.validation.Valid;

@Controller
public class GeneralController {

	@Autowired
	PortalUser portalUser;

	@Autowired
	PortalUserService userService;

	@GetMapping("/")
	public String loadHome() {
		System.out.println("Control- / , Homepage is displayer");
		return "home.html";
	}

	@GetMapping("/login")
	public String loadLogin() {
		System.out.println("Control- /login , Login Page is displayer");
		return "login.html";
	}

	@GetMapping("/signup")
	public String loadSignup(ModelMap map) {
		System.out.println("Control- /signup - Get , Empty Object is Sent to Signup Page");
		map.put("portalUser", portalUser);
		return "signup.html";
	}

	@PostMapping("/signup")
	public String signup(@Valid PortalUser portalUser, BindingResult result, ModelMap map) {
		System.out.println("Control- /signup - Post , Recieved Post Request");
		return userService.signup(portalUser, result, map);
	}

	@PostMapping("/submit-otp")
	public String submitOtp(@RequestParam int otp, @RequestParam int id, ModelMap map) {
		System.out.println("Control - /submit-otp Get , Recieved otp");
		return userService.submitOtp(otp, id, map);
	}

}
