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

import com.sun.javafx.scene.control.skin.LabeledText;
import java.awt.Rectangle;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;

/**
 * ドッキング可能なタブパネル
 * このインスタンス１つが、DockableAreaGroupPaneのアイテム１つに該当する
 *
 * @author KMY
 */
public class DockableTabPane extends TabPane {

	private DockableAreaGroupPane parent;
	private boolean mouseDragging;
	private double mousePressX;
	private double mousePressY;

	public DockableTabPane (DockableAreaGroupPane parent) {
		this.parent = parent;
		this.setOnMousePressed((obj) -> {
			DockableTabPane.this.mousePressed(obj);
		});
		this.setOnMouseReleased((obj) -> {
			DockableTabPane.this.mouseReleased(obj);
		});
		this.setOnMouseDragged((obj) -> {
			DockableTabPane.this.mouseDragged(obj);
		});
	}

	void setParent (DockableAreaGroupPane parent) {
		this.parent = parent;
	}

	DockableAreaGroupPane getParentPane () {
		return this.parent;
	}

	public Rectangle getLocalRectangle () {
		Point2D pos = this.parent.getPosition();
		double x = pos.getX();
		double y = pos.getY();
		outside:
		for (Node node : this.parent.getRootPane().getItems()) {
			if (node != this) {
				y = 0;

				// VerticalなSplitPane
				if (node instanceof DockableAreaGroupPane) {
					DockableAreaGroupPane pane = (DockableAreaGroupPane) node;
					for (DockableTabPane tabPane : pane.getTabPaneList()) {
						if (tabPane != this) {
							y += tabPane.getHeight();
						}
						else {
							break outside;
						}
					}
					x += pane.getWidth();
				}
			}
			else {
				break;
			}
		}
		double w = this.getWidth();
		double h = this.getHeight();
		//System.out.println("x:" + x + "  y:" + y + "  w:" + w + "  h:" + h);
		Rectangle rect = new Rectangle();
		rect.x = (int) x;
		rect.y = (int) y;
		rect.width = (int) w;
		rect.height = (int) h;
		return rect;
	}

	// -------------------------------------------------------

	public DockableTab cutTab () {
		DockableTab currentTab = (DockableTab) this.getSelectionModel().getSelectedItem();
		if (currentTab != null) {
			this.removeTab(currentTab);
		}
		return currentTab;
	}

	public void removeTab (DockableTab tab) {
		if (tab != null) {
			this.getTabs().remove(tab);
			this.parent.removeEmptyTabPane();
		}
	}

	public void removeTabPane () {
		this.getTabs().clear();
		this.parent.removeEmptyTabPane();
	}

	public void insertTab (int index, DockableTab tab) {
		if (index < 0) {
			index = 0;
		}
		this.getTabs().add(index, tab);
	}

	public void insertTab (DockableTab tab) {
		this.insertTab(this.getSelectionModel().getSelectedIndex(), tab);
	}

	public boolean moveTabBack () {
		int currentIndex = this.getSelectionModel().getSelectedIndex();
		if (currentIndex > 0) {
			currentIndex--;
			this.insertTab(currentIndex, this.cutTab());
			this.getSelectionModel().select(currentIndex);
			return true;
		}
		return false;
	}

	public boolean moveTabNext () {
		int currentIndex = this.getSelectionModel().getSelectedIndex();
		if (currentIndex < this.getTabs().size() - 1) {
			currentIndex++;
			this.insertTab(currentIndex, this.cutTab());
			this.getSelectionModel().select(currentIndex);
			return true;
		}
		return false;
	}

	public Tab findTab (String text) {
		return this.findAfterTab(text, 0);
	}

	public Tab findAfterTab (String text, int index) {
		int tabCount = this.getTabs().size();
		for (int i = index + 1; i < tabCount; i++) {
			Tab tab = this.getTabs().get(i);
			if (tab.getText().equals(text)) {
				return tab;
			}
		}
		return null;
	}

	public Tab findBeforeTab (String text, int index) {
		int tabCount = this.getTabs().size();
		if (index >= tabCount) {
			index = tabCount - 1;
		}
		for (int i = index - 1; i >= 0; i--) {
			Tab tab = this.getTabs().get(i);
			if (tab.getText().equals(text)) {
				return tab;
			}
		}
		return null;
	}

	// -------------------------------------------------------

	private void mousePressed (MouseEvent ev) {
		this.mousePressX = ev.getX();
		this.mousePressY = ev.getY();
		this.mouseDragging = true;
	}

	private void mouseDragged (MouseEvent ev) {
		if (this.mouseDragging && this.getSelectionModel().getSelectedItem() != null) {

			double currentMouseX = ev.getX();
			double currentMouseY = ev.getY();
			double mouseMoveX = currentMouseX - this.mousePressX;
			double mouseMoveY = currentMouseY - this.mousePressY;

			// Yの移動量が一定量以下の時は、タブの順番を変えるだけ
			if (mouseMoveY < 30 && mouseMoveY > -30) {
				if (ev.getPickResult().getIntersectedNode() instanceof LabeledText) {

					// マウスの下にあるタブのラベルを取得
					LabeledText label = (LabeledText) ev.getPickResult().getIntersectedNode();
					DockableTab currentTab = (DockableTab) this.getSelectionModel().getSelectedItem();

					// 自分のタブの上でない場合
					if (!label.getText().equals(this.getSelectionModel().getSelectedItem().getText())) {

						Tab overTab;
						if (mouseMoveX > 0) {
							overTab = this.findAfterTab(label.getText(), this.getSelectionModel().getSelectedIndex());
						}
						else {
							overTab = this.findBeforeTab(label.getText(), this.getSelectionModel().getSelectedIndex());
						}
						int overTabIndex = this.getTabs().indexOf(overTab);
						if (overTabIndex >= 0) {
							this.cutTab();
							this.insertTab(overTabIndex, currentTab);
							this.getSelectionModel().select(currentTab);
							this.mousePressX = ev.getX();
						}
					}
				}
			}

			// ドッキングを試みる
			else {
				this.parent.startShowDockingBorder(ev);
			}
		}
	}

	private void mouseReleased (MouseEvent ev) {
		DockingDirection direction = this.parent.startHideDockingBorder();
		this.mouseDragging = false;

		// ドッキング方向が指定されていれば、新しいパネルを作る
		if (direction != DockingDirection.NONE) {
			DockableTab currentTab = this.cutTab();
			if (currentTab != null) {
				this.parent.moveTab(direction, currentTab);
				currentTab.getTabPane().getSelectionModel().select(currentTab);
			}
		}
	}

}
