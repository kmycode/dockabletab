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

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

enum DockingDirection {
	NONE,
	TOP,
	RIGHT,
	BOTTOM,
	LEFT,
	OVER,
	OVER_TOP,
	OVER_RIGHT,
	OVER_BOTTOM,
	OVER_LEFT,
	FLOATING,;
}

/**
 * ドッキング可能なエリアで、ひとかたまりの長方形グループをあらわす
 * SplitPaneの特性で水平・垂直方向のどちらかにしか指定できないためｒｙ
 * ちなみにrootはVerticalであることを想定
 * itemは、DockableTabPaneとDockableAreaGroupPaneのどちらかを入れる
 *
 * @author KMY
 */
public class DockableAreaGroupPane extends SplitPane {

	private DockableAreaGroupPane parent = null;
	private DockableAreaGroupPane rootPane = null;		// 取得時は必ずgetRootPaneを使用
	private DockablePane rootParent;
	private DockableAreaGroupPane childPane = null;
	private static DockingBorder dockingBorder;
	private static DockingDirection dockingDirection = DockingDirection.NONE;
	private static DockableTabPane overTabPane;

	DockableAreaGroupPane (DockableAreaGroupPane parent) {
		this(parent, Orientation.HORIZONTAL);
	}

	DockableAreaGroupPane (DockableAreaGroupPane parent, Orientation orientation) {
		this.parent = parent;
		this.rootPane = parent.getRootPane();
		this.rootParent = parent.rootParent;
		this.setOrientation(orientation);
		this.setChildPane();
	}

	public DockableAreaGroupPane (DockablePane parent) {
		this(parent, Orientation.HORIZONTAL);
	}

	public DockableAreaGroupPane (DockablePane parent, Orientation orientation) {
		this.rootParent = parent;
		if (DockableAreaGroupPane.dockingBorder == null) {
			DockableAreaGroupPane.dockingBorder = new DockingBorder(parent.getWindow());
			DockableAreaGroupPane.dockingBorder.setLocalPosition(0, 0);
			DockableAreaGroupPane.dockingBorder.setSize(100, 100);
		}
		this.setOrientation(orientation);
		this.setChildPane();
	}

	// 分割方向が水平ならば、垂直方向のペインを作成する
	private void setChildPane () {
		if (this.getOrientation() == Orientation.HORIZONTAL) {
			this.childPane = new DockableAreaGroupPane(this, Orientation.VERTICAL);
			this.getItems().add(this.childPane);
		}
	}

	public ObservableList<Node> getTabPaneItems () {
		return this.childPane == null ? this.getItems() : this.childPane.getItems();
	}

	DockableAreaGroupPane getChildArea () {
		return this.childPane == null ? this : this.childPane;
	}

	DockableAreaGroupPane getParentArea () {
		return this.childPane != null ? this : this.parent;
	}

	/*
	 * public ObservableList<Node> getTabPaneItems () {
	 * return this.childPane == null ? super.getTabPaneItems() : this.childPane.getTabPaneItems();
	 *	}

	public ObservableList<Node> getParentItems () {
		return this.childPane != null ? super.getTabPaneItems() : this.parent.getTabPaneItems();
	 *	}
	 */

	// コンストラクタ内でのthisリークを防ぐ
	// rootPaneを利用するときは、必ずこのメソッドを経由すること
	DockableAreaGroupPane getRootPane () {
		/*
		 if (this.rootPane == null) {
			this.rootPane = this;
		}
		 // 古いルートの上に、新しいルートが設定された場合を想定
		else if (this.rootPane != this) {
			this.rootPane = this.rootPane.getRootPane();
		}
		 */
		//this.rootPane = this.parent != null ? this.parent.getRootPane() : this;
		//return this.rootPane;
		return this.rootParent.rootGroupProperty().get();
	}

	// 親パネルを設定
	void setParentPane (DockableAreaGroupPane pane) {
		this.parent = pane;
		// 自分がルートならば、ルートを変更したことになる
		if (this.getRootPane() == this) {
			this.rootPane = pane;
			this.rootPane.getTabPaneItems().add(this);
		}
	}

	// -------------------------------------------------------
	// 指定したインデックス番号に、新しいタブパネルを追加する
	public DockableTabPane add (int index) {
		DockableTabPane pane = new DockableTabPane(this);
		AnchorPane.setTopAnchor(pane, 10.0);
		AnchorPane.setLeftAnchor(pane, 10.0);
		AnchorPane.setRightAnchor(pane, 10.0);
		AnchorPane.setBottomAnchor(pane, 10.0);
		this.add(index, pane);
		return pane;
	}

	// 指定したインデックス番号に、指定したタブパネルを追加する
	public void add (int index, DockableTabPane pane) {
		this.getTabPaneItems().add(index, pane);
	}

	// 指定したタブパネルを末尾に追加する
	public void add (DockableTabPane pane) {
		this.getTabPaneItems().add(pane);
		pane.setParent(this);
	}

	// 指定したタブを、指定した方向へ移動する。ドッキングになるか、他のタブパネルへの移動になるかはこのメソッドで決まる
	void moveTab (DockingDirection direction, DockableTab tab) {
		if (direction != DockingDirection.NONE && direction != DockingDirection.OVER && direction != DockingDirection.OVER_TOP
				&& direction != DockingDirection.OVER_RIGHT && direction != DockingDirection.OVER_LEFT && direction
				!= DockingDirection.OVER_BOTTOM && direction != DockingDirection.FLOATING) {
			DockableTabPane tabPane = new DockableTabPane(this);
			tabPane.getTabs().add(tab);
			if (this.getOrientation() == Orientation.VERTICAL) {
				if (direction == DockingDirection.BOTTOM) {
					this.add(tabPane);
				}
				else if (direction == DockingDirection.TOP) {
					this.add(0, tabPane);
				}
			}
			else if (this.getOrientation() == Orientation.HORIZONTAL) {
				DockableAreaGroupPane groupPane = new DockableAreaGroupPane(this, Orientation.VERTICAL);
				groupPane.add(tabPane);
				if (direction == DockingDirection.RIGHT) {
					this.getParentArea().getItems().add(groupPane);
				}
				else if (direction == DockingDirection.LEFT) {
					this.getParentArea().getItems().add(0, groupPane);
				}
			}
		}
		else if (direction == DockingDirection.OVER) {
			this.getOverTabPane().getTabs().add(tab);
		}
		else if (direction == DockingDirection.OVER_BOTTOM) {
			DockableTabPane tabPane = new DockableTabPane(this);
			tabPane.getTabs().add(tab);
			this.getOverTabPane().getParentPane().getTabPaneItems().add(tabPane);
		}
		else if (direction == DockingDirection.OVER_TOP) {
			DockableTabPane tabPane = new DockableTabPane(this);
			tabPane.getTabs().add(tab);
			this.getOverTabPane().getParentPane().getTabPaneItems().add(0, tabPane);
		}
		else if (direction == DockingDirection.FLOATING) {
			DockableAreaGroupPane groupPane = new DockableAreaGroupPane(this);
			DockableTabPane tabPane = new DockableTabPane(this);
			tabPane.getTabs().add(tab);
			groupPane.getTabPaneItems().add(tabPane);
			DockablePane newPane = new DockablePane(this.rootParent, this, dockingBorder.getX(), dockingBorder.getY(), dockingBorder.
													getWidth(), dockingBorder.getHeight());
			newPane.getChildren().add(groupPane);
			AnchorPane.setTopAnchor(groupPane, 0.0);
			AnchorPane.setLeftAnchor(groupPane, 0.0);
			AnchorPane.setRightAnchor(groupPane, 0.0);
			AnchorPane.setBottomAnchor(groupPane, 0.0);
		}
		this.getRootPane().removeEmptyTabPane();
	}

	public List<DockableTabPane> getTabPaneList () {
		 ArrayList<DockableTabPane> result = new ArrayList<>();
		this.getTabPaneList(result);
		return result;
		//return this.rootParent.getTabPaneList();
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

	void getTabPaneList (List<DockableTabPane> result) {
		for (Node node : this.getItems()) {
			if (node instanceof DockableTabPane) {
				result.add((DockableTabPane) node);
			}
			else if (node instanceof DockableAreaGroupPane) {
				((DockableAreaGroupPane) node).getTabPaneList(result);
			}
		}
	}

	public void remove (TabPane e) {
		this.getTabPaneItems().remove(e);
	}

	public void remove (int index) {
		this.getTabPaneItems().remove(index);
	}

	// タブを持たないタブパネルやグループパネルを見つけたら消す
	public void removeEmptyTabPane () {
		List<Node> removeNodeList = new ArrayList<>();
		for (Node node : this.getItems()) {
			if (node instanceof DockableTabPane) {

				// 最後のタブペインであるときは、タブペインを消さない
				if (this == this.getRootPane() && this.getTabPaneItems().size() <= 1) {
					continue;
				}

				// 最後のものでなければ消す
				DockableTabPane tabPane = (DockableTabPane) node;
				if (tabPane.getTabs().size() <= 0) {
					removeNodeList.add(tabPane);
				}
			}
			else if (node instanceof DockableAreaGroupPane) {
				DockableAreaGroupPane groupPane = (DockableAreaGroupPane) node;
				groupPane.removeEmptyTabPane();
				if (groupPane.getTabPaneItems().size() <= 0) {
					removeNodeList.add(groupPane);
				}
			}
		}

		// フローティング
		for (DockablePane pane : this.rootParent.getMainPane().getChildPane()) {
			if (pane.isEmpty()) {
				pane.close();
			}
		}

		this.getItems().removeAll(removeNodeList);
		this.getTabPaneItems().removeAll(removeNodeList);
	}

	// -------------------------------------------------------
	void startShowDockingBorder (MouseEvent ev) {
		this.getRootPane().showDockingBorder(ev);
	}

	private void showDockingBorder (MouseEvent ev) {
		double windowX = this.rootParent.getWindow().getX();
		double windowY = this.rootParent.getWindow().getY();
		double mouseX = ev.getScreenX();
		double mouseY = ev.getScreenY();
		Point2D position = this.getPosition();
		Rectangle2D size = this.getSize();
		double paneX = windowX + position.getX();
		double paneY = windowY + position.getY();
		double paneW = size.getWidth();
		double paneH = size.getHeight();
		Rectangle eventTabPaneRect = ((DockableTabPane) ev.getSource()).getLocalRectangle();
		double localMouseX = ev.getX() + eventTabPaneRect.getX();
		double localMouseY = ev.getY() + eventTabPaneRect.getY();

		double tabPaneY = this.rootParent.getLayoutY();

		// 結果は決まったか？
		boolean hit = false;

		// Right
		if (paneX + paneW - 60 < mouseX && paneX + paneW + 60 > mouseX) {
			DockableAreaGroupPane.dockingBorder.setPosition(paneX + paneW - 50, paneY + 30 + tabPaneY);
			DockableAreaGroupPane.dockingBorder.setSize(50, paneH);
			DockableAreaGroupPane.dockingBorder.show(this.getRootPane().rootParent.getWindow());
			DockableAreaGroupPane.dockingDirection = DockingDirection.RIGHT;
			hit = true;
		}
		// Left
		else if (paneX + 60 > mouseX && paneX - 60 < mouseX) {
			DockableAreaGroupPane.dockingBorder.setPosition(paneX + 10, paneY + 30 + tabPaneY);
			DockableAreaGroupPane.dockingBorder.setSize(50, paneH);
			DockableAreaGroupPane.dockingBorder.show(this.getRootPane().rootParent.getWindow());
			DockableAreaGroupPane.dockingDirection = DockingDirection.LEFT;
			hit = true;
		}
		// Over or Floating
		else {

			List<DockableTabPane> tabPaneList = this.rootParent.rootGroupProperty().get().getTabPaneList();
			for (DockableTabPane tabPane : tabPaneList) {
				Rectangle rect = tabPane.getLocalRectangle();
				if (localMouseX > rect.x && localMouseX < rect.x + rect.width && localMouseY > rect.y && localMouseY
						< rect.y + rect.height) {

					// Bottom
					if (paneY + paneH - 60 < mouseY) {
						DockableAreaGroupPane.dockingBorder.setPosition(rect.x + windowX + 10, rect.height + rect.y + windowY - 30
																		+ tabPaneY);
						DockableAreaGroupPane.dockingBorder.setSize(rect.width, 50);
						DockableAreaGroupPane.dockingBorder.show(this.getRootPane().rootParent.getWindow());
						DockableAreaGroupPane.dockingDirection = DockingDirection.OVER_BOTTOM;
					}
					// Top
					else if (paneY + 100 > mouseY) {
						DockableAreaGroupPane.dockingBorder.setPosition(rect.x + windowX + 10, rect.y + windowY + 30 + tabPaneY);
						DockableAreaGroupPane.dockingBorder.setSize(rect.width, 50);
						DockableAreaGroupPane.dockingBorder.show(this.getRootPane().rootParent.getWindow());
						DockableAreaGroupPane.dockingDirection = DockingDirection.OVER_TOP;
					}

					// Over
					else {
						DockableAreaGroupPane.dockingBorder.setPosition(rect.x + windowX + 10, rect.y + windowY + 30
																		+ tabPaneY);
						DockableAreaGroupPane.dockingBorder.setSize(rect.width, rect.height);
						DockableAreaGroupPane.dockingBorder.show(this.getRootPane().rootParent.getWindow());
						DockableAreaGroupPane.dockingDirection = DockingDirection.OVER;
					}

					DockableAreaGroupPane.overTabPane = tabPane;
					hit = true;

					break;
				}
			}
		}

		// いずれでもなければFloating
		if (!hit) {
			DockableAreaGroupPane.dockingBorder.setPosition(mouseX, mouseY);
			DockableAreaGroupPane.dockingBorder.setSize(paneW, paneH);
			DockableAreaGroupPane.dockingBorder.show(this.getRootPane().rootParent.getWindow());
			DockableAreaGroupPane.dockingDirection = DockingDirection.FLOATING;
			hit = true;
		}
	}

	DockingDirection startHideDockingBorder () {
		DockingDirection result = DockableAreaGroupPane.dockingDirection;
		this.getRootPane().hideDockingBorder();
		return result;
	}

	private void hideDockingBorder () {
		DockableAreaGroupPane.dockingBorder.hide();
		DockableAreaGroupPane.dockingDirection = DockingDirection.NONE;
	}

	DockableTabPane getOverTabPane () {
		return DockableAreaGroupPane.overTabPane;
	}

	Point2D getPosition () {
		return this.getPosition(this);
	}

	Point2D getPosition (Control target) {
		int x = 0;
		int y = 0;
		/*
		 		DockableAreaGroupPane pane = this;
		DockableAreaGroupPane parentPane = this.parent;
		while (pane != pane.getRootPane()) {
			for (Node node : parentPane.getTabPaneItems()) {
					if (node != this) {
					if (node instanceof DockableAreaGroupPane) {
						if (parentPane.getOrientation() == Orientation.VERTICAL) {
							y += ((Control) node).getHeight();
						}
						else {
							x += ((Control) node).getWidth();
						}
					}
				}
				else {
					break;
				}
			}
			pane = parentPane;
			parentPane = parentPane.parent;
		}
		 * */

		if (this.parent != null) {
			Point2D ppos = this.parent.getPosition(this);
			x += ppos.getX();
			y += ppos.getY();
		}

		if (this.parent != null) {
			for (Node node : this.parent.getTabPaneItems()) {
				if (node != target) {
					if (node instanceof Control) {
						if (this.getOrientation() == Orientation.VERTICAL) {
							y += ((Control) node).getHeight();
						}
						else {
							x += ((Control) node).getWidth();
						}
					}
				}
				else {
					break;
				}
			}
			return new Point2D(x, y);
		}
		else {
			return new Point2D(0, 0);
		}
	}

	private Rectangle2D getSize () {
		double width = this.getWidth();
		double height = this.getHeight();
		return new Rectangle2D(0, 0, width, height);
	}

}
