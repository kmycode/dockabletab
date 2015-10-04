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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;

/**
 * ドッキング可能なエリア
 *
 * @author KMY
 */
public class DockablePane extends AnchorPane {

	private ObjectProperty<DockableAreaGroupPane> rootGroup;
	private Window parent;

	public DockablePane (Window parent) {
		this.parent = parent;
		this.rootGroup = new SimpleObjectProperty<>();
		this.rootGroup.addListener((obj) -> {
			DockablePane.this.getChildren().clear();
			DockablePane.this.getChildren().add(this.rootGroup.get());
		});
		this.rootGroup.set(new DockableAreaGroupPane(this));
		AnchorPane.setTopAnchor(rootGroup.get(), 0.0);
		AnchorPane.setLeftAnchor(rootGroup.get(), 0.0);
		AnchorPane.setRightAnchor(rootGroup.get(), 0.0);
		AnchorPane.setBottomAnchor(rootGroup.get(), 0.0);
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

}
