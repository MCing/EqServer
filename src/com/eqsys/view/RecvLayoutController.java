package com.eqsys.view;

import com.eqsys.model.RecvInfo;
import com.eqsys.server.EqServer;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class RecvLayoutController {

	
	@FXML
	private TableView recvInfoTable;
	@FXML
	private TableColumn<RecvInfo, String> timeColumn;
	@FXML
	private TableColumn<RecvInfo, String> srcColumn;
	@FXML
	private TableColumn<RecvInfo, String> typeColumn;
	
	@FXML
	private void initialize(){
		
		timeColumn.setCellValueFactory(new PropertyValueFactory<RecvInfo, String>("time"));
		srcColumn.setCellValueFactory(new PropertyValueFactory<RecvInfo, String>("srcId"));
		typeColumn.setCellValueFactory(new PropertyValueFactory<RecvInfo, String>("type"));
		
		recvInfoTable.setItems(EqServer.recvInfoList);
	}
	
}
