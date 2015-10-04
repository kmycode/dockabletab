/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dockabletab;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author KMY
 */
public class DockableTab_Main extends Application {

	@Override
	public void start (Stage primaryStage) {

		DockablePane root = new DockablePane(primaryStage);
		DockableTabPane tabPane = new DockableTabPane(root.rootGroupProperty().get());
		tabPane.insertTab(new DockableTab("aaa"));
		tabPane.insertTab(new DockableTab("bbb"));
		tabPane.insertTab(new DockableTab("ccc"));
		tabPane.insertTab(new DockableTab("ddd"));
		tabPane.insertTab(new DockableTab("eee"));
		tabPane.insertTab(new DockableTab("fff"));
		tabPane.insertTab(new DockableTab("ggg"));
		tabPane.insertTab(new DockableTab("hhh"));
		tabPane.insertTab(new DockableTab("iii"));
		tabPane.insertTab(new DockableTab("jjj"));
		tabPane.insertTab(new DockableTab("kkk"));
		root.rootGroupProperty().get().add(tabPane);

		Scene scene = new Scene(root, 500, 500);

		primaryStage.setTitle("Hello World!");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main (String[] args) {
		launch(args);
	}

}
