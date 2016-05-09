package com.eqsys.view;

import java.io.IOException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import com.eqsys.dao.ClientInfoDao;
import com.eqsys.dao.StatusDataDao;
import com.eqsys.dao.TrgDataDao;
import com.eqsys.dao.WavefDataDao;
import com.eqsys.model.ClientInfo;
import com.eqsys.util.ClientWindowMgr;
import com.eqsys.util.ParseUtil;

public class DataAnalysisController extends FXMLController {
	
	private ObservableList<ClientInfo> nodeList = FXCollections.observableArrayList();
	private String clientDetailPath = "/com/eqsys/view/ClientDetailLayout.fxml";
	
	@FXML
	private TableView<ClientInfo> listTable;
	@FXML
	private TableColumn<ClientInfo, String> idColumn;
	@FXML
	private TableColumn<ClientInfo, Integer> wavefColumn;
	@FXML
	private TableColumn<ClientInfo, Integer> triColumn;
	@FXML
	private TableColumn<ClientInfo, Integer> statusColumn;
	
	@FXML
	protected void initialize() {
		idColumn.setCellValueFactory(new PropertyValueFactory<ClientInfo, String>("stationId"));
		wavefColumn
				.setCellValueFactory(new PropertyValueFactory<ClientInfo, Integer>("wavefDataCount"));
		triColumn.setCellValueFactory(new PropertyValueFactory<ClientInfo, Integer>(
				"triDataCount"));
		statusColumn.setCellValueFactory(new PropertyValueFactory<ClientInfo, Integer>(
				"statusDataCount"));
		listTable.setItems(nodeList);
		
		listTable.setRowFactory(new Callback<TableView<ClientInfo>, TableRow<ClientInfo>>() {
			
			@Override
			public TableRow<ClientInfo> call(TableView<ClientInfo> param) {
				return new TableRowControl(); 
			}
		});
		updateList();
		
	}
	
	/** 从数据库更新数据  */
	private void updateList() {
		//获取stationid
		List<ClientInfo> list = ClientInfoDao.get();
		//获取波形数据数量
		//获取触发信息数量
		//获取状态信息数量
		for(ClientInfo node: list){
			int wavefCount = WavefDataDao.getCount(node.getStationId(), 0, Long.MAX_VALUE);
			int triCount = TrgDataDao.getCount(node.getStationId());
			int statusCount = StatusDataDao.getCount(node.getStationId());
			node.setWavefDataCount(wavefCount);
			node.setTriDataCount(triCount);
			node.setStatusDataCount(statusCount);
		}
		nodeList.clear();
		nodeList.addAll(list);
	}

	/**
	 * 用于控制node table 的行(实现双击响应)
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
                            && TableRowControl.this.getIndex() < listTable.getItems().size()) {
                    	ClientInfo clientInfo = listTable.getSelectionModel().getSelectedItem();
                    	openClientDetail(clientInfo);
                    }  
                }

            });  
        }
       }
	
	/** 右键菜单,查看详情 */
	@FXML
	private void handleDetail(){
		
		int index = listTable.getSelectionModel().getSelectedIndex();
		if(index >=0 && index < nodeList.size()){
			ClientInfo clientInfo = listTable.getSelectionModel().getSelectedItem();
			openClientDetail(clientInfo);
		}
	}
	@FXML
	private void handleRefresh(){
		updateList();
	}
	
	/** 打开客户端详情窗口 */
	public void openClientDetail(ClientInfo clientInfo) {

		ClientWindowMgr manager = ClientWindowMgr.getClientWindowMgr();
		if (manager.isExist(clientInfo.getStationId())) {
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
		stage.setTitle("台站详情");
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.show();
		manager.add(clientInfo.getStationId(), controller);
	}
}
