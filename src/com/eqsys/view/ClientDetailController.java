package com.eqsys.view;

import com.eqsys.model.ClientInfo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ClientDetailController {

	private ClientInfo client;

	@FXML
	private Label stationIdLab;
	@FXML
	private Label transModeLab;
	@FXML
	private Label longtitudeLab;
	@FXML
	private Label latitudeLab;
	@FXML
	private Label altitudeLab;
	@FXML
	private Label sensitivityLab;
	@FXML
	private Label thresholdLab;
	@FXML
	private Label permitLab;
	@FXML
	private Button transModeBtn;
	@FXML
	private Button thresholdBtn;

	@FXML
	private void initialize() {
		initClientInfo();
	}

	public void setClient(ClientInfo info) {
		client = info;
		initClientInfo();
	}

	/** 设置ClientInfo(客户端信息值)  */
	private void initClientInfo() {
		if (client == null) {
			stationIdLab.setText("");
			transModeLab.setText("");
			longtitudeLab.setText("");
			latitudeLab.setText("");
			altitudeLab.setText("");
			sensitivityLab.setText("");
			thresholdLab.setText("");
			permitLab.setText("");

		} else {
			stationIdLab.setText(client.getId());
			transModeLab.setText(client.getTransMode());
			longtitudeLab.setText(String.valueOf(client.getLongitude()));
			latitudeLab.setText(String.valueOf(client.getLatitude()));
			altitudeLab.setText(String.valueOf(client.getAltitude()));
			sensitivityLab.setText(String.valueOf(client.getSensitivity()));
			thresholdLab.setText(String.valueOf(client.getTriggerThreshold()));
			permitLab.setText(String.valueOf(client.getPermit()));
		}
	}
	
	/** 处理 设置传输模式 */
	@FXML
	private void handleTransMode(){
		
	}
	/** 处理 设置触发阈值 */
	@FXML
	private void handleThreshold(){
		
	}
}
