package com.eqsys.application;

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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.apache.log4j.Logger;

import com.eqsys.dao.ClientInfoDao;
import com.eqsys.handler.CmdRespHandler;
import com.eqsys.handler.DataRecvHandler;
import com.eqsys.handler.HeartbeatRespHandler;
import com.eqsys.handler.RegRespHandler;
import com.eqsys.model.RecvInfo;
import com.eqsys.msg.RegMsg;
import com.eqsys.util.JDBCHelper;
import com.eqsys.util.LogUtil;
import com.eqsys.util.SysConfig;
import com.eqsys.view.LoginLayoutController;
import com.eqsys.view.RootLayoutController;

public class EqServer extends Application {
	
	private Logger log = Logger.getLogger(EqServer.class);

	// 标题栏移动坐标
	private double xOffset = 0;
	private double yOffset = 0;

	// 布局
	private BorderPane rootPane; // 主布局
	private Node headToolbar; // 标题栏
	private Stage mPrimaryStage;
	private String rootPath = "/com/eqsys/view/Rootlayout.fxml";
	private String loginPath = "/com/eqsys/view/Loginlayout.fxml";
	private String maxIconPath = "/com/eqsys/view/images/icon_max.png";
	private String mainPanePath = "/com/eqsys/view/MainLayout.fxml";

	// 参数
	private int port = 8080;
	private String host = "localhost";
	
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

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * 全局初始化
	 */
	private void globalInit() {
		LogUtil.initLog();
		SysConfig.preConfig();
		initView();
		//初始化数据库连接池
		JDBCHelper.initDB();
		//testJDBC();

	}

	/**
	 * 初始化布局
	 */
	private void initView() {
		// 应用宽高
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int serverWidth = (int) (screenSize.width * 0.8);
		int serverHeight = (int) (screenSize.height * 0.8);

		// 操作系统标题栏图标
		mPrimaryStage.getIcons().add(new Image(maxIconPath));
		// 初始化主布局
		mPrimaryStage.initStyle(StageStyle.UNDECORATED);
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(rootPath));
		try {
			rootPane = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		RootLayoutController controller = loader.getController();
		controller.setControllerStage(mPrimaryStage);
		headToolbar = rootPane.getTop();

		// 自定义的标题栏，移动功能
		headToolbar.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = event.getSceneX();
				yOffset = event.getSceneY();
			}
		});
		// 拖动窗体
		headToolbar.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!controller.isMaximized()) {
					mPrimaryStage.setX(event.getScreenX() - xOffset);
					mPrimaryStage.setY(event.getScreenY() - yOffset);
				}
			}
		});
		// 双击最大化/恢复
		headToolbar.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2) {
					controller.toogleMaximized();
				}

			}
		});

		Scene scene = new Scene(rootPane, serverWidth, serverHeight);
		mPrimaryStage.setScene(scene);
		//gotoLoginPage();
		gotoMainPage();
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

	/**
	 * pipeline handler 队列
	 *
	 */
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {

			ChannelPipeline pipeline = ch.pipeline();
			pipeline.addLast(new ObjectDecoder(1024,
					ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
			pipeline.addLast(new ObjectEncoder());
//			pipeline.addLast(MarshallingFactory.buildMarshallingDecoder());
//			pipeline.addLast(MarshallingFactory.buildMarshallingEncoder());
			pipeline.addLast(new ReadTimeoutHandler(120, TimeUnit.SECONDS));
			pipeline.addLast(new RegRespHandler());
//			pipeline.addLast(new HeartbeatRespHandler());
			pipeline.addLast(new CmdRespHandler());
			pipeline.addLast(new DataRecvHandler());
		}

	}

	/**
	 * 登录页面
	 */
	private void gotoLoginPage() {

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(loginPath));
		try {
			Node page = loader.load();
			rootPane.setCenter(page);
			LoginLayoutController controller = loader.getController();
			controller.setMainApp(EqServer.this);
		} catch (IOException e) {
			
			log.error("登录页面加载失败:"+e.getMessage());
		}
	}

	/**
	 * 主页面
	 */
	public void gotoMainPage() {

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(mainPanePath));
		try {
			Node page = loader.load();
			//设置填充父布局
			AnchorPane.setTopAnchor(page, Double.valueOf(1));
			AnchorPane.setBottomAnchor(page, Double.valueOf(1));
			AnchorPane.setLeftAnchor(page, Double.valueOf(1));
			AnchorPane.setRightAnchor(page, Double.valueOf(1));
			rootPane.setCenter(page);
		} catch (IOException e) {
			
			log.error("主页面加载失败:"+e.getMessage());
		}
		
		initNetty();
	}
	
	/**
	 * 测试jdbc
	 */
	private void testJDBC(){
		new ClientInfoDao().add(new RegMsg());
	}

}
