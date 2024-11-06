/*
 * Copyright Studio 42 GmbH 2020. All rights reserved.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For details to the License read https://www.s42m.de/license
 */

module de.sft.mq.rc
{
	requires java.desktop;
	requires de.sft.log;
	requires de.sft.base;
	requires org.antlr.antlr4.runtime;
	requires de.sft.dl;
	requires org.lwjgl;
	requires org.lwjgl.glfw;
	requires org.lwjgl.opengl;
	requires org.lwjgl.opengl.natives;
	requires org.lwjgl.stb;
	requires org.lwjgl.assimp;
	requires org.lwjgl.openal;
	requires org.joml;

	exports de.s42.mq;
	exports de.s42.mq.assets;
	exports de.s42.mq.buffers;
	exports de.s42.mq.cameras;
	exports de.s42.mq.core;
	exports de.s42.mq.data;
	exports de.s42.mq.debug;
	exports de.s42.mq.dl;
	exports de.s42.mq.dl.types;
	exports de.s42.mq.editor;
	exports de.s42.mq.editor.dataeditors;
	exports de.s42.mq.fonts;
	exports de.s42.mq.fonts.fnt;
	exports de.s42.mq.i18n;
	exports de.s42.mq.input;
	exports de.s42.mq.input.keys;
	exports de.s42.mq.input.mouse;
	exports de.s42.mq.loaders.obj;
	exports de.s42.mq.loaders.fbx;
	exports de.s42.mq.materials;
	exports de.s42.mq.meshes;
	exports de.s42.mq.rc;
	exports de.s42.mq.rendering;
	exports de.s42.mq.shaders;
	exports de.s42.mq.shaders.postfx;
	exports de.s42.mq.scenes;
	exports de.s42.mq.sound;
	exports de.s42.mq.tasks;
	exports de.s42.mq.ui;
	exports de.s42.mq.ui.actions;
	exports de.s42.mq.ui.animations;
	exports de.s42.mq.ui.layout;
	exports de.s42.mq.ui.layout.uilayout;
	exports de.s42.mq.ui.textfield;
	exports de.s42.mq.util;
	exports de.s42.mq.dl.annotations;
	exports de.s42.mq.prefabs;

	opens de.s42.mq.dl;
}
