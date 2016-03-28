package com.eqsys.view;

import java.io.IOException;

import com.eqsys.model.ClientInfo;
import com.eqsys.model.RecvInfo;
import com.eqsys.util.ClientConnList;
import com.eqsys.util.TmpOblist;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

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
		stationId.setCellValueFactory(new PropertyValueFactory("id"));
		transMode.setCellValueFactory(new PropertyValueFactory("transMode"));
		sensitivity
				.setCellValueFactory(new PropertyValueFactory("sensitivity"));
		triggerThreshold.setCellValueFactory(new PropertyValueFactory(
				"triggerThreshold"));
		transMode.setCellValueFactory(new PropertyValueFactory("transMode"));

		clientTable.setItems(ClientConnList.getInstance().getObservableList());

		clientTable.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<ClientInfo>() {

					@Override
					public void changed(
							ObservableValue<? extends ClientInfo> observable,
							ClientInfo oldValue, ClientInfo newValue) {

						int selectedIndex = clientTable.getSelectionModel()
								.getSelectedIndex();
						System.out.println("select index:" + selectedIndex);
						if (selectedIndex >= 0) {
						}

					}

				});
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

}
