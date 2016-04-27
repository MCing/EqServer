package com.eqsys.view;

import com.eqsys.util.JDBCHelper;
import com.eqsys.util.SysConfig;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DatabaseSettingController extends FXMLController {

	private Stage stage;
	
	@FXML
	private TextField serverName;
	@FXML
	private TextField dbName;
	@FXML
	private TextField port;
	@FXML
	private TextField user;
	@FXML
	private PasswordField password;
	
	@FXML
	protected void initialize(){
		serverName.setText(SysConfig.getJdbcServerName());
		dbName.setText(SysConfig.getJdbcDb());
		port.setText(String.valueOf(SysConfig.getJdbcPort()));
		user.setText(SysConfig.getJdbcUser());
		password.setText(SysConfig.getJdbcPasswd());
	}
	
	public void initController(Stage stage){
		this.stage = stage;
	}
	
	@FXML
	private void handleOk(){
		String jdbcUser = user.getText();
		String jdbcPasswd = password.getText();
		String jdbcDb = dbName.getText();
		String jdbcPort = port.getText();
		String jdbcServerName = serverName.getText();
		SysConfig.saveDbConfig(jdbcServerName, jdbcPort, jdbcUser, jdbcPasswd, jdbcDb);
		stage.close();
	}
	
	@FXML 
	private void handleCancel(){
		stage.close();
	}
}
