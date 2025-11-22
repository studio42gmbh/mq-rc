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
package de.s42.mq.ui;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.annotations.attributes.RequiredDLAnnotation.required;
import de.s42.dl.annotations.persistence.DontPersistDLAnnotation.dontPersist;
import de.s42.dl.exceptions.DLException;
import de.s42.mq.cameras.Camera;
import de.s42.mq.data.StringData;
import de.s42.mq.fonts.Text;
import de.s42.mq.fonts.TextOptions;
import de.s42.mq.meshes.MeshGroup;
import de.s42.mq.ui.actions.UIAction;
import de.s42.mq.ui.layout.Layout;
import de.s42.mq.ui.layout.LayoutOptions;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Benjamin Schiller
 */
public class Button extends MeshGroup implements UIAction, UIComponent
{

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
	protected Panel panelComponent = new Panel();

	@dontPersist
	protected final List<UIAction> actions = new ArrayList<>();

	@Override
	public Button copy()
	{
		try {
			Button copy = (Button) super.copy();

			// @todo finalize proper copying
			copy.text.setValue(text.getValue());
			copy.layout = layout;
			copy.layoutOptions = (layoutOptions != null) ? (LayoutOptions) layoutOptions.copy() : null;
			copy.uiManager = uiManager;
			copy.textOptions = textOptions.copy();
			copy.panelOptions = panelOptions.copy();
			copy.textComponent = textComponent.copy();
			copy.panelComponent = panelComponent.copy();
			copy.actions.addAll(actions);

			return copy;
		} catch (Exception ex) {
			throw new RuntimeException("Error copying button " + ex.getMessage(), ex);
		}
	}

	@Override
	public void handleKey(int key, int scancode, int action, int mods)
	{
	}

	@Override
	public void handleChar(String chars)
	{
	}

	@Override
	public void handleClick(int x, int y) throws DLException
	{
		doPerform();
	}

	public void addAction(UIAction action)
	{
		assert action != null;

		actions.add(action);
	}

	@Override
	public void addChild(String name, Object child)
	{
		if (child instanceof UIAction action) {
			addAction(action);
		}

		super.addChild(name, child);
	}

	@Override
	public void doPerform() throws DLException
	{
		for (UIAction action : actions) {
			action.doPerform();
		}
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

		panelComponent.setParentMatrix(getModelMatrix());
		panelComponent.setOptions(getPanelOptions());
		panelComponent.setIdentifier(getIdentifier());
		panelComponent.setLayout(getLayout());
		panelComponent.setLayoutOptions(getLayoutOptions());
		panelComponent.setCamera(getCamera());
		panelComponent.setLayers(getLayers());
		panelComponent.load();
		addMesh(panelComponent);

		textComponent.setParentMatrix(getModelMatrix());
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
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

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
	// "Getters/Setters" </editor-fold>
}
