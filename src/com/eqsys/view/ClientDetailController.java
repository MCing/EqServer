package com.eqsys.view;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import com.eqsys.consts.Constants;
import com.eqsys.dao.WavefDataDao;
import com.eqsys.handler.CtrlManager;
import com.eqsys.model.ClientInfo;
import com.eqsys.model.CtrlEvent;
import com.eqsys.model.WavefDataModel;
import com.eqsys.msg.MsgConstant;
import com.eqsys.msg.TransModeReq;
import com.eqsys.util.ClientConnList;
import com.eqsys.util.DataBuilder;
import com.eqsys.util.ParseUtil;
import com.eqsys.util.UTCTimeUtil;

/**
 * 客户端详情
 *
 */
public class ClientDetailController extends FXMLController {

	private ClientInfo client;
	private Stage stage;

	private ObservableList<WavefDataModel> wavefDataList = FXCollections.observableArrayList();
	private ObservableList<String> wavefXStrings = FXCollections.observableArrayList();
	// 控制 tab
	@FXML
	private Label errorLabel, stationIdLab, transModeLab, longtitudeLab, latitudeLab, altitudeLab, sensitivityLab,thresholdLab, permitLab;
//	@FXML
//	private Label stationIdLab;
//	@FXML
//	private Label transModeLab;
//	@FXML
//	private Label longtitudeLab;
//	@FXML
//	private Label latitudeLab;
//	@FXML
//	private Label altitudeLab;
//	@FXML
//	private Label sensitivityLab;
//	@FXML
//	private Label thresholdLab;
//	@FXML
//	private Label permitLab;
	@FXML
	private Button transModeBtn, thresholdBtn;
//	@FXML
//	private Button thresholdBtn;
	//传输模式控制
	private ToggleGroup transmodeGroup;
	@FXML
	private RadioButton consRb, triWNRb, triWRb;
//	@FXML
//	private RadioButton triWNRb;
//	@FXML
//	private RadioButton triWRb;

	// 波形数据tab
	@FXML
	private BarChart<String, Integer> wavefBar;
	@FXML
	private CategoryAxis wavefXAxis;
	@FXML
	private TableView<WavefDataModel> wavefTable;
	@FXML
	private TableColumn<WavefDataModel, Integer> wavefId;
	@FXML
	private TableColumn<WavefDataModel, String> wavefType, wavefTime;
//	@FXML
//	private TableColumn<WavefDataModel, String> wavefTime;
	@FXML
	private Button wavefQuery;
	@FXML
	private DatePicker datePicker;
	@FXML
	private RadioButton hourRb, dayRb;
//	@FXML
//	private RadioButton dayRb;
	private ToggleGroup radioGroup;
	// 条状图初始化
	private String[] hourStrs = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
			"17", "18", "19", "20", "21", "22", "23", "24" };
	private String[] dayStrs = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
			"17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" };
	
	@FXML
	private DatePicker starttimeDp, endtimeDp;
//	@FXML
//	private DatePicker endtimeDp;
	@FXML
	private Label wavefResultLabel;

	@FXML
	protected void initialize() {
	}

	// 初始化数据
	public void initController(Stage stage, ClientInfo client) {
		this.stage = stage;
		this.client = client;
		initClientInfo();
		initWavefdataTab();
		initCtrlData();
		if(!ClientConnList.getInstance().getState(client.getStationId())){
			updateErrorTip("未连接，无法控制！");
		}
	}
	/** 客户端参数修改成功后更新该窗口 
	 * @param msg */
	public void update(ClientInfo client, String msg) {
		this.client = client;
		initClientInfo();
		updateErrorTip(msg);
	}

	public void show() {
		if (stage.isShowing()) {
			stage.toFront();
		} else {
			stage.show();
		}
	}

	/*********************************************************************************
	 * 控制 tab
	 * 
	 *********************************************************************************/
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
			if(verifyPermit()){
				permitLab.setText("可控制");
			}else{
				permitLab.setText("不可控制");
			}
		}
	}

	/** 验证该服务端是否有控制权限 */
	private boolean verifyPermit() {

		return client.getPermit() == 0 ? true : false;
	}

	/** 台站控制  */
	private void initCtrlData(){
		transmodeGroup = new ToggleGroup();
		consRb.setToggleGroup(transmodeGroup);
		triWNRb.setToggleGroup(transmodeGroup);
		triWRb.setToggleGroup(transmodeGroup);
		switch(ParseUtil.parseTransMode(client.getTransMode())){
			case 1:
				consRb.setSelected(true);
				break;
			case 2:
				triWRb.setSelected(true);
				break;
			case 3:
				triWNRb.setSelected(true);
				break;
		}
		consRb.setUserData((short)1);
		triWRb.setUserData((short)2);
		triWNRb.setUserData((short)3);
	}
	@FXML
	private void handleTranMode(){
		Toggle selectedRb = transmodeGroup.getSelectedToggle();
		if(selectedRb != null){
			short selectMode = (short)selectedRb.getUserData();
			if(selectMode != ParseUtil.parseTransMode(client.getTransMode())){
				CtrlEvent transModeEvent = new CtrlEvent();
				TransModeReq req = new TransModeReq();
				req.setSubCommand(MsgConstant.CMD_TRANSMODE);
				req.setSubTransMode(selectMode);
				transModeEvent.setClient(client);
				transModeEvent.setReqMsg(DataBuilder.buildCtrlReq(client.getStationId(), req));
				CtrlManager.getMagager().ctrlReq(transModeEvent);
			}
		}
	}
	private void updateErrorTip(String msg){
		
		errorLabel.setText(msg);
	}
	/*********************************************************************************
	 * 
	 * 波形数据 tab
	 * 
	 *********************************************************************************/
	@FXML
	/** 波形数据 表格查询 */
	private void handleWavefQuery2() {

		LocalDate localstarttime = starttimeDp.getValue();
		LocalDate localendtime = endtimeDp.getValue();
		if (localstarttime != null && localendtime != null) {

			long starttime = localstarttime.toEpochDay() * 24 * 60 * 60 * 1000;
			long endtime = localendtime.toEpochDay() * 24 * 60 * 60 * 1000;
			if (starttime == endtime) { // 如果是同一天则查当天的记录
				endtime = (localendtime.toEpochDay() + 1) * 24 * 60 * 60 * 1000;
			}
			updateWavefTable(starttime, endtime);
		}
	}
	/** 初始化波形数据tab */
	private void initWavefdataTab() {

		// init tableview
		wavefId.setCellValueFactory(new PropertyValueFactory<WavefDataModel, Integer>("id"));
		wavefType.setCellValueFactory(new PropertyValueFactory<WavefDataModel, String>("type"));
		wavefTime.setCellValueFactory(new PropertyValueFactory<WavefDataModel, String>("time"));
		wavefTable.setItems(wavefDataList);
		// 条件初始化
		radioGroup = new ToggleGroup();
		hourRb.setToggleGroup(radioGroup);
		dayRb.setToggleGroup(radioGroup);
		hourRb.setSelected(true);
		dayRb.setSelected(false);
		hourRb.setUserData((short) 1);
		dayRb.setUserData((short) 2);
		datePicker.setValue(LocalDate.now());

		wavefXAxis.setCategories(wavefXStrings);
		starttimeDp.setValue(LocalDate.now());
		endtimeDp.setValue(LocalDate.now());
	}
	/** 查询 */
	@FXML
	private void handleWavefQuery() {

		Toggle selectedRb = radioGroup.getSelectedToggle();

		// 该时间不是UTC时间
		LocalDate date = datePicker.getValue();
		long time = date.toEpochDay() * 24 * 60 * 60 * 1000;
		updataWavefBar((short) selectedRb.getUserData(), UTCTimeUtil.getUTCTimeLong(time));
	}

	/**
	 * 更新波形数据统计条
	 * 
	 * @param type
	 *            1:按小时 2:按天
	 * @param starttime
	 *            起始UTC时间(0点)
	 */
	private void updataWavefBar(short type, long starttime) {
		long interval = 0; // 间隔,单位毫秒
		int[] result = null;
		wavefXStrings.clear();
		if (type == 1) { // 按小时
			wavefXStrings.addAll(hourStrs);
			interval = 60 * 60 * 1000; // 时间间隔 为1小时
			result = new int[hourStrs.length];
		} else if (type == 2) { // 按天

			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(starttime);
			int year = cal.get(Calendar.YEAR);
			int mon = cal.get(Calendar.MONTH);
			int days = UTCTimeUtil.getDaysOfMonth(year, mon + 1); // Calendar月份是从0开始算的
			starttime = UTCTimeUtil.getFirstDayTime(starttime);
			wavefXStrings.addAll(Arrays.copyOf(dayStrs, days)); // 根据月份天数，截取与该月份天数同样长度的数组
			interval = 24 * 60 * 60 * 1000; // 时间间隔为1天
			result = new int[days];
		}
		XYChart.Series<String, Integer> series = new XYChart.Series<>();
		wavefBar.getData().clear();
		for (int j = 0; j < result.length; j++) {
			result[j] = WavefDataDao.getCount(client.getStationId(), (starttime + j * interval),
					(starttime + (j + 1) * interval));
			series.getData().add(new XYChart.Data<>(wavefXStrings.get(j), result[j]));
		}
		wavefBar.getData().add(series);
	}

	/** 从数据库获取数据更新波形数据列表 */
	private void updateWavefTable(long starttime, long endtime) {

		wavefDataList.clear();
		List<WavefDataModel> list = WavefDataDao.getRecord(client.getStationId(), starttime, endtime);
		wavefDataList.addAll(list);
		wavefResultLabel.setText(String.valueOf(list.size()));
	}

	@FXML
	private void handleTest() {
		wavefBar.getData().clear();
	}

	/*********************************************************************************
	 * 
	 * 触发数据 tab
	 * 
	 *********************************************************************************/
	

	

	
}
