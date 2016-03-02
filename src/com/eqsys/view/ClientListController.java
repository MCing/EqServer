package com.eqsys.view;

import com.eqsys.model.ClientInfo;
import com.eqsys.util.ClientConnList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ClientListController {

	@FXML
    private TableView<ClientInfo> clientTable;
    @FXML
    private TableColumn<ClientInfo, String> stationId;
    @FXML
    private TableColumn<ClientInfo, String> transMode;
//    @FXML
//    private TableColumn<ClientInfo, Integer> longitude;
//    @FXML
//    private TableColumn<ClientInfo, Integer> latitude;
//    @FXML
//    private TableColumn<ClientInfo, Integer> altitude;
    @FXML
    private TableColumn<ClientInfo, Integer> sensitivity;
    @FXML
    private TableColumn<ClientInfo, Integer> triggerThreshold;
    
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	
    	// Initialize the person table with the two columns.
    	stationId.setCellValueFactory(new PropertyValueFactory("id"));
    	transMode.setCellValueFactory(new PropertyValueFactory("transMode"));
//    	longitude.setCellValueFactory(new PropertyValueFactory("longitude"));
//    	altitude.setCellValueFactory(new PropertyValueFactory("altitude"));
//    	latitude.setCellValueFactory(new PropertyValueFactory("latitude"));
    	sensitivity.setCellValueFactory(new PropertyValueFactory("sensitivity"));
    	triggerThreshold.setCellValueFactory(new PropertyValueFactory("triggerThreshold"));
    	transMode.setCellValueFactory(new PropertyValueFactory("transMode"));
    	
    	clientTable.setItems(ClientConnList.getInstance().getObservableList());
    	
    	clientTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ClientInfo>() {

			@Override
			public void changed(ObservableValue<? extends ClientInfo> observable, ClientInfo oldValue, ClientInfo newValue) {
				
				int selectedIndex = clientTable.getSelectionModel().getSelectedIndex();
				System.out.println("select index:"+selectedIndex);
				if(selectedIndex >= 0){
				}
				
			}

		});;
        	
    }
}
