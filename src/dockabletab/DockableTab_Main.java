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
		DockableTab tab = new DockableTab("aaa");
		DockableTab tab2 = new DockableTab("bbb");
		DockableTabPane tabPane = new DockableTabPane(root.rootGroupProperty().get());
		tabPane.insertTab(tab);
		tabPane.insertTab(tab2);
		root.rootGroupProperty().get().add(tabPane);

		Scene scene = new Scene(root, 300, 250);

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
