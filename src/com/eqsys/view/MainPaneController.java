package com.eqsys.view;

import java.io.IOException;

import com.eqsys.model.ClientInfo;
import com.eqsys.model.RecvInfo;
import com.eqsys.util.ClientConnList;
import com.eqsys.util.TmpOblist;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

/**
 * 主工作窗口
 *
 */
public class MainPaneController {

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
	private TableView recvInfoTable;
	@FXML
	private TableColumn<RecvInfo, String> timeColumn;
	@FXML
	private TableColumn<RecvInfo, String> srcColumn;
	@FXML
	private TableColumn<RecvInfo, String> typeColumn;
	
	Callback<TableColumn<ClientInfo, String>, TableCell<ClientInfo, String>> cellFactory =
	        new Callback<TableColumn<ClientInfo, String>, TableCell<ClientInfo, String>>() {
	    public TableCell<ClientInfo, String> call(TableColumn<ClientInfo, String> p) {
	        TableCell<ClientInfo, String> cell = new TableCell<ClientInfo, String>();// {
//
//	            @Override
//	            public void updateItem(Integer item, boolean empty) {
//	                super.updateItem(item, empty);
//	                setText((item == null || empty) ? null : item.toString());
//	                setGraphic(null);
//	            }
//	        };

	        cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent event) {
	                if (event.getClickCount() > 1) {
	                    System.err.println("double clicked!");
	                    TableCell c = (TableCell) event.getSource();
	                    System.out.println("Cell text: " + c.getText());
	                }
	            }
	        });
	        return cell;
	    }
	};

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
//		stationId.setCellFactory(value);
		stationId.setCellValueFactory(new PropertyValueFactory<ClientInfo, String>("id"));
		transMode.setCellValueFactory(new PropertyValueFactory("transMode"));
		sensitivity
				.setCellValueFactory(new PropertyValueFactory("sensitivity"));
		triggerThreshold.setCellValueFactory(new PropertyValueFactory(
				"triggerThreshold"));
		transMode.setCellValueFactory(new PropertyValueFactory("transMode"));
		//实现对ClientInfo中一行的双击响应
		clientTable.setRowFactory(new Callback<TableView<ClientInfo>, TableRow<ClientInfo>>() {
			
			@Override
			public TableRow<ClientInfo> call(TableView<ClientInfo> param) {
				return new TableRowControl(); 
			}
		});
		clientTable.setItems(ClientConnList.getInstance().getObservableList());
		
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
	
	class TableRowControl extends TableRow<ClientInfo> {  
		  
        public TableRowControl() {  
            super();  
            this.setOnMouseClicked(new EventHandler<MouseEvent>() {  
                @Override  
                public void handle(MouseEvent event) {  
                    if (event.getButton().equals(MouseButton.PRIMARY)  //左键
                            && event.getClickCount() == 2  				//双击
                            && TableRowControl.this.getIndex() < clientTable.getItems().size()) {
                    	ClientInfo info = clientTable.getSelectionModel().getSelectedItem();
                    	//
                    }  
                }  
            });  
        }  
    }  

}
