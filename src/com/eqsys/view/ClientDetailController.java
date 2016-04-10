package com.eqsys.view;

import java.io.IOException;

import com.eqsys.model.ClientInfo;
import com.eqsys.util.ParseUtil;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ClientDetailController {

	private ClientInfo client;
	private String settingPath = "/com/eqsys/view/ClientSettingLayout.fxml";

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

	/** 设置ClientInfo(客户端信息值) */
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
	private void handleTransMode() {
		if(verifyPermit()){
			//new ModalDialog(permitLab.getScene().getWindow());
			openClientSetting();
		}else{
			//没有控制权限
		}
	}

	/** 验证该服务端是否有控制权限 */
	private boolean verifyPermit() {
		
		return client.getPermit() == 0 ? true : false;
	}

	/** 处理 设置触发阈值 */
	@FXML
	private void handleThreshold() {
//		new ModalDialog(permitLab.getScene().getWindow());
	}
	
	private void openClientSetting() {

		Stage stage = new Stage();
    	FXMLLoader loader = new FXMLLoader();
		loader.setLocation(ParseUtil.getFXMLURL(settingPath));
		Node page = null;
		try {
			page = loader.load();
			ClientSettingController controller = loader.getController();
			controller.initController(client, stage);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		stage.initModality(Modality.APPLICATION_MODAL);     //模态窗口
		stage.initOwner(permitLab.getScene().getWindow());  //任意一个控件可获得其所属窗口对象
		stage.setTitle("客户端控制");
		Scene scene = new Scene((Parent) page);
		stage.setScene(scene);
    	stage.centerOnScreen();
    	stage.show();
	}  

	private class ModalDialog {
		Button btn;

		public ModalDialog(final Window parent) {
			btn = new Button();

			final Stage stage = new Stage();
			
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(parent);
			stage.setTitle("Top Stage With Modality");
			Group root = new Group();
			Scene scene = new Scene(root, 300, 250, Color.LIGHTGREEN);

			btn.setOnAction(new EventHandler<ActionEvent>() {

				public void handle(ActionEvent event) {
					stage.hide();

				}
			});

			btn.setLayoutX(100);
			btn.setLayoutY(80);
			btn.setText("OK");

			root.getChildren().add(btn);
			stage.setScene(scene);
			stage.show();

		}
	}

}
