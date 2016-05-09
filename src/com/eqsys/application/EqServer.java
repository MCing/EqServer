package com.eqsys.application;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.eqsys.handler.CmdRespHandler;
import com.eqsys.handler.DataRecvHandler;
import com.eqsys.handler.RegRespHandler;
import com.eqsys.util.JDBCHelper;
import com.eqsys.util.LogUtil;
import com.eqsys.util.ParseUtil;
import com.eqsys.util.SysConfig;
import com.eqsys.util.UTCTimeUtil;
import com.eqsys.view.FXMLController;
import com.eqsys.view.LoginLayoutController;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class EqServer extends Application {

	private Logger log = Logger.getLogger(EqServer.class);

	// 布局
	private Stage mPrimaryStage;
	private String loginPath = "/com/eqsys/view/LoginLayout.fxml";
	private String maxIconPath = "/com/eqsys/view/images/icon_max.png";
	private String mainPanePath = "/com/eqsys/view/MainPaneLayout.fxml";
	
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
	public static StringProperty utcTime = new SimpleStringProperty();

	@Override
	public void start(Stage primaryStage) {
		try {
			mPrimaryStage = primaryStage;
			globalInit();
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() throws Exception {

		log.info("服务器退出");
		stopUTCTime();
		executor.shutdown();
		JDBCHelper.closeDB();
	}

	/** 程序入口 */
	public static void main(String[] args) {
		launch(args);
	}

	/** 全局初始化 */
	private void globalInit() {
		LogUtil.initLog();
		SysConfig.preConfig(); // 配置文件
		JDBCHelper.initDB();
		initView();
		startUTCTime();
	}

	private int screenWidth;
	private int screenHeight;

	/** 初始化根窗口布局 */
	private void initView() {
		// 应用宽高
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenWidth = screenSize.width;
		screenHeight = screenSize.height;

		// 操作系统标题栏图标
		mPrimaryStage.getIcons().add(new Image(maxIconPath));
		mPrimaryStage.setTitle("地震数据监控系统");

		loadLoginPage();

	}

	/** 显示登录窗口 */
	private void loadLoginPage() {

		LoginLayoutController controller = (LoginLayoutController) loadFxml(loginPath);
		controller.initController(EqServer.this);
	}

	/** 显示主工作窗口 */
	public void loadMainPage() {
		
		mPrimaryStage.close();   //关闭登录窗口
		loadFxml(mainPanePath);
		mPrimaryStage.setMinHeight(screenHeight * 0.8);
		mPrimaryStage.setMinWidth(screenWidth * 0.8);
		mPrimaryStage.centerOnScreen();
		mPrimaryStage.show();
		initNetty();
	}
	
	/**
	 * 加载fxml
	 * @param path	fxml全路径
	 * @return		fxml的controller
	 */
	public FXMLController loadFxml(String path){
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(ParseUtil.getFXMLURL(path));
		Parent page = null;
		try {
			page = loader.load();
			mPrimaryStage.setScene(new Scene(page));
			return loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
			log.error(path+"加载失败:" + e.getMessage());
			return null;
		}
		
	}

	/**
	 * 初始化通信
	 */
	private void initNetty() {
		// 初始化服务器
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChildChannelHandler())
				.option(ChannelOption.SO_BACKLOG, 1000) // TCP请求队列最大长度
				.childOption(ChannelOption.SO_KEEPALIVE, true); // 长连接

		ChannelFuture future = bootstrap.bind(SysConfig.getServerIp(),
				SysConfig.getServerPort());
		future.addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {

				log.info("服务器开始监听");
			}
		});
	}

	/** pipeline handler 队列 */
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {

			ChannelPipeline pipeline = ch.pipeline();
			pipeline.addLast(new ObjectDecoder(1024, ClassResolvers
					.weakCachingConcurrentResolver(this.getClass()
							.getClassLoader())));
			pipeline.addLast(new ObjectEncoder());
			pipeline.addLast(new ReadTimeoutHandler(120, TimeUnit.SECONDS));
			pipeline.addLast(new RegRespHandler());
			pipeline.addLast(new CmdRespHandler());
			pipeline.addLast(new DataRecvHandler());
		}
	}
	
	private ScheduledFuture timerFuture;
	/** 启动定时更新UTC时间 任务 */
	private void startUTCTime(){
		timerFuture = executor.scheduleAtFixedRate(new UTCTimerTask(), 1000, 1000, TimeUnit.MILLISECONDS);
	}
	/** 关闭更新UTC时间 任务 */
	private void stopUTCTime(){
		if(timerFuture != null){
			timerFuture.cancel(true);
		}
	}
	private class UTCTimerTask implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					utcTime.set(UTCTimeUtil.timeFormat2(UTCTimeUtil.getCurrUTCTime()));
				}
			});
		}
		
	}

}
