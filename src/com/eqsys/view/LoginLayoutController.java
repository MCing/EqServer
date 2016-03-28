package com.eqsys.view;

import com.eqsys.application.EqServer;
import com.eqsys.security.Authenticator;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginLayoutController {

	private EqServer mainApp;
	// test 测试使用固定账号密码
	String tmpAccount = "administrator";
	String tmpPassword = "123";

	@FXML
	private TextField accountTf;
	@FXML
	private PasswordField passwordPf;
	@FXML
	private Button loginBtn;
	@FXML
	private Button resetBtn;
	@FXML
	private Label errorLabel;

	@FXML
	private void initialize() {
		accountTf.setText(tmpAccount);
		passwordPf.setText(tmpPassword);
	}

	/** 验证账号 */
	@FXML
	private void handleLogin() {
		String account = accountTf.getText();
		String password = passwordPf.getText();
		if (!"".equals(account) && !"".equals(password)) {
			validateLogin(account, password);
		} else {
			errorLabel.setText("请输入账号密码！");
		}
	}

	/** 验证登录 */
	private void validateLogin(String account, String password) {

		String encryptPassword = getEncryptPassword(account);
		if (encryptPassword == null) {
			errorLabel.setText("账号错误！");
			return;
		}
		if (!Authenticator.validMD5String(password, encryptPassword)) {
			errorLabel.setText("密码错误！");
			return;
		}
		mainApp.loadMainPage();

	}

	/**
	 * 通过账号获取加密过的密码 ,账号密码保存方式可用数据库、文本等 return 账号不存在则返回null,否则返回对应密码的md5值
	 * 
	 */
	private String getEncryptPassword(String account) {
		String ret = null;

		if (tmpAccount.equals(account)) {
			ret = Authenticator.getEncryptedString(tmpPassword);
		}
		return ret;
	}

	/** reset input */
	@FXML
	private void handleReset() {
		accountTf.setText("");
		passwordPf.setText("");
		errorLabel.setText("");
	}

	public void setMainApp(EqServer main) {
		this.mainApp = main;
	}

}
