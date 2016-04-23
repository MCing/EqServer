package com.eqsys.view;

import java.io.IOException;
import java.util.List;

import com.eqsys.dao.ClientInfoDao;
import com.eqsys.model.ClientInfo;
import com.eqsys.model.RecvInfo;
import com.eqsys.util.ParseUtil;
import com.eqsys.util.TmpOblist;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NodeManagerController {
	
	private ObservableList<ClientInfo> nodeList = FXCollections.observableArrayList();
	private ClientInfoDao nodeInfoDao = new ClientInfoDao();
	private String nodeEditPath = "/com/eqsys/view/NodeEditLayout.fxml";
	
	@FXML
	private TableView<ClientInfo> nodeTable;
	@FXML
	private TableColumn<ClientInfo, String> stationId;
	@FXML
	private TableColumn<ClientInfo, Integer> triggerThreshold;
	@FXML
	private TableColumn<ClientInfo, Integer> sensitivity;
	@FXML
	private TableColumn<ClientInfo, Integer> longitude;
	@FXML
	private TableColumn<ClientInfo, Integer> latitude;
	@FXML
	private TableColumn<ClientInfo, Integer> altitude;
	
	@FXML
	private void initialize() {
		
		//init tableview
		stationId.setCellValueFactory(new PropertyValueFactory<ClientInfo, String>("stationId"));
		sensitivity
				.setCellValueFactory(new PropertyValueFactory<ClientInfo, Integer>("sensitivity"));
		triggerThreshold.setCellValueFactory(new PropertyValueFactory<ClientInfo, Integer>(
				"threshold"));
		longitude.setCellValueFactory(new PropertyValueFactory<ClientInfo, Integer>(
				"longitude"));
		latitude.setCellValueFactory(new PropertyValueFactory<ClientInfo, Integer>(
				"latitude"));
		altitude.setCellValueFactory(new PropertyValueFactory<ClientInfo, Integer>(
				"altitude"));
		nodeTable.setItems(nodeList);
		
		updateList();
		
	}
	
	/** 编辑一条记录 */
	@FXML
	private void handleEditNode(){
		ClientInfo newNode = nodeTable.getSelectionModel().getSelectedItem();
		if(newNode != null){
			openNodeEditDialog(newNode);
		}
	}
	
	/** 删除一条记录 */
	@FXML
	private void handleDelNode(){
		int index = nodeTable.getSelectionModel().getSelectedIndex();
		if(index >=0 && index < nodeList.size()){
			ClientInfo delItem = nodeTable.getSelectionModel().getSelectedItem();
			if(nodeInfoDao.del(delItem.getStationId())){
				nodeList.remove(delItem);
			}
		}
	}
	
	/** 添加一条记录 */
	@FXML
	private void handleAddNode(){
		ClientInfo newNode = new ClientInfo();
		openNodeEditDialog(newNode);
	}
	
	/**
	 * 打开节点编辑窗口，用于添加或修改节点
	 * @param node
	 */
	private void openNodeEditDialog(ClientInfo node){
		Stage stage = new Stage();
    	FXMLLoader loader = new FXMLLoader();
		loader.setLocation(ParseUtil.getFXMLURL(nodeEditPath));
		Node page = null;
		try {
			page = loader.load();
			NodeEditController controller = loader.getController();
			controller.setNode(node);
			controller.setStage(stage);
			
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		stage.initModality(Modality.APPLICATION_MODAL);     //模态窗口
		stage.initOwner(nodeTable.getScene().getWindow());  //任意一个控件可获得其所属窗口对象
		Scene scene = new Scene((Parent) page);
		stage.setScene(scene);
    	stage.centerOnScreen();
    	stage.showAndWait();   //在此阻塞
    	updateList();
	}
	
	/** 从数据库更新列表 */
	private void updateList(){
		
		List<ClientInfo> list = nodeInfoDao.get();
		nodeList.clear();
		nodeList.addAll(list);
	}
}
