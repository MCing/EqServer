package com.eqsys.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import com.eqsys.handler.CtrlManager;
import com.eqsys.model.ClientInfo;
import com.eqsys.model.CtrlEvent;
import com.eqsys.msg.MsgConstant;
import com.eqsys.msg.TransModeReq;
import com.eqsys.util.DataBuilder;
import com.eqsys.util.ParseUtil;

public class ClientSettingController extends FXMLController{
	
	private ClientInfo client;
	private Stage dialogStage;
	private ToggleGroup transmodeGroup;

	@FXML
	private RadioButton consRb;
	@FXML
	private RadioButton triWNRb;
	@FXML
	private RadioButton triWRb;
	@FXML
	private Label currModeLab;
	
	@FXML
	protected void initialize() {
		
		transmodeGroup = new ToggleGroup();
		consRb.setToggleGroup(transmodeGroup);
		triWNRb.setToggleGroup(transmodeGroup);
		triWRb.setToggleGroup(transmodeGroup);
		consRb.setSelected(false);
		triWNRb.setSelected(false);
		triWRb.setSelected(false);
		consRb.setUserData((short)1);
		triWNRb.setUserData((short)2);
		triWRb.setUserData((short)3);
	}
	
	public void initController(ClientInfo info, Stage stage) {
		dialogStage = stage;
		client = info;
		initInfo();
	}
	private void initInfo() {
		
		currModeLab.setText(client.getTransMode());
	}

	@FXML
	private void handleCancel(){
		
		dialogStage.close();
	}
	
	@FXML
	private void handleTranMode(){
		Toggle selectedRb = transmodeGroup.getSelectedToggle();
		if(selectedRb != null){
			short selectMode = (short)selectedRb.getUserData();
			System.err.println(selectMode);
			if(selectMode != ParseUtil.parseTransMode(client.getTransMode())){
				CtrlEvent transModeEvent = new CtrlEvent();
				TransModeReq req = new TransModeReq();
				req.setSubCommand(MsgConstant.CMD_TRANSMODE);
				req.setSubTransMode(selectMode);
				transModeEvent.setClient(client);
				transModeEvent.setReqMsg(DataBuilder.buildCtrlReq(client.getStationId(), req));
				CtrlManager.getMagager().ctrlReq(transModeEvent);
			}
		}
		
		dialogStage.close();
	}
}
