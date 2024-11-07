/*
 * Copyright Studio 42 GmbH 2021. All rights reserved.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For details to the License read https://www.s42m.de/license
 */
package de.s42.mq.ui.textfield;

import de.s42.base.strings.StringHelper;
import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.annotations.attributes.RequiredDLAnnotation.required;
import de.s42.dl.annotations.persistence.DontPersistDLAnnotation.dontPersist;
import de.s42.dl.exceptions.DLException;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.cameras.Camera;
import de.s42.mq.data.StringData;
import de.s42.mq.fonts.Text;
import de.s42.mq.fonts.TextOptions;
import de.s42.mq.meshes.MeshGroup;
import de.s42.mq.ui.Panel;
import de.s42.mq.ui.PanelOptions;
import de.s42.mq.ui.UIComponent;
import de.s42.mq.ui.UIManager;
import de.s42.mq.ui.layout.Layout;
import de.s42.mq.ui.layout.LayoutOptions;
import static org.lwjgl.glfw.GLFW.*;

/**
 *
 * @author Benjamin Schiller
 */
public class Textfield extends MeshGroup implements UIComponent
{

	private final static Logger log = LogManager.getLogger(Textfield.class.getName());

	@required
	protected StringData text = new StringData();

	@required
	protected TextOptions textOptions;

	@required
	protected PanelOptions panelOptions;

	/**
	 * if no layout is given position and scale shall remain untouched
	 */
	@AttributeDL(required = false)
	protected Layout layout;

	@AttributeDL(required = false)
	protected LayoutOptions layoutOptions;

	@AttributeDL(required = false)
	protected UIManager uiManager;

	@AttributeDL(required = false, defaultValue = "true")
	protected boolean focusable = true;

	@dontPersist
	protected Text textComponent = new Text();

	@dontPersist
	protected Text caretComponent = new Text();

	@dontPersist
	protected int caretPosition;

	@dontPersist
	protected Panel panelComponent = new Panel();

	@Override
	public Textfield copy()
	{
		try {
			Textfield copy = (Textfield) super.copy();

			// @todo finalize proper copying
			copy.text.setValue(text.getValue());
			copy.layout = layout;
			copy.layoutOptions = (layoutOptions != null) ? layoutOptions.copy() : null;
			copy.uiManager = uiManager;
			copy.textOptions = textOptions.copy();
			copy.panelOptions = panelOptions.copy();
			copy.textComponent = textComponent.copy();
			copy.panelComponent = panelComponent.copy();
			copy.caretComponent = caretComponent.copy();
			copy.caretPosition = caretPosition;

			return copy;
		} catch (Exception ex) {
			throw new RuntimeException("Error copying button " + ex.getMessage(), ex);
		}
	}

	@Override
	public void handleChar(String chars)
	{
		assert chars != null : "chars != null";

		String currentText = text.getValue();
		caretPosition = Math.max(Math.min(caretPosition, currentText.length()), 0);
		currentText = StringHelper.insert(currentText, chars, caretPosition);
		caretPosition += chars.length();
		text.setValueAndHandle(currentText);
	}

	@Override
	public void handleKey(int key, int scancode, int action, int mods)
	{
		log.debug("handleKey", key, scancode, action, mods);

		if (action == GLFW_PRESS || action == GLFW_REPEAT) {

			switch (key) {
				// Handle backspace -> remove char before caret
				case GLFW_KEY_BACKSPACE -> {
					String currentText = text.getValue();
					caretPosition = Math.max(Math.min(caretPosition, currentText.length()), 0);
					if (caretPosition > 0) {
						caretPosition = caretPosition - 1;
						currentText = StringHelper.remove(currentText, caretPosition, 1);
						text.setValueAndHandle(currentText);
					}
				}
				// Handle backspace -> remove char after caret
				case GLFW_KEY_DELETE -> {
					String currentText = text.getValue();
					caretPosition = Math.max(Math.min(caretPosition, currentText.length()), 0);
					currentText = StringHelper.remove(currentText, caretPosition, 1);
					text.setValueAndHandle(currentText);
				}
				// Left -> Caret to left
				case GLFW_KEY_LEFT ->
					caretPosition = Math.max(caretPosition - 1, 0);
				// Left -> Caret to right
				case GLFW_KEY_RIGHT ->
					caretPosition = Math.min(caretPosition + 1, text.getValue().length());
				// End -> Caret end of text
				case GLFW_KEY_END ->
					caretPosition = text.getValue().length();
				// End -> Caret end of text
				case GLFW_KEY_HOME ->
					caretPosition = 0;
			}
		}
	}

	@Override
	public void handleClick(int x, int y) throws DLException
	{
		log.debug("handleClick", x, y);
	}

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		super.load();

		if (uiManager != null) {
			uiManager.register(this);
		}

		panelComponent.setOptions(getPanelOptions());
		panelComponent.setIdentifier(getIdentifier());
		panelComponent.setLayout(getLayout());
		panelComponent.setLayoutOptions(getLayoutOptions());
		panelComponent.setCamera(getCamera());
		panelComponent.setLayers(getLayers());
		panelComponent.load();
		addMesh(panelComponent);

		textComponent.getPosition().z = 0.001f;
		textComponent.setOptions(getTextOptions());
		textComponent.setText(getText());
		textComponent.setIdentifier(getIdentifier());
		textComponent.setLayout(getLayout());
		textComponent.setLayoutOptions(getLayoutOptions());
		textComponent.setCamera(getCamera());
		textComponent.setLayers(getLayers());
		textComponent.load();
		addMesh(textComponent);

		caretComponent.getPosition().z = 0.002f;
		caretComponent.setOptions(getTextOptions());
		caretComponent.getText().setValue("");
		caretComponent.setIdentifier(getIdentifier());
		caretComponent.setLayout(getLayout());
		caretComponent.setLayoutOptions(getLayoutOptions());
		caretComponent.setCamera(getCamera());
		caretComponent.setLayers(getLayers());
		caretComponent.load();
		addMesh(caretComponent);
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		removeMesh(caretComponent);
		removeMesh(textComponent);
		removeMesh(panelComponent);

		if (uiManager != null) {
			uiManager.unregister(this);
		}

		super.unload();
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public StringData getText()
	{
		return text;
	}

	public void setText(StringData text)
	{
		this.text = text;
	}

	public TextOptions getTextOptions()
	{
		return textOptions;
	}

	public void setTextOptions(TextOptions textOptions)
	{
		this.textOptions = textOptions;
	}

	@Override
	public Layout getLayout()
	{
		return layout;
	}

	@Override
	public void setLayout(Layout layout)
	{
		this.layout = layout;
	}

	@Override
	public LayoutOptions getLayoutOptions()
	{
		return layoutOptions;
	}

	@Override
	public void setLayoutOptions(LayoutOptions layoutOptions)
	{
		this.layoutOptions = layoutOptions;
	}

	public Text getTextComponent()
	{
		return textComponent;
	}

	@Override
	public void setIdentifier(int identifier)
	{
		super.setIdentifier(identifier);

		textComponent.setIdentifier(identifier);
		panelComponent.setIdentifier(identifier);
	}

	@Override
	public void setCamera(Camera camera)
	{
		super.setCamera(camera);

		textComponent.setCamera(camera);
		panelComponent.setCamera(camera);
	}

	@Override
	public void setModelMatrixDirty(boolean modelMatrixDirty)
	{
		super.setModelMatrixDirty(modelMatrixDirty);

		textComponent.setModelMatrixDirty(modelMatrixDirty);
		panelComponent.setModelMatrixDirty(modelMatrixDirty);
	}

	public PanelOptions getPanelOptions()
	{
		return panelOptions;
	}

	public void setPanelOptions(PanelOptions panelOptions)
	{
		this.panelOptions = panelOptions;
	}

	public Panel getPanelComponent()
	{
		return panelComponent;
	}

	@Override
	public UIManager getUiManager()
	{
		return uiManager;
	}

	@Override
	public void setUiManager(UIManager uiManager)
	{
		this.uiManager = uiManager;
	}

	@Override
	public boolean isFocusable()
	{
		return focusable;
	}

	@Override
	public void setFocusable(boolean focusable)
	{
		this.focusable = focusable;
	}

	public Text getCaretComponent()
	{
		return caretComponent;
	}

	public void setCaretComponent(Text caretComponent)
	{
		this.caretComponent = caretComponent;
	}

	public int getCaretPosition()
	{
		return caretPosition;
	}

	public void setCaretPosition(int caretPosition)
	{
		this.caretPosition = caretPosition;
	}
	// "Getters/Setters" </editor-fold>
}
