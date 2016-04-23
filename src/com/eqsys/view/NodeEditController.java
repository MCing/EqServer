package com.eqsys.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
	private Label errorLab;
	
	
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
			return;
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
		boolean ret = true;
		String numRegExp ="^\\d+$";  //非负整数
		String floatRegExp = "^\\d+(\\.\\d+)?$";  //非负浮点数
		StringBuilder error = new StringBuilder();
		String stdId = stationIdTf.getText();
		String altStr = altitudeTf.getText();
		String longStr = longitudeTf.getText();
		String latStr = latitudeTf.getText();
		String triStr = triggerThresholdTf.getText();
		String sensStr = sensitivityTf.getText();
		if(stdId == null ||					//_test  如果不输入台站代码，这里的stdId为null而不是空字符串？？
				"".equals(altStr) || 
				"".equals(longStr) || 
				"".equals(latStr) || 
				"".equals(triStr) || 
				"".equals(stdId) || 
				"".equals(sensStr)){
			error.append("请完成输入！");
			ret = false;
		}else if("".equals(stdId) || stdId.length() > 5){
			error.append("台站代码输入有误！");
			ret = false;
		}else if(!altStr.matches(numRegExp) ||
				!longStr.matches(floatRegExp) ||
				!latStr.matches(floatRegExp) ||
				!sensStr.matches(numRegExp) ||
				!triStr.matches(numRegExp)){
			error.append("数值非法输入！");
			ret = false;
		}
		
		if(!ret){
			errorLab.setText(error.toString());
		}
		return ret;
	}

	@FXML
	private void handleCancel(){
		dialogStage.close();
	}
}
