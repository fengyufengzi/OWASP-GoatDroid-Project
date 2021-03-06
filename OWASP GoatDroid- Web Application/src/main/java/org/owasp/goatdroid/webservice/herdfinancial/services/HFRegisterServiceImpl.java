/**
 * OWASP GoatDroid Project
 * 
 * This file is part of the Open Web Application Security Project (OWASP)
 * GoatDroid project. For details, please see
 * https://www.owasp.org/index.php/Projects/OWASP_GoatDroid_Project
 *
 * Copyright (c) 2012 - The OWASP Foundation
 * 
 * GoatDroid is published by OWASP under the GPLv3 license. You should read and accept the
 * LICENSE before you use, modify, and/or redistribute this software.
 * 
 * @author Jack Mannino (Jack.Mannino@owasp.org https://www.owasp.org/index.php/User:Jack_Mannino)
 * @created 2012
 */
package org.owasp.goatdroid.webservice.herdfinancial.services;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.owasp.goatdroid.webservice.herdfinancial.Constants;
import org.owasp.goatdroid.webservice.herdfinancial.Validators;
import org.owasp.goatdroid.webservice.herdfinancial.dao.HFRegisterDaoImpl;
import org.owasp.goatdroid.webservice.herdfinancial.model.Register;
import org.springframework.stereotype.Service;

@Service
public class HFRegisterServiceImpl implements RegisterService {

	@Resource
	HFRegisterDaoImpl dao;

	public Register registerUser(String accountNumber, String firstName,
			String lastName, String userName, String password) {

		Register register = new Register();
		ArrayList<String> errors = Validators.validateRegistrationFields(
				accountNumber, firstName, lastName, userName, password);

		try {
			if (errors.size() == 0) {
				if (!dao.doesUserNameExist(userName)) {
					if (!dao.doesAccountNumberExist(accountNumber)) {
						dao.registerUser(accountNumber, firstName, lastName,
								userName, password);
						register.setSuccess(true);
					} else
						errors.add(Constants.ACCOUNT_NUMBER_ALREADY_REGISTERED);
				} else
					errors.add(Constants.USERNAME_ALREADY_REGISTERED);
			}
		} catch (Exception e) {
			errors.add(Constants.UNEXPECTED_ERROR);
		} finally {
			register.setErrors(errors);
		}
		return register;
	}
}
