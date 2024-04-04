package com.s13sh.jobportal.service;

import java.time.LocalDate;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.s13sh.jobportal.dao.PortalUserDao;
import com.s13sh.jobportal.dto.PortalUser;
import com.s13sh.jobportal.helper.AES;
import com.s13sh.jobportal.helper.EmailSendingHelper;

import jakarta.servlet.http.HttpSession;

@Service
public class PortalUserService {

	@Autowired
	PortalUserDao userDao;

	@Autowired
	EmailSendingHelper emailHelper;

	public String signup(PortalUser portalUser, BindingResult result, ModelMap map) {
		if (portalUser.getDob() == null) {
			result.rejectValue("dob", "error.dob", "* Select a Date");
			System.out.println("Error - Age is Not Selected");
		} else if (LocalDate.now().getYear() - portalUser.getDob().getYear() < 18) {
			result.rejectValue("dob", "error.dob", "* Age should be Greater Than 18");
			System.out.println("Error - Age is Not Greater Than 18");
		}
		if (!portalUser.getPassword().equals(portalUser.getConfirm_password())) {
			result.rejectValue("confirm_password", "error.confirm_password",
					"* Password and Confirm Password Should be Matching");
			System.out.println("Error - Password is Not Matching");
		}
		if (userDao.existsByEmail(portalUser.getEmail())) {
			result.rejectValue("email", "error.email", "* Account Already Exists");
			System.out.println("Error - Email is Repeated");
		}
		if (result.hasErrors()) {
			System.out.println("Error - There is Some Error");
			return "signup.html";
		} else {
			System.out.println("No Errors");
			int otp = new Random().nextInt(100000, 999999);
			System.out.println("Otp Generated - " + otp);
			portalUser.setOtp(otp);
			portalUser.setPassword(AES.encrypt(portalUser.getPassword(), "123"));
			portalUser.setConfirm_password(AES.encrypt(portalUser.getConfirm_password(), "123"));
			System.out.println("Encrypted Password - " + portalUser.getPassword());
			userDao.saveUser(portalUser);
			System.out.println("Data is Saved in db");
			emailHelper.sendOtp(portalUser);
			System.out.println("Otp is Sent to Email " + portalUser.getEmail());
			map.put("msg", "Otp Sent Success");
			map.put("id", portalUser.getId());
			System.out.println("Control- enter-otp.html");
			return "enter-otp.html";
		}
	}

	public String submitOtp(int otp, int id, ModelMap map) {
		PortalUser portalUser = userDao.findUserById(id);
		if (otp == portalUser.getOtp()) {
			System.out.println("Success- OTP Matched");
			portalUser.setVerified(true);
			userDao.saveUser(portalUser);
			map.put("msg", "Account Created Success");
			return "login.html";
		} else {
			System.out.println("Failure- OTP MissMatch");
			map.put("msg", "Incorrect Otp! Try Again");
			map.put("id", portalUser.getId());
			return "enter-otp.html";
		}
	}

	public String resendOtp(int id, ModelMap map) {
		PortalUser portalUser = userDao.findUserById(id);
		int otp = new Random().nextInt(100000, 999999);
		System.out.println("Otp ReGenerated - " + otp);
		portalUser.setOtp(otp);
		userDao.saveUser(portalUser);
		System.out.println("Data is Updated in db");
		emailHelper.sendOtp(portalUser);
		System.out.println("Otp is Sent to Email " + portalUser.getEmail());
		map.put("msg", "Otp Sent Again, Check");
		map.put("id", portalUser.getId());
		System.out.println("Control- enter-otp.html");
		return "enter-otp.html";
	}

	public String login(String emph, String password, ModelMap map, HttpSession session) {
		PortalUser portalUser = null;
		try {
			long mobile = Long.parseLong(emph);
			portalUser = userDao.findUserByMobile(mobile);
		} catch (NumberFormatException e) {
			String email = emph;
			portalUser = userDao.findUserByEmail(email);
		}
		if (portalUser == null) {
			map.put("msg", "Invalid Email or Phone Number");
			return "login.html";
		} else {
			if (password.equals(AES.decrypt(portalUser.getPassword(), "123"))) {
				if (portalUser.isVerified()) {
					map.put("msg", "Login Success");
					session.setAttribute("portalUser", portalUser);
					if (portalUser.getRole().equals("applicant")) {
						return "applicant-home.html";
					} else {
						return "recruiter-home.html";
					}
				} else {
					map.put("msg", "First Verify Your Email");
					return "login.html";
				}
			} else {
				map.put("msg", "Invalid Password");
				return "login.html";
			}
		}

	}

}
