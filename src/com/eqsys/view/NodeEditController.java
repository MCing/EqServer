package com.eqsys.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import com.eqsys.dao.ClientInfoDao;
import com.eqsys.model.ClientInfo;
import com.eqsys.util.ParseUtil;

public class NodeEditController {
	
	private ClientInfo node;
	private Stage dialogStage;
	private ClientInfoDao dao = new ClientInfoDao();
	
	@FXML
	private TextField stationIdTf;
	@FXML
	private TextField longitudeTf;
	@FXML
	private TextField latitudeTf;
	@FXML
	private TextField altitudeTf;
	@FXML
	private TextField sensitivityTf;
	@FXML
	private TextField triggerThresholdTf;
	@FXML
	private Button okBtn;
	@FXML
	private Button cancelBtn;
	
	
	@FXML
	private void initialize() {
		
	}
	
	public void setNode(ClientInfo info){
		node = info;
		
		stationIdTf.setText(node.getStationId());
		longitudeTf.setText(String.valueOf(node.getLongitude()));
		latitudeTf.setText(String.valueOf(node.getLatitude()));
		altitudeTf.setText(String.valueOf(node.getAltitude()));
		sensitivityTf.setText(String.valueOf(node.getSensitivity()));
		triggerThresholdTf.setText(String.valueOf(node.getThreshold()));
		
	}
	
	public void setStage(Stage stage){
		dialogStage = stage;
	}
	
	@FXML
	private void handleOk(){
		if(!isValid()){
//			error!
		}
		node.setStationId(stationIdTf.getText());
		node.setAltitude(Integer.valueOf(altitudeTf.getText()));
		node.setLatitude(Float.valueOf(latitudeTf.getText()));
		node.setLongitude(Float.valueOf(longitudeTf.getText()));
		node.setSensitivity(Integer.valueOf(sensitivityTf.getText()));
		node.setThreshold(Integer.valueOf(triggerThresholdTf.getText()));
		//以下节点属性的值没有提供修改的接口,为了不覆盖之前的值将之前的值(如果存在)赋给现在的值
		node.setPermit(node.getPermit());
		node.setTransMode(node.getTransMode());
		dao.add(node);
		dialogStage.close();
	}
	
	/** 验证输入是否合法 */
	private boolean isValid() {
		return false;
	}

	@FXML
	private void handleCancel(){
		dialogStage.close();
	}
}
