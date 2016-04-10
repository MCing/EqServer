package com.eqsys.view;

import java.io.IOException;

import com.eqsys.model.ClientInfo;
import com.eqsys.model.RecvInfo;
import com.eqsys.model.SysEvent;
import com.eqsys.util.ClientConnList;
import com.eqsys.util.ParseUtil;
import com.eqsys.util.TmpOblist;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * 主工作窗口
 *
 */
public class MainPaneController {
	
	private String clientDetailPath = "/com/eqsys/view/ClientDetailLayout.fxml";

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
	
	@FXML
	private void initialize() {

		// 添加各个分区到主窗口
		createTree();

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
		stationId.setCellValueFactory(new PropertyValueFactory<ClientInfo, String>("id"));
		transMode.setCellValueFactory(new PropertyValueFactory<ClientInfo, String>("transMode"));
		sensitivity
				.setCellValueFactory(new PropertyValueFactory<ClientInfo, Integer>("sensitivity"));
		triggerThreshold.setCellValueFactory(new PropertyValueFactory<ClientInfo, Integer>(
				"triggerThreshold"));
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

	/** 打开客户端详情窗口  */
	private void openClientDetail(ClientInfo clientInfo) {

		Stage stage = new Stage();
    	FXMLLoader loader = new FXMLLoader();
		loader.setLocation(ParseUtil.getFXMLURL(clientDetailPath));
		Node page = null;
		try {
			page = loader.load();
			ClientDetailController controller = loader.getController();
			controller.setClient(clientInfo);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		Scene scene = new Scene((Parent) page);
		stage.setScene(scene);
    	stage.centerOnScreen();
    	stage.show();
		
	}  
}
