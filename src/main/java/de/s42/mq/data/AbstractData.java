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
package de.s42.mq.data;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 *
 * @author Benjamin Schiller
 * @param <DataValueType>
 */
public abstract class AbstractData<DataValueType extends Object> implements Data<DataValueType>
{
	protected boolean editable;
	protected String name;
	protected Class<DataValueType> dataType;
	protected boolean dirty;
	protected final List<WeakReference<DataHandler<DataValueType>>> handlers = new ArrayList<>();

	protected AbstractData()
	{
		dirty = true;
	}

	protected AbstractData(String name)
	{
		assert name != null;

		this.name = name;
		dirty = true;
	}

	@Override
	public void handleUpdate()
	{
		if (!dirty) {
			return;
		}

		Iterator<WeakReference<DataHandler<DataValueType>>> it = handlers.iterator();
		while (it.hasNext()) {
			WeakReference<DataHandler<DataValueType>> handlerReference = it.next();
			DataHandler<DataValueType> handler = handlerReference.get();
			if (handler != null) {
				handler.handleUpdate(this);
			}
			// prune invalid references
			else {
				it.remove();
			}
		}

		setDirty(false);
	}

	public void addHandler(DataHandler<DataValueType> handler)
	{
		assert handler != null;

		handlers.add(new WeakReference<>(handler));
	}

	public void removeHandler(DataHandler<DataValueType> remove)
	{
		assert remove != null;

		Iterator<WeakReference<DataHandler<DataValueType>>> it = handlers.iterator();
		while (it.hasNext()) {
			WeakReference<DataHandler<DataValueType>> handlerReference = it.next();
			DataHandler<DataValueType> handler = handlerReference.get();
			if (handler == remove) {
				it.remove();
			}
		}
	}

	@Override
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public Class<DataValueType> getDataType()
	{
		return dataType;
	}

	public void setDataType(Class<DataValueType> dataType)
	{
		assert dataType != null;

		this.dataType = dataType;
	}

	public boolean isDirty()
	{
		return dirty;
	}

	public void setDirty(boolean dirty)
	{
		this.dirty = dirty;
	}

	public boolean isValueEqual(DataValueType value)
	{
		return (getValue() == value);
	}

	public void setValueAndHandle(DataValueType value)
	{
		if (isValueEqual(value)) {
			return;
		}

		setValue(value);
		handleUpdate();
	}

	@Override
	public boolean isEditable()
	{
		return editable;
	}

	public void setEditable(boolean editable)
	{
		this.editable = editable;
	}

	@Override
	public String toString()
	{
		return "" + getValue();
	}
}
