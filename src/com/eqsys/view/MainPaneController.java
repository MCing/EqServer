package com.eqsys.view;

import java.io.IOException;

import com.eqsys.application.EqServer;
import com.eqsys.model.ClientInfo;
import com.eqsys.model.RecvInfo;
import com.eqsys.model.SysEvent;
import com.eqsys.util.ClientConnList;
import com.eqsys.util.ClientWindowMgr;
import com.eqsys.util.JDBCHelper;
import com.eqsys.util.ParseUtil;
import com.eqsys.util.SysConfig;
import com.eqsys.util.TmpOblist;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

/**
 * 主工作窗口
 *
 */
public class MainPaneController extends FXMLController {

	private EqServer main;

	private String clientDetailPath = "/com/eqsys/view/ClientDetailLayout.fxml";
	private String nodeMgrPath = "/com/eqsys/view/NodeManagerLayout.fxml";
	private String dbSettingPath = "/com/eqsys/view/DatabaseSettingLayout.fxml";
	private String dataAnalyPath = "/com/eqsys/view/DataAnalysisLayout.fxml";

	// 客户端信息
	@FXML
	private TableView<ClientInfo> clientTable;
	@FXML
	private TableColumn<ClientInfo, String> stationId, transMode;
	// @FXML
	// private TableColumn<ClientInfo, String> transMode;
	@FXML
	private TableColumn<ClientInfo, Integer> sensitivity, triggerThreshold;
	// @FXML
	// private TableColumn<ClientInfo, Integer> triggerThreshold;

	// 接收消息信息
	@FXML
	private TableView<RecvInfo> recvInfoTable;
	@FXML
	private TableColumn<RecvInfo, String> timeColumn, srcColumn, typeColumn;
	// @FXML
	// private TableColumn<RecvInfo, String> srcColumn;
	// @FXML
	// private TableColumn<RecvInfo, String> typeColumn;

	// 系统事件
	@FXML
	private TableView<SysEvent> eventTable;
	@FXML
	private TableColumn<SysEvent, String> stimeColumn, ssrcColumn, eventColumn;
	// @FXML
	// private TableColumn<SysEvent, String> ssrcColumn;
	// @FXML
	// private TableColumn<SysEvent, String> eventColumn;

	// 台网信息
	@FXML
	private Label serverId, serverIp, onLineLabel;
	// @FXML
	// private Label serverIp;
	// @FXML
	// private Label onLineLabel;

	// 数据库信息
	@FXML
	private Label dbName, dbType, dbState;
	// @FXML
	// private Label dbType;
	// @FXML
	// private Label dbState;

	// 快捷键
	@FXML
	private Button connDbBtn, nodeMgrBtn, analysisyBtn;
	// @FXML
	// private Button nodeMgrBtn;
	// @FXML
	// private Button analysisyBtn;

	@FXML
	private Label utcTimeLabel;

	// 主页面
	@FXML
	private TabPane mainTabPane;
	private Tab dataAnalysisTab;
	private Tab nodeMgrTab;
	private SingleSelectionModel<Tab> workspSelectMode;

	@FXML
	protected void initialize() {
	}

	public void initController(EqServer main) {
		this.main = main;

		initWorkspace();
		initDbInfo();
		initTableView();
		initShortKey();
	}

	/** 初始化主工作区(主页) */
	private void initWorkspace() {

		workspSelectMode = mainTabPane.getSelectionModel();
		serverId.setText(SysConfig.serverId);
		serverIp.setText(SysConfig.serverIp);
		onLineLabel.textProperty().bind(
				ClientConnList.getInstance().getOnlineNumber());
	}

	/** 初始换全部TableView */
	private void initTableView() {

		// 数据接收tableview
		timeColumn
				.setCellValueFactory(new PropertyValueFactory<RecvInfo, String>(
						"time"));
		srcColumn
				.setCellValueFactory(new PropertyValueFactory<RecvInfo, String>(
						"srcId"));
		typeColumn
				.setCellValueFactory(new PropertyValueFactory<RecvInfo, String>(
						"type"));
		recvInfoTable.setItems(TmpOblist.getRecvObserList());

		// 已连接台站信息tableview
		stationId
				.setCellValueFactory(new PropertyValueFactory<ClientInfo, String>(
						"stationId"));
		transMode
				.setCellValueFactory(new PropertyValueFactory<ClientInfo, String>(
						"transMode"));
		sensitivity
				.setCellValueFactory(new PropertyValueFactory<ClientInfo, Integer>(
						"sensitivity"));
		triggerThreshold
				.setCellValueFactory(new PropertyValueFactory<ClientInfo, Integer>(
						"threshold"));
		transMode
				.setCellValueFactory(new PropertyValueFactory<ClientInfo, String>(
						"transMode"));
		// 实现对ClientInfo中一行的双击响应
		clientTable
				.setRowFactory(new Callback<TableView<ClientInfo>, TableRow<ClientInfo>>() {

					@Override
					public TableRow<ClientInfo> call(TableView<ClientInfo> param) {
						return new TableRowControl();
					}
				});
		clientTable.setItems(ClientConnList.getInstance().getObservableList());

		// 系统事件tableview
		stimeColumn
				.setCellValueFactory(new PropertyValueFactory<SysEvent, String>(
						"time"));
		ssrcColumn
				.setCellValueFactory(new PropertyValueFactory<SysEvent, String>(
						"srcId"));
		eventColumn
				.setCellValueFactory(new PropertyValueFactory<SysEvent, String>(
						"event"));

		eventTable.setItems(TmpOblist.getsysEventObserList());

	}

	/** 初始化快捷键区 */
	private void initShortKey() {

		// 快捷键提示
		connDbBtn.setTooltip(new Tooltip("连接数据库"));
		nodeMgrBtn.setTooltip(new Tooltip("台站管理"));
		analysisyBtn.setTooltip(new Tooltip("数据分析"));

		utcTimeLabel.textProperty().bind(main.getUTCTime());
	}

	/********************************************************************
	 * 
	 * 菜单+快捷键 区
	 * 
	 ********************************************************************/
	/** 连接数据库 */
	@FXML
	private void handleConnectDb() {

		if (JDBCHelper.initDB()) {
			initDbInfo();
		}
	}

	/** 数据库配置 */
	@FXML
	private void handleDbSetting() {
		openDbSettingDialog();
	}

	/** 打开数据库设置对话框 */
	private void openDbSettingDialog() {

		Stage stage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(ParseUtil.getFXMLURL(dbSettingPath));
		Node page = null;
		try {
			page = loader.load();
			DatabaseSettingController controller = loader.getController();
			controller.initController(stage);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		stage.initModality(Modality.APPLICATION_MODAL); // 模态窗口
		stage.initOwner(clientTable.getScene().getWindow()); // 任意一个控件可获得其所属窗口对象
		Scene scene = new Scene((Parent) page);
		stage.setTitle("数据库设置");
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.show();
	}

	/********************************************************************
	 * 
	 * 已连接台站区
	 * 
	 ********************************************************************/
	/** 打开台站详情窗口 */
	public void openClientDetail(final ClientInfo clientInfo) {

		ClientWindowMgr manager = ClientWindowMgr.getClientWindowMgr();
		if (manager.isExist(clientInfo.getStationId())) {
			manager.open(clientInfo.getStationId());
			return;
		}
		final Stage stage = new Stage();

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(ParseUtil.getFXMLURL(clientDetailPath));
		Node page = null;
		final ClientDetailController controller;
		try {
			page = loader.load();
			controller = loader.getController();
//			controller.initController(stage, clientInfo);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		Scene scene = new Scene((Parent) page);
		stage.setTitle("台站:"+clientInfo.getStationId());
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.setOnShown(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				//每次从新打开窗口都要更新数据
				controller.initController(stage, clientInfo);
			}
		});
		stage.show();
		manager.add(clientInfo.getStationId(), controller);
	}

	/**
	 * 用于控制clientInfo table 的行(实现双击响应)
	 *
	 */
	class TableRowControl extends TableRow<ClientInfo> {

		public TableRowControl() {
			super();
			this.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (event.getButton().equals(MouseButton.PRIMARY) // 左键
							&& event.getClickCount() == 2 // 双击
							&& TableRowControl.this.getIndex() < clientTable
									.getItems().size()) {
						ClientInfo clientInfo = clientTable.getSelectionModel()
								.getSelectedItem();
						openClientDetail(clientInfo);
					}
				}
			});
		}
	}

	/** 右键菜单,查看详情 */
	@FXML
	private void handleDetail(){
		
		int index = clientTable.getSelectionModel().getSelectedIndex();
		if(index >=0 && index < clientTable.getItems().size()){
			ClientInfo clientInfo = clientTable.getSelectionModel().getSelectedItem();
			openClientDetail(clientInfo);
		}
	}
	/********************************************************************
	 * 
	 * 主工作区
	 * 
	 ********************************************************************/
	private void initDbInfo() {
		dbName.setText(SysConfig.jdbcServerName);
		dbType.setText("mysql");
		if (JDBCHelper.getDbState()) {
			dbState.setText("已连接");
			dbState.setTextFill(Color.GREEN);
		} else {
			dbState.setText("未连接");
			dbState.setTextFill(Color.RED);
		}
	}

	/** 打开节点管理界面 */
	@FXML
	private void handleNodeMgr() {

		if (nodeMgrTab == null) {
			nodeMgrTab = new Tab();
			nodeMgrTab.setContent(getNodeFromFXML(nodeMgrPath));
			nodeMgrTab.setText("台站管理");
		}
		if (!mainTabPane.getTabs().contains(nodeMgrTab)) {

			mainTabPane.getTabs().add(nodeMgrTab);
		}
		workspSelectMode.select(nodeMgrTab);
	}

	/** 打开节点管理窗口 */
	private Node getNodeFromFXML(String path) {

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(ParseUtil.getFXMLURL(path));
		try {
			return loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/** 打开数据分析tab */
	@FXML
	private void handleDataAnalysis() {
		if (dataAnalysisTab == null) {
			dataAnalysisTab = new Tab();
			dataAnalysisTab.setContent(getNodeFromFXML(dataAnalyPath));
			dataAnalysisTab.setText("数据分析");
		}
		if (!mainTabPane.getTabs().contains(dataAnalysisTab)) {

			mainTabPane.getTabs().add(dataAnalysisTab);
		}
		workspSelectMode.select(dataAnalysisTab);
	}

}
