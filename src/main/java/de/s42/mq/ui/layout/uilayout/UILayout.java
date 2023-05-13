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
package de.s42.mq.ui.layout.uilayout;

import de.s42.dl.DLAnnotation.AnnotationDL;
import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.mq.core.AbstractEntity;
import de.s42.mq.data.Vector2Data;
import de.s42.mq.materials.Texture;
import de.s42.mq.meshes.Mesh;
import de.s42.mq.ui.Image;
import de.s42.mq.ui.Panel;
import de.s42.mq.ui.layout.Layout;
import static de.s42.mq.ui.layout.uilayout.UILayoutFit.*;
import de.s42.mq.util.Transform;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.dl.annotations.EditableDLAnnotation;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 *
 * @author Benjamin Schiller
 */
public class UILayout extends AbstractEntity implements Layout<UILayoutOptions>
{

	private final static Logger log = LogManager.getLogger(UILayout.class.getName());

	@AttributeDL(required = true)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	protected Vector2Data dimension = new Vector2Data();

	@Override
	public void layout(Mesh mesh, UILayoutOptions options)
	{
		assert mesh != null : "mesh != null for " + mesh;
		assert options != null : "options != null for " + mesh;

		Transform meshTransform = mesh.getTransform();

		//log.debug("Layouting " + mesh.getName() + " " + options);
		Vector2f displaySize = new Vector2f(getDimension().getValue());
		Vector2f toScreenCoords = (new Vector2f(2.0f, -2.0f)).div(displaySize);

		// left top with anchor
		Vector2f posLT = new Vector2f(options.getLeftTop());
		posLT = posLT.mul(toScreenCoords);
		Vector2f anchorLT = new Vector2f(options.getLeftTopAnchor());
		posLT = anchorLT.add(posLT);

		// bottom right with anchor
		Vector2f posBR = new Vector2f(options.getRightBottom());
		posBR = posBR.mul(toScreenCoords);
		Vector2f anchorBR = new Vector2f(options.getRightBottomAnchor());
		posBR = anchorBR.add(posBR);

		float deltaX = (posBR.x - posLT.x);
		float deltaY = (posBR.y - posLT.y);

		Vector3f meshPosition = meshTransform.getPosition();
		Vector3f meshScale = meshTransform.getScale();

		meshPosition.x = (posLT.x + deltaX * 0.5f);
		meshPosition.y = (posLT.y + deltaY * 0.5f);

		Vector2f screenSize = new Vector2f(deltaX, -deltaY); // the - is compensate for default quad already turning coordinates to support neutral scale image viewing

		UILayoutFit imageFit = options.getFit();
		if (mesh instanceof Image) {

			Image image = (Image) mesh;

			// apply image fit rules
			if (imageFit != UILayoutFit.NONE) {
				Texture t = image.getTexture();
				Vector2f texSize = new Vector2f(t.getDimension());
				float textRatio = texSize.x / texSize.y;
				float displayRatio = displaySize.x / displaySize.y;

				if (imageFit == FIT_X) {

					screenSize.x = screenSize.y * textRatio / displayRatio;
				} else if (imageFit == FIT_Y) {

					screenSize.y = screenSize.x * displayRatio / textRatio;
				} else if (imageFit == FIT_BOTH) {
					// @todo fit in both directions
				}
			}

			// set screen uvs
			if (image.isScreenUvs()) {

				float leftUv = (meshPosition.x - (screenSize.x * 0.5f)) * 0.5f + 0.5f;
				float rightUv = (meshPosition.x + (screenSize.x * 0.5f)) * 0.5f + 0.5f;

				float topUv = (meshPosition.y + (screenSize.y * 0.5f)) * 0.5f + 0.5f;
				float bottomUv = (meshPosition.y - (screenSize.y * 0.5f)) * 0.5f + 0.5f;

				image.setUvs(leftUv, topUv, rightUv, bottomUv);
			}
		}

		meshScale.x = screenSize.x;
		meshScale.y = screenSize.y;

		if (mesh instanceof Panel) {
			Panel panel = (Panel) mesh;

			panel.getDimensionUI().setValue(screenSize.x * displaySize.x * 0.5f, screenSize.y * displaySize.y * 0.5f);
		}

		meshTransform.setDirty(true);
		mesh.setModelMatrixDirty(true);
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public Vector2Data getDimension()
	{
		return dimension;
	}

	public void setDimension(Vector2Data dimension)
	{
		this.dimension = dimension;
	}
	// "Getters/Setters" </editor-fold>	
}
