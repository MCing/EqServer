package com.eqsys.view;

import java.io.IOException;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * 主工作窗口
 *
 */
public class MainPaneController {
	
	private String clientDetailPath = "/com/eqsys/view/ClientDetailLayout.fxml";
	private String nodeMgrPath = "/com/eqsys/view/NodeManagerLayout.fxml";
	private String dbSettingPath = "/com/eqsys/view/DatabaseSettingLayout.fxml";

	// 左栏,节点树形
	@FXML
	private TreeView treeView;

	// 客户端信息
	@FXML
	private TableView<ClientInfo> clientTable;
	@FXML
	private TableColumn<ClientInfo, String> stationId;
	@FXML
	private TableColumn<ClientInfo, String> transMode;
	@FXML
	private TableColumn<ClientInfo, Integer> sensitivity;
	@FXML
	private TableColumn<ClientInfo, Integer> triggerThreshold;

	// 接收消息
	@FXML
	private TableView<RecvInfo> recvInfoTable;
	@FXML
	private TableColumn<RecvInfo, String> timeColumn;
	@FXML
	private TableColumn<RecvInfo, String> srcColumn;
	@FXML
	private TableColumn<RecvInfo, String> typeColumn;
	
	//系统事件
	@FXML
	private TableView<SysEvent> eventTable;
	@FXML
	private TableColumn<SysEvent, String> stimeColumn;
	@FXML
	private TableColumn<SysEvent, String> ssrcColumn;
	@FXML
	private TableColumn<SysEvent, String> eventColumn;
	
	//台网信息
	@FXML
	private Label serverId;
	@FXML
	private Label serverIp;
	
	//数据库信息
	@FXML
	private Label dbName;
	@FXML
	private Label dbType;
	@FXML
	private Label dbState;
	
	@FXML
	private void initialize() {

		// 添加各个分区到主窗口
		createTree();
		
		//init server info and database info
		serverId.setText(SysConfig.serverId);
		serverIp.setText(SysConfig.serverIp);
		initDbInfo();
			

		// initialize recv data
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

		// Initialize client info
		stationId.setCellValueFactory(new PropertyValueFactory<ClientInfo, String>("stationId"));
		transMode.setCellValueFactory(new PropertyValueFactory<ClientInfo, String>("transMode"));
		sensitivity
				.setCellValueFactory(new PropertyValueFactory<ClientInfo, Integer>("sensitivity"));
		triggerThreshold.setCellValueFactory(new PropertyValueFactory<ClientInfo, Integer>(
				"threshold"));
		transMode.setCellValueFactory(new PropertyValueFactory<ClientInfo, String>("transMode"));
		//实现对ClientInfo中一行的双击响应
		clientTable.setRowFactory(new Callback<TableView<ClientInfo>, TableRow<ClientInfo>>() {
			
			@Override
			public TableRow<ClientInfo> call(TableView<ClientInfo> param) {
				return new TableRowControl(); 
			}
		});
		clientTable.setItems(ClientConnList.getInstance().getObservableList());
		
		// initialize system event
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

	private void initDbInfo() {
		dbName.setText(SysConfig.jdbcServerName);
		dbType.setText("mysql");
		if(JDBCHelper.getDbState()){
			dbState.setText("已连接");
			dbState.setTextFill(Color.GREEN);
		}else{
			dbState.setText("未连接");
			dbState.setTextFill(Color.RED);
		}
	}

	/** 树形菜单 */
	private void createTree() {

		// create root
		TreeItem<String> root = new TreeItem<String>("Root");
		// root.setExpanded(true);
		// create child
		TreeItem<String> itemChild = new TreeItem<String>("Child");
		itemChild.setExpanded(false);
		// root is the parent of itemChild
		root.getChildren().add(itemChild);
		treeView.setRoot(root);
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
                    if (event.getButton().equals(MouseButton.PRIMARY)  //左键
                            && event.getClickCount() == 2  				//双击
                            && TableRowControl.this.getIndex() < clientTable.getItems().size()) {
                    	ClientInfo clientInfo = clientTable.getSelectionModel().getSelectedItem();
                    	openClientDetail(clientInfo);
                    }  
                }

            });  
        }  
    }  
	Stage stage = null;/// = new Stage();
	/** 打开客户端详情窗口  */
	private void openClientDetail(ClientInfo clientInfo) {

		ClientWindowMgr manager = ClientWindowMgr.getClientWindowMgr();
		if(manager.isExist(clientInfo.getStationId())){
			manager.open(clientInfo.getStationId());
			return;
		}
		
		Stage stage = new Stage();
		
    	FXMLLoader loader = new FXMLLoader();
		loader.setLocation(ParseUtil.getFXMLURL(clientDetailPath));
		Node page = null;
		ClientDetailController controller = null;
		try {
			page = loader.load();
			controller = loader.getController();
			controller.initController(stage, clientInfo);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		Scene scene = new Scene((Parent) page);
		stage.setTitle("客户端详情");
		stage.setScene(scene);
    	stage.centerOnScreen();
    	stage.show();
    	manager.add(clientInfo.getStationId(), controller);
	}
	
	/** 节点管理 */
	@FXML
	private void handleNodeMgr(){
		openNodeMgr();
	}
	/** 打开节点管理窗口  */
	private void openNodeMgr() {

		Stage stage = new Stage();
    	FXMLLoader loader = new FXMLLoader();
		loader.setLocation(ParseUtil.getFXMLURL(nodeMgrPath));
		Node page = null;
		try {
			page = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		stage.initModality(Modality.APPLICATION_MODAL);     //模态窗口
		stage.initOwner(clientTable.getScene().getWindow());  //任意一个控件可获得其所属窗口对象
		Scene scene = new Scene((Parent) page);
		stage.setTitle("节点管理");
		stage.setScene(scene);
    	stage.centerOnScreen();
    	stage.show();
	}
	
	/** 连接数据库 */
	@FXML
	private void handleConnectDb(){
		
		if(JDBCHelper.initDB()){
			initDbInfo();
		}
	}
	
	/** 数据库配置 */
	@FXML 
	private void handleDbSetting(){
		openDbSettingDialog();
	}

	/** 打开数据库设置对话框  */
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
		stage.initModality(Modality.APPLICATION_MODAL);     //模态窗口
		stage.initOwner(clientTable.getScene().getWindow());  //任意一个控件可获得其所属窗口对象
		Scene scene = new Scene((Parent) page);
		stage.setTitle("数据库设置");
		stage.setScene(scene);
    	stage.centerOnScreen();
    	stage.show();
	}
}
