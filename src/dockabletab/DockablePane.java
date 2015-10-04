/*
 * jStorybook: すべての小説家・作者のためのオープンソース・ソフトウェア
 * Copyright (C) 2008 - 2012 Martin Mustun
 *   (このソースの製作者) KMY
 *
 * このプログラムはフリーソフトです。
 * あなたは、自由に修正し、再配布することが出来ます。
 * あなたの権利は、the Free Software Foundationによって発表されたGPL ver.2以降によって保護されています。
 *
 * このプログラムは、小説・ストーリーの制作がよりよくなるようにという願いを込めて再配布されています。
 * あなたがこのプログラムを再配布するときは、GPLライセンスに同意しなければいけません。
 *  <http://www.gnu.org/licenses/>.
 */
package dockabletab;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * ドッキング可能なエリア
 *
 * @author KMY
 */
public class DockablePane extends AnchorPane {

	private ObjectProperty<DockableAreaGroupPane> rootGroup = new SimpleObjectProperty<>();
	private Window parent;
	private Stage window;
	private DockablePane mainPane;
	private List<DockablePane> childPane = new LinkedList<>();

	public DockablePane (Window parent) {
		this.parent = parent;
		this.rootGroup.addListener((obj) -> {
			DockablePane.this.getChildren().clear();
			DockablePane.this.getChildren().add(this.rootGroup.get());
		});
		this.rootGroup.set(new DockableAreaGroupPane(this));
		AnchorPane.setTopAnchor(rootGroup.get(), 0.0);
		AnchorPane.setLeftAnchor(rootGroup.get(), 0.0);
		AnchorPane.setRightAnchor(rootGroup.get(), 0.0);
		AnchorPane.setBottomAnchor(rootGroup.get(), 0.0);

		this.mainPane = this;
	}

	DockablePane (DockablePane mainPane, DockableAreaGroupPane groupPane, double x, double y, double w, double h) {
		Stage floatingStage = new Stage();
		Scene floatingScene = new Scene(this, w, h);
		floatingStage.setScene(floatingScene);
		floatingStage.setX(x);
		floatingStage.setY(y);
		floatingStage.initOwner(mainPane.getWindow());
		floatingStage.show();
		this.rootGroup.set(groupPane);
		AnchorPane.setTopAnchor(rootGroup.get(), 0.0);
		AnchorPane.setLeftAnchor(rootGroup.get(), 0.0);
		AnchorPane.setRightAnchor(rootGroup.get(), 0.0);
		AnchorPane.setBottomAnchor(rootGroup.get(), 0.0);

		this.parent = mainPane.parent;
		this.mainPane = mainPane;
		this.mainPane.childPane.add(this);
		this.window = floatingStage;
	}

	public ReadOnlyObjectProperty<DockableAreaGroupPane> rootGroupProperty () {
		return this.rootGroup;
	}

	DockableAreaGroupPane wrapRootGroup (Orientation orientation) {
		DockableAreaGroupPane newRootGroup = new DockableAreaGroupPane(this, orientation);
		this.rootGroup.get().setParentPane(newRootGroup);
		this.rootGroup.set(newRootGroup);
		AnchorPane.setTopAnchor(rootGroup.get(), 0.0);
		AnchorPane.setLeftAnchor(rootGroup.get(), 0.0);
		AnchorPane.setRightAnchor(rootGroup.get(), 0.0);
		AnchorPane.setBottomAnchor(rootGroup.get(), 0.0);
		return newRootGroup;
	}

	public Window getWindow () {
		return this.parent;
	}

	public DockablePane getMainPane () {
		return this.mainPane;
	}

	public List<DockablePane> getChildPane () {
		return this.childPane;
	}

	public List<DockableTabPane> getTabPaneList () {
		List<DockableTabPane> result = new ArrayList<>();
		for (DockablePane pane : this.mainPane.childPane) {
			for (Node node : pane.getChildren()) {
				if (node instanceof DockableAreaGroupPane) {
					((DockableAreaGroupPane) node).getTabPaneList(result);
				}
			}
		}
		return result;
	}

	public List<DockableTab> getTabList () {
		List<DockableTabPane> tabPaneList = this.getTabPaneList();
		ArrayList<DockableTab> result = new ArrayList<>();
		for (DockableTabPane tabPane : tabPaneList) {
			for (Tab tab : tabPane.getTabs()) {
				result.add((DockableTab) tab);
			}
		}
		return result;
	}

	public boolean isEmpty () {
		int count = 0;
		for (Node node : this.getChildren()) {
			if (node instanceof DockableAreaGroupPane) {
				for (Node tabPaneNode : ((DockableAreaGroupPane) node).getTabPaneItems()) {
					if (tabPaneNode instanceof DockableTabPane) {
						count += ((DockableTabPane) tabPaneNode).getTabs().size();
					}
				}
			}
		}
		return count == 0;
	}

	public void close () {
		if (this.window != null) {
			this.window.close();
		}
	}
}
