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

import de.s42.dl.annotations.persistence.DontPersistDLAnnotation.dontPersist;
import de.s42.dl.types.DLContainer;
import de.s42.mq.assets.AbstractAsset;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Benjamin Schiller
 */
public class UIManager extends AbstractAsset implements DLContainer<UIComponent>
{

	protected final static AtomicInteger nextComponentId = new AtomicInteger(0);

	@dontPersist
	protected final List<UIComponent> components = new ArrayList<>();

	@dontPersist
	protected final Map<Integer, UIComponent> componentsByIdentifier = new HashMap<>();

	@dontPersist
	protected final Map<String, UIComponent> templateComponents = new HashMap<>();

	@dontPersist
	protected UIComponent focusedComponent;

	public boolean containsComponentId(int id)
	{
		return componentsByIdentifier.containsKey(id);
	}

	public UIComponent getComponent(int id)
	{
		return componentsByIdentifier.get(id);
	}

	public int getNextComponentId()
	{
		return nextComponentId.incrementAndGet();
	}

	public <UIComponentType extends UIComponent> UIComponentType createFromTemplate(String templateName)
	{
		assert templateName != null;

		UIComponent uiComponent = templateComponents.get(templateName);

		if (uiComponent == null) {
			throw new RuntimeException("uiComponent '" + templateName + "' not contained in templates");
		}

		return (UIComponentType) uiComponent.copy();
	}

	public void register(UIComponent component)
	{
		assert component != null;

		component.setIdentifier(getNextComponentId());

		components.add(component);
		componentsByIdentifier.put(component.getIdentifier(), component);
	}

	public void unregister(UIComponent component)
	{
		assert component != null;

		components.remove(component);
		componentsByIdentifier.remove(component.getIdentifier());
	}

	public void handleClick(int identifier, int x, int y) throws Exception
	{
		UIComponent component = componentsByIdentifier.get(identifier);

		if (component != null) {

			// Handle focus of clicked component
			if (component.isFocusable()) {
				setFocusedComponent(component);
			}

			component.handleClick(x, y);
		}
	}

	@Override
	public void addChild(String name, UIComponent child)
	{
		assert child != null;

		addTemplateComponent(child);
	}

	@Override
	public List<UIComponent> getChildren()
	{
		return (List<UIComponent>) Collections.unmodifiableList(new ArrayList<>(templateComponents.values()));
	}

	public Map<String, UIComponent> getTemplateComponents()
	{
		return templateComponents;
	}

	/**
	 * Can just add named components to allow to create from them later
	 *
	 * @param templateComponent
	 */
	public void addTemplateComponent(UIComponent templateComponent)
	{
		assert templateComponent != null;
		assert templateComponent.getName() != null;

		templateComponents.put(templateComponent.getName(), templateComponent);
	}

	public List<UIComponent> getComponents()
	{
		return Collections.unmodifiableList(components);
	}

	public Map<Integer, UIComponent> getComponentsByIdentifier()
	{
		return Collections.unmodifiableMap(componentsByIdentifier);
	}

	public UIComponent getFocusedComponent()
	{
		return focusedComponent;
	}

	public void setFocusedComponent(UIComponent focusedComponent)
	{
		this.focusedComponent = focusedComponent;
	}
}
