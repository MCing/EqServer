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
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import jfxtras.scene.control.CalendarTimePicker;

import com.eqsys.dao.StatusDataDao;
import com.eqsys.dao.TrgDataDao;
import com.eqsys.dao.WavefDataDao;
import com.eqsys.handler.CtrlManager;
import com.eqsys.model.ClientInfo;
import com.eqsys.model.CtrlEvent;
import com.eqsys.model.WavefDataModel;
import com.eqsys.msg.MsgConstant;
import com.eqsys.msg.PeriodDataReq;
import com.eqsys.msg.TransModeReq;
import com.eqsys.msg.data.StatusData;
import com.eqsys.msg.data.TrgData;
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

	private ObservableList<WavefDataModel> wavefDataList = FXCollections
			.observableArrayList();
	private ObservableList<String> wavefXStrings = FXCollections
			.observableArrayList();
	// 控制 tab
	@FXML
	private Label errorLabel, stationIdLab, transModeLab, longtitudeLab,
			latitudeLab, altitudeLab, sensitivityLab, thresholdLab, permitLab;
	// @FXML
	// private Label stationIdLab;
	// @FXML
	// private Label transModeLab;
	// @FXML
	// private Label longtitudeLab;
	// @FXML
	// private Label latitudeLab;
	// @FXML
	// private Label altitudeLab;
	// @FXML
	// private Label sensitivityLab;
	// @FXML
	// private Label thresholdLab;
	// @FXML
	// private Label permitLab;
	@FXML
	private Button transModeBtn, thresholdBtn;
	// @FXML
	// private Button thresholdBtn;
	// 传输模式控制
	private ToggleGroup transmodeGroup;
	@FXML
	private RadioButton consRb, triWNRb, triWRb;
	// @FXML
	// private RadioButton triWNRb;
	// @FXML
	// private RadioButton triWRb;

	

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
		initTriTab();
		initStatusTab();
		if (!ClientConnList.getInstance().getState(client.getStationId())) {
			updateErrorTip("未连接，无法控制！");
		}else{
			updateErrorTip("");
		}
	}

	/**
	 * 客户端参数修改成功后更新该窗口
	 * 
	 * @param msg
	 */
	public void update(ClientInfo client, String msg) {
		this.client = client;
		initClientInfo();
//		updateErrorTip(msg);
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
	@FXML
	private HBox dataReqPane1;
	@FXML
	private HBox dataReqPane2;
	@FXML
	private DatePicker dataReqDp1;
	@FXML
	private DatePicker dataReqDp2;
	
	private CalendarTimePicker startTp;
	private CalendarTimePicker endTp;
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
			if (verifyPermit()) {
				permitLab.setText("可控制");
			} else {
				permitLab.setText("不可控制");
			}
		}
	}

	/** 验证该服务端是否有控制权限 */
	private boolean verifyPermit() {

		return client.getPermit() == 0 ? true : false;
	}

	/** 台站控制 */
	private void initCtrlData() {
		//模式控制
		transmodeGroup = new ToggleGroup();
		consRb.setToggleGroup(transmodeGroup);
		triWNRb.setToggleGroup(transmodeGroup);
		triWRb.setToggleGroup(transmodeGroup);
		switch (ParseUtil.parseTransMode(client.getTransMode())) {
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
		consRb.setUserData((short) 1);
		triWRb.setUserData((short) 2);
		triWNRb.setUserData((short) 3);
		//申请时间段数据
		starttimeDp.setValue(LocalDate.now());
		endtimeDp.setValue(LocalDate.now());
		startTp = new CalendarTimePicker();
		endTp = new CalendarTimePicker();
		dataReqPane1.getChildren().add(startTp);
		dataReqPane2.getChildren().add(endTp);
	}
	@FXML
	private void handleDataReq(){
		
		//获取开始和结束时间
		long starttime = 0;
		long endtime = 0;
		LocalDate startdate = dataReqDp1.getValue();
		LocalDate enddate = dataReqDp2.getValue();
		Calendar startCal = startTp.getCalendar();
		startCal.set(Calendar.YEAR, startdate.getYear());
		startCal.set(Calendar.MONTH, startdate.getMonthValue()-1);
		startCal.set(Calendar.DATE, startdate.getDayOfMonth());
		starttime = UTCTimeUtil.getUTCTimeLong(startCal.getTimeInMillis());
		Calendar endCal = endTp.getCalendar();
		endCal.set(Calendar.YEAR, enddate.getYear());
		endCal.set(Calendar.MONTH, enddate.getMonthValue()-1);
		endCal.set(Calendar.DATE, enddate.getDayOfMonth());
		endtime = UTCTimeUtil.getUTCTimeLong(endCal.getTimeInMillis());
		System.err.println(UTCTimeUtil.timeFormat1(starttime));
		System.err.println(UTCTimeUtil.timeFormat1(endtime));
		//发送请求
		CtrlEvent transModeEvent = new CtrlEvent();
		PeriodDataReq req = new PeriodDataReq();
		req.setSubCommand(MsgConstant.CMD_PERIODDATA);
		req.setTimeCode(starttime);
		req.setPeriod((int)(endtime - starttime));
		transModeEvent.setClient(client);
		transModeEvent.setReqMsg(DataBuilder.buildCtrlReq(
				client.getStationId(), req));
		CtrlManager.getMagager().ctrlReq(transModeEvent);
	}

	@FXML
	private void handleTranMode() {
		Toggle selectedRb = transmodeGroup.getSelectedToggle();
		if (selectedRb != null) {
			short selectMode = (short) selectedRb.getUserData();
			if (selectMode != ParseUtil.parseTransMode(client.getTransMode())) {
				CtrlEvent transModeEvent = new CtrlEvent();
				TransModeReq req = new TransModeReq();
				req.setSubCommand(MsgConstant.CMD_TRANSMODE);
				req.setSubTransMode(selectMode);
				transModeEvent.setClient(client);
				transModeEvent.setReqMsg(DataBuilder.buildCtrlReq(
						client.getStationId(), req));
				CtrlManager.getMagager().ctrlReq(transModeEvent);
			}
		}
		updateErrorTip("请求发送成功,请注意查看系统事件");
	}

	private void updateErrorTip(String msg) {

		errorLabel.setText(msg);
	}

	/*********************************************************************************
	 * 
	 * 波形数据 tab
	 * 
	 *********************************************************************************/
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
		// @FXML
		// private TableColumn<WavefDataModel, String> wavefTime;
		@FXML
		private Button wavefQuery;
		@FXML
		private DatePicker datePicker;
		@FXML
		private RadioButton hourRb, dayRb;
		// @FXML
		// private RadioButton dayRb;
		private ToggleGroup radioGroup;
		// 条状图初始化
		private String[] hourStrs = { "1", "2", "3", "4", "5", "6", "7", "8", "9",
				"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
				"21", "22", "23", "24" };
		private String[] dayStrs = { "1", "2", "3", "4", "5", "6", "7", "8", "9",
				"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
				"21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" };

		@FXML
		private DatePicker starttimeDp, endtimeDp;
		// @FXML
		// private DatePicker endtimeDp;
		@FXML
		private Label wavefResultLabel;
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
			updateWavefTable(UTCTimeUtil.getUTCTimeLong(starttime),
					UTCTimeUtil.getUTCTimeLong(endtime));
		}
	}

	/** 初始化波形数据tab */
	private void initWavefdataTab() {

		// init tableview
		wavefId.setCellValueFactory(new PropertyValueFactory<WavefDataModel, Integer>(
				"id"));
		wavefType
				.setCellValueFactory(new PropertyValueFactory<WavefDataModel, String>(
						"type"));
		wavefTime
				.setCellValueFactory(new PropertyValueFactory<WavefDataModel, String>(
						"time"));
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

	/** 波形数据统计条形图的查询 */
	@FXML
	private void handleWavefQuery() {

		Toggle selectedRb = radioGroup.getSelectedToggle();

		// 该时间不是UTC时间
		LocalDate date = datePicker.getValue();
		if (date != null) {
			long time = date.toEpochDay() * 24 * 60 * 60 * 1000;
			updataWavefBar((short) selectedRb.getUserData(),
					UTCTimeUtil.getUTCTimeLong(time));
		}
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
			wavefXAxis.setLabel("小时");
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
			wavefXAxis.setLabel("天");
		}
		XYChart.Series<String, Integer> series = new XYChart.Series<>();
		wavefBar.getData().clear();
		for (int j = 0; j < result.length; j++) {
			result[j] = WavefDataDao.getCount(client.getStationId(),
					(starttime + j * interval),
					(starttime + (j + 1) * interval));
			series.getData().add(
					new XYChart.Data<>(wavefXStrings.get(j), result[j]));
		}
		wavefBar.getData().add(series);
	}

	/** 从数据库获取数据更新波形数据列表 */
	private void updateWavefTable(long starttime, long endtime) {

		wavefDataList.clear();
		List<WavefDataModel> list = WavefDataDao.getRecord(
				client.getStationId(), starttime, endtime);
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
	@FXML
	private BarChart<String, Integer> triBarChart;
	@FXML
	private CategoryAxis triBarXAxis;
	@FXML
	private DatePicker triBarDp;
	@FXML
	private AnchorPane lcParentPane;	//烈度图的父布局,为了在linechart的横坐标使用NumberAxis,在代码中动态添加LineChart
	@FXML
	private Label totalLabel;
	@FXML
	private Label indexLabel;
	
	private ObservableList<String> triXStrings = FXCollections
			.observableArrayList();

	private LineChart<Integer, Double> triLc;
	private NumberAxis xAxis;
	private NumberAxis yAxis;
	private int lineChartTotal = 300; //烈度图序号总数
	private List<TrgData> trgResult;	//缓存触发数据结果,用于分页显示
	private int pageIndex;	//折线图当前页的最大值

	/** 初始化触发数据tab */
	private void initTriTab() {

		yAxis = new NumberAxis();
		xAxis = new NumberAxis("序号", 0, lineChartTotal, 50);
		yAxis.setLabel("烈度值");
		
		//烈度折线图
		triLc = new LineChart(xAxis, yAxis);
		AnchorPane.setBottomAnchor(triLc, 10d);
		AnchorPane.setTopAnchor(triLc, 5d);
		AnchorPane.setLeftAnchor(triLc, 10d);
		AnchorPane.setRightAnchor(triLc, 10d);
		lcParentPane.getChildren().add(triLc);

		//
		triBarDp.setValue(LocalDate.now());
		triBarXAxis.setCategories(triXStrings);
		
		updateTriBar(UTCTimeUtil.getCurrUTCTime());
		updateTriLineChart(UTCTimeUtil.getCurrUTCTime());
	}

	/** 触发数据查询 */
	@FXML
	private void handleTriQuery() {
		LocalDate date = triBarDp.getValue();
		if (date != null) {
			long time = date.toEpochDay() * 24 * 60 * 60 * 1000;
			updateTriBar(UTCTimeUtil.getUTCTimeLong(time));
			updateTriLineChart(UTCTimeUtil.getUTCTimeLong(time));
		}
	}

	/**
	 * 更新波形数据统计条
	 *
	 * @param starttime
	 *            起始UTC时间(0点)
	 */
	private void updateTriBar(long starttime) {
		long interval = 0; // 间隔,单位毫秒
		int[] result = null;
		triXStrings.clear();

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(starttime);
		int year = cal.get(Calendar.YEAR);
		int mon = cal.get(Calendar.MONTH);
		int days = UTCTimeUtil.getDaysOfMonth(year, mon + 1); // Calendar月份是从0开始算的
		starttime = UTCTimeUtil.getFirstDayTime(starttime);
		triXStrings.addAll(Arrays.copyOf(dayStrs, days)); // 根据月份天数，截取与该月份天数同样长度的数组
		interval = 24 * 60 * 60 * 1000; // 时间间隔为1天
		result = new int[days];

		triBarChart.setTitle(String.valueOf(mon+1)+"月 触发数据统计");
		XYChart.Series<String, Integer> series = new XYChart.Series<>();
		triBarChart.getData().clear();
		for (int j = 0; j < result.length; j++) {
			result[j] = TrgDataDao.getCount(client.getStationId(),
					(starttime + j * interval),
					(starttime + (j + 1) * interval));
			series.getData().add(
					new XYChart.Data<>(triXStrings.get(j), result[j]));
		}
		triBarChart.getData().add(series);
	}

	/**
	 * 更新触发数据折线图
	 * @param time
	 */
	private void updateTriLineChart(long time) {
		
		
		trgResult = TrgDataDao.getRecord(client.getStationId(), time, time + 24
				* 60 * 60 * 1000);
		if(trgResult.size() == 0){
			return ;
		}
		StringBuilder dateBuilder = new StringBuilder();
		triLc.getData().clear();
		LineChart.Series<Integer, Double> series1 = new LineChart.Series<Integer, Double>();
		dateBuilder.append(UTCTimeUtil.timeFormat3(trgResult.get(0)
				.getStartTimeSec()));
		dateBuilder.append("-");
		int i = 0;
		for (; i < lineChartTotal && i < trgResult.size(); i++) {
			XYChart.Data<Integer, Double> data = new XYChart.Data<Integer, Double>(
					i, (double) trgResult.get(i).getIntensity() / 10);
			series1.getData().add(data);
		}
		pageIndex = i;
		totalLabel.setText(String.valueOf((trgResult.size()+lineChartTotal)/lineChartTotal));
		if(pageIndex % lineChartTotal > 0){
			indexLabel.setText(String.valueOf(String.valueOf(pageIndex/lineChartTotal + 1)));
		}else{
			indexLabel.setText(String.valueOf(String.valueOf(pageIndex/lineChartTotal)));
		}
		dateBuilder.append(UTCTimeUtil.timeFormat3(trgResult.get(i - 1)
				.getStartTimeSec()));
		dateBuilder.append("烈度图");
		triLc.setTitle(dateBuilder.toString());
		triLc.getData().add(series1);
	}
	/** 下一页 */
	@FXML
	private void handleTriNextPage() {
		if (trgResult != null && trgResult.size() - pageIndex > 0) {
			triLc.getData().clear();
			StringBuilder dateBuilder = new StringBuilder();
			LineChart.Series<Integer, Double> series1 = new LineChart.Series<Integer, Double>();
			dateBuilder.append(UTCTimeUtil.timeFormat3(trgResult.get(pageIndex)
					.getStartTimeSec()));
			dateBuilder.append("-");
			int i = pageIndex;
			
			for (int j = 0; j < lineChartTotal && i < trgResult.size(); i++,j++) {
				XYChart.Data<Integer, Double> data = new XYChart.Data<Integer, Double>(
						j, (double) trgResult.get(i).getIntensity() / 10);
				series1.getData().add(data);
			}
			pageIndex = i;
			if(pageIndex % lineChartTotal > 0){
				indexLabel.setText(String.valueOf(String.valueOf(pageIndex/lineChartTotal + 1)));
			}else{
				indexLabel.setText(String.valueOf(String.valueOf(pageIndex/lineChartTotal)));
			}
			dateBuilder.append(UTCTimeUtil.timeFormat3(trgResult.get(i - 1)
					.getStartTimeSec()));
			dateBuilder.append(" 烈度图");
			triLc.setTitle(dateBuilder.toString());
			triLc.getData().add(series1);
		}
	}

	/** 上一页 */
	@FXML
	private void handleTriPrevPage() {
		if (trgResult != null && pageIndex > lineChartTotal) {
			StringBuilder dateBuilder = new StringBuilder();
			LineChart.Series<Integer, Double> series1 = new LineChart.Series<Integer, Double>();
			int i = pageIndex  - lineChartTotal;
			if(pageIndex % lineChartTotal > 0){
				i -= pageIndex % lineChartTotal;
			}else{
				i -= lineChartTotal;
			}
			dateBuilder.append(UTCTimeUtil.timeFormat3(trgResult.get(i)
					.getStartTimeSec()));
			dateBuilder.append("-");
			for (int j = 0; j < lineChartTotal; i++,j++) {
				XYChart.Data<Integer, Double> data = new XYChart.Data<Integer, Double>(
						j, (double) trgResult.get(i).getIntensity() / 10);
				series1.getData().add(data);
			}
			pageIndex = i;
			if(pageIndex % lineChartTotal > 0){
				indexLabel.setText(String.valueOf(String.valueOf(pageIndex/lineChartTotal + 1)));
			}else{
				indexLabel.setText(String.valueOf(String.valueOf(pageIndex/lineChartTotal)));
			}
			dateBuilder.append(UTCTimeUtil.timeFormat3(trgResult.get(i - 1)
					.getStartTimeSec()));
			dateBuilder.append("烈度图");
			triLc.setTitle(dateBuilder.toString());
			triLc.getData().clear();
			triLc.getData().add(series1);
		}
	}
	
	/***************************************************************************************
	 * 
	 * 状态信息tab
	 * 
	 **************************************************************************************/
	@FXML
	private BarChart<String, Integer> statusBarChart;
	@FXML
	private CategoryAxis statusBarXAxis;
	@FXML
	private DatePicker statusBarDp;
	@FXML
	private AnchorPane statusLcParentPane1;
	@FXML
	private AnchorPane statusLcParentPane2;
	@FXML
	private AnchorPane statusLcParentPane3;
	@FXML
	private Label statusTotalLabel;
	@FXML
	private Label statusIndexLabel;
	@FXML
	private Label pvTitleLabel;
	
	private ObservableList<String> statusXStrings = FXCollections
			.observableArrayList();

	private LineChart<Integer, Integer> statusLc1;
	private LineChart<Integer, Integer> statusLc2;
	private LineChart<Integer, Integer> statusLc3;
	private int statuslineChartTotal = 100; //峰峰值折线图横坐标最多显示多少个statusdata数据(每个数据中有10个数据)
	private List<StatusData> statusResult;	//缓存触发数据结果,用于分页显示
	private int statusPageIndex;	//折线图当前页的最大值
	
	
	/** 初始化触发数据tab */
	private void initStatusTab() {

		NumberAxis statusyAxis1 = new NumberAxis();
		NumberAxis statusxAxis1 = new NumberAxis("序号", 0, statuslineChartTotal*10, 100);
		NumberAxis statusyAxis2 = new NumberAxis();
		NumberAxis statusxAxis2 = new NumberAxis("序号", 0, statuslineChartTotal*10, 100);
		NumberAxis statusyAxis3 = new NumberAxis();
		NumberAxis statusxAxis3 = new NumberAxis("序号", 0, statuslineChartTotal*10, 100);
		statusyAxis1.setLabel("峰峰值");
		statusyAxis2.setLabel("峰峰值");
		statusyAxis3.setLabel("峰峰值");
		
//		
//		//峰峰值
		statusLc1 = new LineChart(statusxAxis1, statusyAxis1);
		AnchorPane.setBottomAnchor(statusLc1, 10d);
		AnchorPane.setTopAnchor(statusLc1, 5d);
		AnchorPane.setLeftAnchor(statusLc1, 10d);
		AnchorPane.setRightAnchor(statusLc1, 10d);
		statusLcParentPane1.getChildren().add(statusLc1);
		statusLc2 = new LineChart(statusxAxis2, statusyAxis2);
		AnchorPane.setBottomAnchor(statusLc2, 10d);
		AnchorPane.setTopAnchor(statusLc2, 5d);
		AnchorPane.setLeftAnchor(statusLc2, 10d);
		AnchorPane.setRightAnchor(statusLc2, 10d);
		statusLcParentPane2.getChildren().add(statusLc2);
		statusLc3 = new LineChart(statusxAxis3, statusyAxis3);
		AnchorPane.setBottomAnchor(statusLc3, 10d);
		AnchorPane.setTopAnchor(statusLc3, 5d);
		AnchorPane.setLeftAnchor(statusLc3, 10d);
		AnchorPane.setRightAnchor(statusLc3, 10d);
		statusLcParentPane3.getChildren().add(statusLc3);
		statusLc1.setTitle("EW峰峰值");
		statusLc2.setTitle("NS峰峰值");
		statusLc3.setTitle("UD峰峰值");
//
		statusBarDp.setValue(LocalDate.now());
		statusBarXAxis.setCategories(statusXStrings);
//		
		updateStatusBar(UTCTimeUtil.getCurrUTCTime());
		updateStatusLineChart(UTCTimeUtil.getCurrUTCTime());
	}

	/** 触发数据查询 */
	@FXML
	private void handleStatusQuery() {
		LocalDate date = statusBarDp.getValue();
		if (date != null) {
			long time = date.toEpochDay() * 24 * 60 * 60 * 1000;
			updateStatusBar(UTCTimeUtil.getUTCTimeLong(time));
			updateStatusLineChart(UTCTimeUtil.getUTCTimeLong(time));
		}
	}

	/**
	 * 更新波形数据统计条
	 *
	 * @param starttime
	 *            起始UTC时间(0点)
	 */
	private void updateStatusBar(long starttime) {
		long interval = 0; // 间隔,单位毫秒
		int[] result = null;
		statusXStrings.clear();
//
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(starttime);
		int year = cal.get(Calendar.YEAR);
		int mon = cal.get(Calendar.MONTH);
		int days = UTCTimeUtil.getDaysOfMonth(year, mon + 1); // Calendar月份是从0开始算的
		starttime = UTCTimeUtil.getFirstDayTime(starttime);
		statusXStrings.addAll(Arrays.copyOf(dayStrs, days)); // 根据月份天数，截取与该月份天数同样长度的数组
		interval = 24 * 60 * 60 * 1000; // 时间间隔为1天
		result = new int[days];
//
		statusBarChart.setTitle(String.valueOf(mon+1)+"月 状态数据统计");
		XYChart.Series<String, Integer> series = new XYChart.Series<>();
		statusBarChart.getData().clear();
		for (int j = 0; j < result.length; j++) {
			result[j] = StatusDataDao.getCount(client.getStationId(),
					(starttime + j * interval),
					(starttime + (j + 1) * interval));
			series.getData().add(
					new XYChart.Data<>(statusXStrings.get(j), result[j]));
		}
		statusBarChart.getData().add(series);
	}

	/**
	 * 更新触发数据折线图
	 * @param time
	 */
	private void updateStatusLineChart(long time) {
		
		statusResult = StatusDataDao.getRecord(client.getStationId(), time, time + 24
				* 60 * 60 * 1000);
		if(statusResult.size() == 0){
			return ;
		}
		StringBuilder dateBuilder = new StringBuilder();
		statusLc1.getData().clear();
		statusLc2.getData().clear();
		statusLc3.getData().clear();
		LineChart.Series<Integer, Integer> series1 = new LineChart.Series<Integer, Integer>();
		LineChart.Series<Integer, Integer> series2 = new LineChart.Series<Integer, Integer>();
		LineChart.Series<Integer, Integer> series3 = new LineChart.Series<Integer, Integer>();
		dateBuilder.append(UTCTimeUtil.timeFormat3(statusResult.get(0).getStartTime()));
		dateBuilder.append("-");
		int i = 0;
		for (; i < statuslineChartTotal && i < statusResult.size(); i++) {
			for(int j = 0; j < 10; j++){
				XYChart.Data<Integer, Integer> data1 = new XYChart.Data<Integer, Integer>(
						i*10+j, statusResult.get(i).getUdPeakValue()[j]);
				XYChart.Data<Integer, Integer> data2 = new XYChart.Data<Integer, Integer>(
						i*10+j, statusResult.get(i).getEwPeakValue()[j]);
				XYChart.Data<Integer, Integer> data3 = new XYChart.Data<Integer, Integer>(
						i*10+j, statusResult.get(i).getNsPeakValue()[j]);
				series1.getData().add(data1);
				series2.getData().add(data2);
				series3.getData().add(data3);
			}
		}
		statusPageIndex = i;
		statusTotalLabel.setText(String.valueOf((statusResult.size()+statuslineChartTotal)/statuslineChartTotal));
		if(statusPageIndex % statuslineChartTotal > 0){
			statusIndexLabel.setText(String.valueOf(String.valueOf(statusPageIndex/statuslineChartTotal + 1)));
		}else{
			statusIndexLabel.setText(String.valueOf(String.valueOf(statusPageIndex/statuslineChartTotal)));
		}
		dateBuilder.append(UTCTimeUtil.timeFormat3(statusResult.get(i - 1).getStartTime()));
		dateBuilder.append("  峰峰值");
		pvTitleLabel.setText(dateBuilder.toString());
		statusLc1.getData().add(series1);
		statusLc2.getData().add(series2);
		statusLc3.getData().add(series3);
	}
	/** 下一页 */
	@FXML
	private void handleStatusNextPage() {
		if (statusResult != null && statusResult.size() - statusPageIndex > 0) {
			statusLc1.getData().clear();
			statusLc2.getData().clear();
			statusLc3.getData().clear();
			StringBuilder dateBuilder = new StringBuilder();
			LineChart.Series<Integer, Integer> series1 = new LineChart.Series<Integer, Integer>();
			LineChart.Series<Integer, Integer> series2 = new LineChart.Series<Integer, Integer>();
			LineChart.Series<Integer, Integer> series3 = new LineChart.Series<Integer, Integer>();
			dateBuilder.append(UTCTimeUtil.timeFormat3(statusResult.get(statusPageIndex).getStartTime()));
			dateBuilder.append("-");
			int i = statusPageIndex;
			for (int j = 0; j < statuslineChartTotal && i < statusResult.size(); i++,j++) {
				for(int k = 0; k < 10; k++){
					XYChart.Data<Integer, Integer> data1 = new XYChart.Data<Integer, Integer>(
							j*10+k, statusResult.get(i).getUdPeakValue()[k]);
					XYChart.Data<Integer, Integer> data2 = new XYChart.Data<Integer, Integer>(
							j*10+k, statusResult.get(i).getEwPeakValue()[k]);
					XYChart.Data<Integer, Integer> data3 = new XYChart.Data<Integer, Integer>(
							j*10+k, statusResult.get(i).getNsPeakValue()[k]);
					series1.getData().add(data1);
					series2.getData().add(data2);
					series3.getData().add(data3);
				}
			}
			statusPageIndex = i;
			if(statusPageIndex % statuslineChartTotal > 0){
				statusIndexLabel.setText(String.valueOf(String.valueOf(statusPageIndex/statuslineChartTotal + 1)));
			}else{
				statusIndexLabel.setText(String.valueOf(String.valueOf(statusPageIndex/statuslineChartTotal)));
			}
			dateBuilder.append(UTCTimeUtil.timeFormat3(statusResult.get(i - 1).getStartTime()));
			dateBuilder.append("  峰峰值");
			pvTitleLabel.setText(dateBuilder.toString());
			statusLc1.getData().add(series1);
			statusLc2.getData().add(series2);
			statusLc3.getData().add(series3);
		}
	}

	/** 上一页 */
	@FXML
	private void handleStatusPrevPage() {
		if (statusResult != null && statusPageIndex > statuslineChartTotal) {
			StringBuilder dateBuilder = new StringBuilder();
			statusLc1.getData().clear();
			statusLc2.getData().clear();
			statusLc3.getData().clear();
			LineChart.Series<Integer, Integer> series1 = new LineChart.Series<Integer, Integer>();
			LineChart.Series<Integer, Integer> series2 = new LineChart.Series<Integer, Integer>();
			LineChart.Series<Integer, Integer> series3 = new LineChart.Series<Integer, Integer>();
			
			int i = statusPageIndex  - statuslineChartTotal;
			if(statusPageIndex % statuslineChartTotal > 0){
				i -= statusPageIndex % statuslineChartTotal;
			}else{
				i -= statuslineChartTotal;
			}
			dateBuilder.append(UTCTimeUtil.timeFormat3(statusResult.get(i).getStartTime()));
			dateBuilder.append("-");
			for (int j = 0; j < statuslineChartTotal; i++,j++) {
				for(int k = 0; k < 10; k++){
					XYChart.Data<Integer, Integer> data1 = new XYChart.Data<Integer, Integer>(
							j*10+k, statusResult.get(i).getUdPeakValue()[k]);
					XYChart.Data<Integer, Integer> data2 = new XYChart.Data<Integer, Integer>(
							j*10+k, statusResult.get(i).getEwPeakValue()[k]);
					XYChart.Data<Integer, Integer> data3 = new XYChart.Data<Integer, Integer>(
							j*10+k, statusResult.get(i).getNsPeakValue()[k]);
					series1.getData().add(data1);
					series2.getData().add(data2);
					series3.getData().add(data3);
				}
			}
			statusPageIndex = i;
			if(statusPageIndex % statuslineChartTotal > 0){
				statusIndexLabel.setText(String.valueOf(String.valueOf(statusPageIndex/statuslineChartTotal + 1)));
			}else{
				statusIndexLabel.setText(String.valueOf(String.valueOf(statusPageIndex/statuslineChartTotal)));
			}
			dateBuilder.append(UTCTimeUtil.timeFormat3(statusResult.get(i - 1).getStartTime()));
			dateBuilder.append("  峰峰值");
			pvTitleLabel.setText(dateBuilder.toString());
			statusLc1.getData().add(series1);
			statusLc2.getData().add(series2);
			statusLc3.getData().add(series3);
		}
	}
}
