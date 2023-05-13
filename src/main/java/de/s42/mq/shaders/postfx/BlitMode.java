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
package de.s42.mq.shaders.postfx;

/**
 *
 * @author Benjamin Schiller
 */
public enum BlitMode
{
	DEFAULT(0),
	RED_CHANNEL(1),
	GREEN_CHANNEL(2),
	BLUE_CHANNEL(3),
	ALPHA_CHANNEL(4),
	LUMINANCE(5),
	INVERTED(6),
	LINEAR_COLORS(7),
	JUST_GAMMA(8),
	TONEMAP_ACES(9);

	public final int blitModeId;
	private static int maxBlitModeId = -1;

	BlitMode(int blitModeId)
	{
		this.blitModeId = blitModeId;
	}

	public static BlitMode valueOf(int blitModeId)
	{
		for (BlitMode mode : BlitMode.values()) {
			if (mode.blitModeId == blitModeId) {
				return mode;
			}
		}

		throw new IllegalArgumentException("Blit mode id " + blitModeId + " is not a valid BlitMode");
	}

	public synchronized static int getMaxBlitModeId()
	{
		if (maxBlitModeId == -1) {
			for (BlitMode mode : BlitMode.values()) {
				maxBlitModeId = Math.max(maxBlitModeId, mode.blitModeId);
			}
		}

		return maxBlitModeId;
	}

	public BlitMode getNextBlitMode()
	{
		return getNextBlitMode(this);
	}

	public static BlitMode getNextBlitMode(BlitMode mode)
	{
		return valueOf((mode.blitModeId + 1) % (getMaxBlitModeId() + 1));
	}

	public BlitMode getPreviousBlitMode()
	{
		return getPreviousBlitMode(this);
	}

	public static BlitMode getPreviousBlitMode(BlitMode mode)
	{
		int m = mode.blitModeId - 1;
		if (m < 0) {
			m = getMaxBlitModeId();
		}
		return valueOf(m);
	}
}
