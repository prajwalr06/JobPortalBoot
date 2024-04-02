package com.s13sh.jobportal.service;

import java.time.LocalDate;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.s13sh.jobportal.dao.PortalUserDao;
import com.s13sh.jobportal.dto.PortalUser;
import com.s13sh.jobportal.helper.EmailSendingHelper;

@Service
public class PortalUserService {

	@Autowired
	PortalUserDao userDao;

	@Autowired
	EmailSendingHelper emailHelper;

	public String signup(PortalUser portalUser, BindingResult result, ModelMap map) {
		if(portalUser.getDob()==null) {
			result.rejectValue("dob", "error.dob", "* Select a Date");
			System.out.println("Error - Age is Not Selected");
		}else
		if (LocalDate.now().getYear() - portalUser.getDob().getYear() < 18) {
			result.rejectValue("dob", "error.dob", "* Age should be Greater Than 18");
			System.out.println("Error - Age is Not Greater Than 18");
		}
		if (!portalUser.getPassword().equals(portalUser.getConfirm_password())) {
			result.rejectValue("confirm_password", "error.confirm_password",
					"* Password and Confirm Password Should be Matching");
			System.out.println("Error - Password is Not Matching");
		}
		if (userDao.existsByEmail(portalUser.getEmail())) {
			result.rejectValue("email", "error.email", "* Email Should be unique");
			System.out.println("Error - Email is Repeated");
		}
		if (result.hasErrors()) {
			System.out.println("Error - There is Some Error");
			return "signup.html";
		}
		else {
			System.out.println("No Errors");
			int otp = new Random().nextInt(100000, 999999);
			System.out.println("Otp Generated - "+otp);
			portalUser.setOtp(otp);
			userDao.saveUser(portalUser);
			System.out.println("Data is Saved in db");
			emailHelper.sendOtp(portalUser);
			System.out.println("Otp is Sent to Email "+portalUser.getEmail());
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

}
