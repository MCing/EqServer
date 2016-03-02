package com.eqsys.view;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;

/**
 * 主工作窗口
 *
 */
public class MainPaneController {

	
	@FXML
	private TreeView treeView;
	@FXML
	private AnchorPane workPane;
	@FXML
	private AnchorPane clientPane;
	@FXML
	private AnchorPane revcInfoPane;
	
	
	private String clientListPath = "ClientListLayout.fxml";
	private String recvInfoPath = "RecvLayout.fxml";
	
	@FXML
	private void initialize() {
		
		//添加各个分区到主窗口
		 createTree();
		 setWorkPane();
		 setClientPane();
		 setRevInfoPane();
	}

	/** 树形菜单 */
	private void createTree() {

		//create root
	    TreeItem<String> root = new TreeItem<>("Root");
	    //root.setExpanded(true);
	    //create child
	    TreeItem<String> itemChild = new TreeItem<>("Child");
	    itemChild.setExpanded(false);
	    //root is the parent of itemChild
	    root.getChildren().add(itemChild);
	    treeView.setRoot(root);
	}
	
	/** 主工作区 */
	private void setWorkPane(){
	}
	
	/** 节点管理区 */
	private void setClientPane(){
		
		Node node = getPane(clientListPath);
		if(node != null){
			
			AnchorPane.setTopAnchor(node, Double.valueOf(0));
			AnchorPane.setBottomAnchor(node, Double.valueOf(0));
			AnchorPane.setLeftAnchor(node, Double.valueOf(0));
			AnchorPane.setRightAnchor(node, Double.valueOf(0));
			clientPane.getChildren().add(node);
		}else{
			
			System.out.println("setClientPane error");
		}
	}
	
	/** 接收显示区 */
	private void setRevInfoPane(){
		
		Node node = getPane(recvInfoPath);
		if(node != null){
			AnchorPane.setTopAnchor(node, Double.valueOf(0));
			AnchorPane.setBottomAnchor(node, Double.valueOf(0));
			AnchorPane.setLeftAnchor(node, Double.valueOf(0));
			AnchorPane.setRightAnchor(node, Double.valueOf(0));
			revcInfoPane.getChildren().add(node);
		}else{
			System.out.println("setRevInfoPane error");
		}
	}
	
	/** 获取fxml 节点 */
	private Node getPane(String path){
		
		Node node = null;
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(path));
		try {
			node = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return node;
	}
}
