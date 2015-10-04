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

import javafx.event.Event;
import javafx.scene.control.Tab;

/**
 *
 * @author KMY
 */
public class DockableTab extends Tab {

	private DockableTabPane tabPane;

	public DockableTab (String title) {
		super(title);

		this.setOnCloseRequest((obj) -> {
			this.onCloseRequest(obj);
			DockableTab.this.tabPane = (DockableTabPane) this.getTabPane();
		});
		this.setOnClosed((obj) -> {
			this.onClosed(obj);
			DockableTab.this.tabPane.getParentPane().getRootPane().removeEmptyTabPane();
		});
	}

	// イベントはこのクラス内で設定するため
	// タブを閉じるイベントを設定するときは、別途このメソッドをオーバーライドする
	protected void onClosed (Event obj) {
	}

	// 同上
	protected void onCloseRequest (Event obj) {
	}

}
