package com.eqsys.view;

import java.awt.Dimension;
import java.awt.Toolkit;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * 主布局控制器
 * @date 2016-2-9
 */
public class RootLayoutController {
	
	private Stage stage;
	private Rectangle2D backupWindowBounds = null;
    private boolean maximized = false;

	@FXML
	private Button minBtn;
	@FXML
	private Button maxBtn;
	@FXML
	private Button closeBtn;
	
	@FXML
    private void initialize() {
    }
	
	/** 最小化按键处理 */
	@FXML
	private void handleMinimized(){
		stage.setIconified(true);
	}
	
	/** 关闭窗口按键处理 */
	@FXML
	private void handleCloseWindow(){
		//Platform.exit();   //close the hole program
		stage.close();   //only close the stage
	}
	
	/** 最大化窗口按键处理 */
	@FXML
	private void handleMaximized(){
		toogleMaximized();
	}
	
	/** 外部调用，传入 stage对象 */
	public void setControllerStage(Stage stage){
		this.stage = stage;
	}
	
	/** 最大化/恢复窗体大小 */
	public void toogleMaximized() {
		//bug 窗体超过屏幕左边边界，Screen.getScreensForRectangle(stage.getX(), stage.getY(), 1, 1)列表为空
        //直接用屏幕分辨作为最大化窗口尺寸
		//final Screen screen = Screen.getScreensForRectangle(stage.getX(), stage.getY(), 1, 1).get(0);
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        if (maximized) {
            maximized = false;
            if (backupWindowBounds != null) {
                stage.setX(backupWindowBounds.getMinX());
                stage.setY(backupWindowBounds.getMinY());
                stage.setWidth(backupWindowBounds.getWidth());
                stage.setHeight(backupWindowBounds.getHeight());
            }
        } else {
            maximized = true;
            backupWindowBounds = new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
            stage.setX(0);
            stage.setY(0);
            stage.setWidth(screensize.getWidth());
            stage.setHeight(screensize.getHeight());
//            stage.setX(screen.getVisualBounds().getMinX());
//            stage.setY(screen.getVisualBounds().getMinY());
//            stage.setWidth(screen.getVisualBounds().getWidth());
//            stage.setHeight(screen.getVisualBounds().getHeight());
        }
    }
	
	/**
	 * 返回窗体是否为最大化状态
	 * @return value of maximized
	 */
	public boolean isMaximized(){
		return maximized;
	}
}
