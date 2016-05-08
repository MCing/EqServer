package com.eqsys.view;

import java.io.IOException;

import com.eqsys.model.ClientInfo;
import com.eqsys.model.RecvInfo;
import com.eqsys.model.WavefDataModel;
import com.eqsys.util.ClientConnList;
import com.eqsys.util.ClientWindowMgr;
import com.eqsys.util.ParseUtil;
import com.eqsys.util.TmpOblist;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * 客户端详情
 *
 */
public class ClientDetailController extends FXMLController {

	private ClientInfo client;
	private Stage stage;
	private String settingPath = "/com/eqsys/view/ClientSettingLayout.fxml";

	//控制 tab
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
	
	//波形数据tab
	@FXML
	private BarChart<String, Integer> wavefBar;
	@FXML
	private CategoryAxis wavefXAxis;
	@FXML
	private TableView<WavefDataModel> wavefTable;
	@FXML
	private TableColumn<WavefDataModel, Integer> wavefId;
	@FXML
	private TableColumn<WavefDataModel, String> wavefType;
	@FXML
	private TableColumn<WavefDataModel, String> wavefTime;
	@FXML
	private Button wavefQuery;
	@FXML
	private DatePicker datePicker;

	@FXML
	protected void initialize() {
		initClientInfo();
	}

	public void initController(Stage stage, ClientInfo client){
		this.stage = stage;
		this.client = client;
		initClientInfo();
		initWavefdataTab();
	}

	/** 初始化波形数据tab */
	private void initWavefdataTab() {
		
		wavefId
		.setCellValueFactory(new PropertyValueFactory<WavefDataModel, Integer>(
				"id"));
		wavefType
		.setCellValueFactory(new PropertyValueFactory<WavefDataModel, String>(
				"type"));
		wavefTime
		.setCellValueFactory(new PropertyValueFactory<WavefDataModel, String>(
				"time"));
		//not finish
		wavefTable.setItems(null);
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
			stationIdLab.setText(client.getStationId());
			transModeLab.setText(client.getTransMode());
			longtitudeLab.setText(String.valueOf(client.getLongitude()));
			latitudeLab.setText(String.valueOf(client.getLatitude()));
			altitudeLab.setText(String.valueOf(client.getAltitude()));
			sensitivityLab.setText(String.valueOf(client.getSensitivity()));
			thresholdLab.setText(String.valueOf(client.getThreshold()));
			permitLab.setText(String.valueOf(client.getPermit()));
		}
	}

	/** 处理 设置传输模式 */
	@FXML
	private void handleTransMode() {
		if(verifyPermit()){
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
	
	/** 打开客户端控制界面 */
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
    	stage.showAndWait();
	}
	
	/** 客户端参数修改成功后更新该窗口 */
	public void update(ClientInfo client){
		this.client = client;
		initClientInfo();
	}
	
	public void show(){
		if(stage.isShowing()){
			stage.toFront();
		}else{
			stage.show();
		}
	}
}
