package com.eqsys.application;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.eqsys.handler.CmdRespHandler;
import com.eqsys.handler.DataRecvHandler;
import com.eqsys.handler.RegRespHandler;
import com.eqsys.util.JDBCHelper;
import com.eqsys.util.LogUtil;
import com.eqsys.util.SysConfig;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class EqServer extends Application {
	
	private Logger log = Logger.getLogger(EqServer.class);

	// 布局
	private Stage mPrimaryStage;
	private String loginPath = "/com/eqsys/view/Loginlayout.fxml";
	private String maxIconPath = "/com/eqsys/view/images/icon_max.png";
	private String mainPanePath = "/com/eqsys/view/MainPaneLayout.fxml";
	
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
		JDBCHelper.closeDB();
	}

	/** 程序入口 */
	public static void main(String[] args) {
		launch(args);
	}

	/** 全局初始化 */
	private void globalInit() {
		LogUtil.initLog();
		SysConfig.preConfig();  //配置文件
		initView();
		//初始化数据库连接池
		JDBCHelper.initDB();

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
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(loginPath));
		Node page = null;
		try {
			page = loader.load();
			LoginLayoutController controller = loader.getController();
			controller.setMainApp(EqServer.this);
		} catch (IOException e) {
			log.error("登录页面加载失败:"+e.getMessage());
			return;
		}
		Scene scene = new Scene((Parent) page);
		mPrimaryStage.setScene(scene);
	}
	/** 显示主工作窗口 */
	public void loadMainPage(){
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(mainPanePath));
		Node page = null;
		try {
			page = loader.load();
		} catch (IOException e) {
			log.error("主页面加载失败:"+e.getMessage());
			return;
		}
		Scene scene = new Scene((Parent) page, screenWidth*0.8, screenHeight*0.8);
		mPrimaryStage.setMinHeight(screenHeight*0.8);
		mPrimaryStage.setMinWidth(screenWidth*0.8);
		mPrimaryStage.setScene(scene);
		mPrimaryStage.centerOnScreen();
		initNetty();
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

		ChannelFuture future = bootstrap.bind(SysConfig.getServerIp(), SysConfig.getServerPort());
		future.addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				
				log.info("服务器开始监听");
			}
		});
	}

	/** pipeline handler 队列  */
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {

			ChannelPipeline pipeline = ch.pipeline();
			pipeline.addLast(new ObjectDecoder(1024,
					ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
			pipeline.addLast(new ObjectEncoder());
			pipeline.addLast(new ReadTimeoutHandler(120, TimeUnit.SECONDS));
			pipeline.addLast(new RegRespHandler());
			pipeline.addLast(new CmdRespHandler());
			pipeline.addLast(new DataRecvHandler());
		}
	}

}
