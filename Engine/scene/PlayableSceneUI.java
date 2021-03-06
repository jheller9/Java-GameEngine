package scene;

import org.joml.Vector3f;

import audio.AudioHandler;
import core.Application;
import core.Resources;
import io.Controls;
import io.Input;
import io.Settings;
import scene.entity.PlayerHandler;
import scene.menu.pause.OptionsPanel;
import scene.overworld.TileShapePicker;
import scene.overworld.inventory.Inventory;
import scene.overworld.inventory.crafting.CraftingUI;
import scene.overworld.inventory.crafting.RecipeHandler;
import ui.UI;
import ui.menu.GuiMenu;
import ui.menu.listener.MenuListener;
import util.Colors;

public class PlayableSceneUI {

	private final GuiMenu mainMenu;
	private final OptionsPanel options;
	
	private TileShapePicker picker;
	
	private final PlayableScene scene;
	
	private final RecipeHandler recipeHandler;
	private final CraftingUI craftingUI;
	
	private final int CROSSHAIR_SIZE = 8;
	private final int CROSSHAIR_THICKNESS = 1;
	private final Vector3f CROSSHAIR_COLOR = new Vector3f(1, 1, 1);
	
	private boolean paused;
	
	public PlayableSceneUI(PlayableScene scene) {
		this.scene = scene;
		mainMenu = new GuiMenu(50, 300, "resume", "options", "quit");
		mainMenu.setFocus(true);
		mainMenu.setBordered(true);
		options = new OptionsPanel(null);
		
		recipeHandler = new RecipeHandler();
		craftingUI = new CraftingUI(recipeHandler);
		
		picker = new TileShapePicker(scene);
		
		Resources.addTexture("hp", "gui/hp.png");

		mainMenu.addListener(new MenuListener() {

			@Override
			public void onClick(String option, int index) {
				
				switch (index) {
				case 0:
					unpause();
					break;
				case 1:
					options.setFocus(!options.isFocused());
					break;
				case 2:
					scene.returnToMenu = true;
					scene.onSceneEnd();
					options.setFocus(false);
					break;
				}
			}

		});
	}
	
	public void cleanUp() {
		Resources.getTexture("hp").delete();
	}
	
	public void update() {
		if (scene.isLoading()) {
			UI.drawString("Loading", 720, 360);
			return;
		}
		UI.drawString(Application.VERSION, 5, 5, .25f, false);
		
		Inventory inventory = scene.getInventory();
		recipeHandler.update(inventory);
		craftingUI.update(inventory);
		
		picker.update();

		UI.drawRect(640 - CROSSHAIR_THICKNESS, 360 - CROSSHAIR_SIZE, 2 * CROSSHAIR_THICKNESS, CROSSHAIR_SIZE,
				CROSSHAIR_COLOR);
		UI.drawRect(640 - CROSSHAIR_THICKNESS, 360, 2 * CROSSHAIR_THICKNESS, CROSSHAIR_SIZE, CROSSHAIR_COLOR);
		UI.drawRect(640 - (CROSSHAIR_SIZE + 1), 360 - CROSSHAIR_THICKNESS, CROSSHAIR_SIZE, 2 * CROSSHAIR_THICKNESS,
				CROSSHAIR_COLOR);
		UI.drawRect(640, 360 - CROSSHAIR_THICKNESS, CROSSHAIR_SIZE, 2 * CROSSHAIR_THICKNESS, CROSSHAIR_COLOR);
		
		for(int i = 0; i < scene.getPlayer().getHp(); i++) {
			UI.drawImage("hp", 980 + (i*28), 660, 24, 24);
		}

		if (Input.isPressed(Controls.get("pause"))) {
			if (!paused) {
				if (inventory.isOpen()) {
					inventory.toggleOpen();
				} else {
					Input.requestMouseRelease();
					paused = true;
					AudioHandler.pause();
					PlayerHandler.disable();
				}
			} else {
				unpause();
			}
		}
		
		if (paused) {
			UI.drawRect(0, 0, 1280, 720, Colors.BLACK).setOpacity(.5f);
			if (options.isFocused()) {
				options.update();
				options.draw();
			} else {
				mainMenu.draw();
			}
		}
	
	}

	private void unpause() {
		if (options.isFocused()) {
			options.setFocus(false);
			Settings.grabData();
			Settings.save();
		} else {
			paused = false;
			AudioHandler.unpause();
			PlayerHandler.enable();
			//if (!Console.isVisible()) {
				Input.requestMouseGrab();
			//}
		}
	}

	public boolean isPaused() {
		return paused;
	}
}
