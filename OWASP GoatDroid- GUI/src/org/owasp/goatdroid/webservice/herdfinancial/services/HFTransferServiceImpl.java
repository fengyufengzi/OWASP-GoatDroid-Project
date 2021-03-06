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

import java.sql.Date;
import java.util.ArrayList;
import javax.annotation.Resource;
import org.owasp.goatdroid.webservice.herdfinancial.Constants;
import org.owasp.goatdroid.webservice.herdfinancial.Validators;
import org.owasp.goatdroid.webservice.herdfinancial.dao.HFTransferDaoImpl;
import org.owasp.goatdroid.webservice.herdfinancial.model.BaseModel;
import org.springframework.stereotype.Service;

@Service
public class HFTransferServiceImpl implements TransferService {

	@Resource
	HFTransferDaoImpl dao;

	/*
	 * Impossible to break! ;-)
	 */
	public BaseModel transferFunds(String authToken, String from, String to,
			double amount) {

		BaseModel transfer = new BaseModel();
		ArrayList<String> errors = new ArrayList<String>();
		if (from.equals(to))
			errors.add(Constants.LULZ);
		else if (!Validators.validateAmountFormat(amount))
			errors.add(Constants.INVALID_CURRENCY_FORMAT);
		else if (!Validators.validateAccountNumber(to)
				&& Validators.validateAccountNumber(from))
			errors.add(Constants.INVALID_ACCOUNT_NUMBER);

		try {
			if (errors.size() == 0) {
				/*
				 * Check to ensure current funds are sufficient
				 */
				if (dao.doesAccountExist(to)) {
					if (dao.getBalance(from) >= amount) {
						java.util.Date today = new java.util.Date();
						Date date = new Date(today.getTime());
						// this is for the from account
						double fromBalance = dao.getBalance(from);
						String fromName = dao.getName(from);
						dao.insertTransaction(from, date, -amount, fromName,
								fromBalance);
						dao.updateAccountBalance(from, -amount, fromBalance);
						// this is for the to account
						double toBalance = dao.getBalance(to);
						String toName = dao.getName(to);
						dao.insertTransaction(to, date, amount, toName,
								toBalance);
						dao.updateAccountBalance(to, amount, toBalance);
						// now we set the success, close connection,
						// and
						// return
						transfer.setSuccess(true);
					} else {
						errors.add(Constants.INSUFFICIENT_FUNDS);
					}
				} else
					errors.add(Constants.INVALID_RECIPIENT);
			}
		} catch (Exception e) {
			errors.add(Constants.UNEXPECTED_ERROR);
		} finally {
			transfer.setErrors(errors);
		}
		return transfer;
	}
}
