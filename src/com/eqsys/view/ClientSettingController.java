package com.eqsys.view;

import com.eqsys.model.ClientInfo;
import com.eqsys.msg.MsgConstant;
import com.eqsys.util.ClientConnList;
import com.eqsys.util.ParseUtil;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class ClientSettingController {
	
	private ClientInfo client;

	@FXML
	private RadioButton consRb;
	@FXML
	private RadioButton triWNRb;
	@FXML
	private RadioButton triWRb;
	
	@FXML
	private void initialize() {
		
		ToggleGroup tg = new ToggleGroup();
		consRb.setToggleGroup(tg);
		triWNRb.setToggleGroup(tg);
		triWRb.setToggleGroup(tg);
		consRb.setSelected(false);
		triWNRb.setSelected(false);
		triWRb.setSelected(false);
		
	}
	
	public void setClient(ClientInfo info) {
		client = info;
		initInfo();
	}
	private void initInfo() {
		String mode = client.getTransMode();
		RadioButton rb;
		////////////////////////
	}

	@FXML
	private void handleCancel(){
		
	}
}
