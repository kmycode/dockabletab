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

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.stage.Window;

/**
 *
 * @author KMY
 */
public class DockingBorder extends Popup {

	private Window parent;
	private Pane rootPane;

	public DockingBorder (Window parent) {
		super();
		this.parent = parent;

		this.rootPane = new AnchorPane();
		this.rootPane.setOpacity(0.6);
		this.rootPane.setStyle("-fx-border-width: 8; -fx-border-color: green; -fx-background-color: #ccffcc;");
		getContent().add(this.rootPane);
	}

	public void setLocalPosition (double x, double y) {
		this.setX(parent.getX() + x);
		this.setY(parent.getY() + y);
	}

	public void setPosition (double x, double y) {
		this.setX(x);
		this.setY(y);
	}

	public void setSize (double w, double h) {
		this.rootPane.setPrefSize(w, h);
	}

}
