package smartsound.view.gui;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.KeyStroke;

import smartsound.common.Tuple;
import smartsound.view.Action;
import de.intarsys.pdf.content.common.CSCreator;
import de.intarsys.pdf.encoding.WinAnsiEncoding;
import de.intarsys.pdf.font.PDFont;
import de.intarsys.pdf.font.PDFontType1;
import de.intarsys.pdf.pd.PDDocument;
import de.intarsys.pdf.pd.PDPage;
import de.intarsys.tools.locator.FileLocator;

public class PDFExporter {
	
	public static void exportHotkeys(final Map<UUID, String> nameMap,
			final Map<UUID, List<Tuple<String, Action>>> actionMap,
			final Map<Action, String> commentMap, final String filePath) {
		
		PDDocument output = PDDocument.createNew();
		PDPage page;
		for (UUID sceneUUID : nameMap.keySet()) {
			page = (PDPage) PDPage.META.createNew();
			output.addPageNode(page);
			
			//drawLayout(page);
			drawHotkeys(page, sceneUUID, nameMap.get(sceneUUID), actionMap.get(sceneUUID), commentMap);
		}
		
		FileLocator locator = new FileLocator(filePath);
		try {
			output.save(locator, null);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void drawLayout(final PDPage page) {
		CSCreator creator = CSCreator.createNew(page);
		
		creator.close();
	}
	
	private static void drawHotkeys(final PDPage page, final UUID sceneUUID, final String sceneName, final List<Tuple<String, Action>> actionList, final Map<Action, String> commentMap) {
		CSCreator creator = CSCreator.createNew(page);

		PDFont font = PDFontType1.createNew(PDFontType1.FONT_Helvetica);
		font.setEncoding(WinAnsiEncoding.UNIQUE);
		
		float fontSize = 20;
		float columnHotkey = 50;
		float columnAction = 200;
		float columnComment = 350;
		float currentRowY = 650;
		float rowDelta = 20;
		creator.textSetFont(null, font, fontSize);
		creator.textLineMoveTo(50, 700);

		creator.textShow(sceneName);
		
		fontSize = 12;
		creator.textSetFont(null, font, fontSize);
		
		creator.textLineMoveTo(columnHotkey, currentRowY);
		creator.textShow("Hotkey");
		creator.textLineMoveTo(columnAction, currentRowY);
		creator.textShow("Action");
		creator.textLineMoveTo(columnComment, currentRowY);
		creator.textShow("Comment");
		
		for (Tuple<String, Action> tuple : actionList) {
			System.out.println("print one Action");
			currentRowY -= rowDelta;
			
			creator.textLineMoveTo(columnHotkey, currentRowY);
			creator.textShow(getKeyStroke(tuple.first).toString());
			creator.textLineMoveTo(columnAction, currentRowY);
			creator.textShow(tuple.second.getDescription());
			creator.textLineMoveTo(columnComment, currentRowY);
			creator.textShow(commentMap.get(tuple.second) == null ? "" : commentMap.get(tuple.second));

		}
		
		creator.close();
	}
	
	private static KeyStroke getKeyStroke(final String hotkeyString) {
		String[] split = hotkeyString.split("\\|");
		return KeyStroke.getKeyStroke(Integer.parseInt(split[1]),Integer.parseInt(split[0]));
	}
}
