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
package de.s42.mq.dl;

import de.s42.dl.*;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.types.*;
import de.s42.dl.types.collections.MapDLType;
import de.s42.dl.types.primitive.ObjectDLType;
import de.s42.mq.MQColor;
import de.s42.mq.assets.AbstractAsset;
import de.s42.mq.assets.AbstractAssetsTask;
import de.s42.mq.assets.Asset;
import de.s42.mq.assets.AssetManager;
import de.s42.mq.assets.Assets;
import de.s42.mq.assets.LoadAssetTask;
import de.s42.mq.assets.LoadAssetsTask;
import de.s42.mq.assets.UnloadAssetTask;
import de.s42.mq.assets.UnloadAssetsTask;
import de.s42.mq.assets.UpdateUpdateable;
import de.s42.mq.assets.Updateable;
import de.s42.mq.buffers.FXBuffer;
import de.s42.mq.buffers.FrameBuffer;
import de.s42.mq.buffers.GBuffer;
import de.s42.mq.cameras.Camera;
import de.s42.mq.cameras.OrthographicCamera;
import de.s42.mq.cameras.PerspectiveCamera;
import de.s42.mq.core.AbstractEntity;
import de.s42.mq.core.Entity;
import de.s42.mq.data.AbstractData;
import de.s42.mq.data.AbstractNumberData;
import de.s42.mq.data.BooleanData;
import de.s42.mq.data.ColorData;
import de.s42.mq.data.Data;
import de.s42.mq.data.FloatData;
import de.s42.mq.data.IntegerData;
import de.s42.mq.data.LongData;
import de.s42.mq.data.PathData;
import de.s42.mq.data.StringData;
import de.s42.mq.data.Vector2Data;
import de.s42.mq.data.Vector3Data;
import de.s42.mq.data.Vector4Data;
import de.s42.mq.debug.Log;
import de.s42.mq.debug.LogLevel;
import de.s42.mq.dl.types.*;
import de.s42.mq.editor.AbstractDataEditor;
import de.s42.mq.editor.DataEditor;
import de.s42.mq.editor.dataeditors.BooleanDataEditor;
import de.s42.mq.editor.dataeditors.ColorDataEditor;
import de.s42.mq.editor.dataeditors.FloatDataEditor;
import de.s42.mq.editor.dataeditors.IntegerDataEditor;
import de.s42.mq.editor.dataeditors.StringDataEditor;
import de.s42.mq.editor.dataeditors.Vector2DataEditor;
import de.s42.mq.editor.dataeditors.Vector3DataEditor;
import de.s42.mq.editor.dataeditors.Vector4DataEditor;
import de.s42.mq.fonts.Font;
import de.s42.mq.fonts.Fonts;
import de.s42.mq.fonts.Glyph;
import de.s42.mq.fonts.GlyphPage;
import de.s42.mq.fonts.Text;
import de.s42.mq.fonts.Text.HorizontalAlignment;
import de.s42.mq.fonts.Text.VerticalAlignment;
import de.s42.mq.fonts.TextOptions;
import de.s42.mq.i18n.L10N;
import de.s42.mq.i18n.Language;
import de.s42.mq.i18n.LocalizedStringData;
import de.s42.mq.input.AbstractInputAxis;
import de.s42.mq.input.AbstractInputKey;
import de.s42.mq.input.AbstractInputTask;
import de.s42.mq.input.Input;
import de.s42.mq.input.InputAxis;
import de.s42.mq.input.InputAxis.InputAxisOverflowMode;
import de.s42.mq.input.InputHandler;
import de.s42.mq.input.InputKey;
import de.s42.mq.input.InputTask;
import de.s42.mq.input.PrepareInputTask;
import de.s42.mq.input.keys.KeyInputAxis;
import de.s42.mq.input.keys.KeyInputKey;
import de.s42.mq.input.mouse.MouseButtonKey;
import de.s42.mq.input.mouse.MousePositionHandler;
import de.s42.mq.input.mouse.MouseScrollWheelKey;
import de.s42.mq.input.mouse.MouseScrollWheelKey.ScrollKey;
import de.s42.mq.input.mouse.ScrollInputAxis;
import de.s42.mq.loaders.fbx.FbxMesh;
import de.s42.mq.loaders.obj.ObjMesh;
import de.s42.mq.materials.CubeTexture;
import de.s42.mq.materials.Material;
import de.s42.mq.materials.PBRMaterial;
import de.s42.mq.materials.ShaderMaterial;
import de.s42.mq.materials.Texture;
import de.s42.mq.materials.Texture.TextureFiltering;
import de.s42.mq.materials.Texture.TextureFormat;
import de.s42.mq.materials.Texture.TextureType;
import de.s42.mq.materials.Texture.TextureWrap;
import de.s42.mq.meshes.AbstractMeshAnimation;
import de.s42.mq.meshes.Cube;
import de.s42.mq.meshes.Mesh;
import de.s42.mq.meshes.MeshAnimation;
import de.s42.mq.meshes.MeshGroup;
import de.s42.mq.meshes.MeshReference;
import de.s42.mq.meshes.Particles;
import de.s42.mq.meshes.Quad;
import de.s42.mq.meshes.ScreenQuad;
import de.s42.mq.meshes.Sphere;
import de.s42.mq.meshes.UpdateMeshes;
import de.s42.mq.prefabs.Prefab;
import de.s42.mq.rendering.ClearBufferTask;
import de.s42.mq.rendering.RenderMeshesTask;
import de.s42.mq.scenes.RunScenes;
import de.s42.mq.scenes.Scene;
import de.s42.mq.scenes.SceneTransition;
import de.s42.mq.scenes.Scenes;
import de.s42.mq.shaders.BasicShader;
import de.s42.mq.shaders.SSRRShader;
import de.s42.mq.shaders.Shader;
import de.s42.mq.shaders.Shader.BlendFunc;
import de.s42.mq.shaders.Shader.CullType;
import de.s42.mq.shaders.Shader.DepthFunc;
import de.s42.mq.shaders.postfx.BlitMode;
import de.s42.mq.sound.AudioClip;
import de.s42.mq.sound.Sound;
import de.s42.mq.sound.SoundReceiver;
import de.s42.mq.sound.Sounds;
import de.s42.mq.sound.UpdateSound;
import de.s42.mq.sound.UpdateSoundReceiver;
import de.s42.mq.tasks.AbstractTask;
import de.s42.mq.tasks.DebouncedSequenceTask;
import de.s42.mq.tasks.DefaultTaskManager;
import de.s42.mq.tasks.ReferenceTask;
import de.s42.mq.tasks.SequenceTask;
import de.s42.mq.tasks.SetData;
import de.s42.mq.tasks.Task;
import de.s42.mq.tasks.TaskManager;
import de.s42.mq.tasks.TaskQueue;
import de.s42.mq.tasks.Tasks;
import de.s42.mq.ui.AbstractWindowTask;
import de.s42.mq.ui.ActivateForRenderingTask;
import de.s42.mq.ui.Button;
import de.s42.mq.ui.CloseWindowTask;
import de.s42.mq.ui.ComponentBackgroundShader;
import de.s42.mq.ui.Cursor;
import de.s42.mq.ui.Cursors;
import de.s42.mq.ui.HandleWindowEventsTask;
import de.s42.mq.ui.Image;
import de.s42.mq.ui.ImageOptions;
import de.s42.mq.ui.Panel;
import de.s42.mq.ui.PanelOptions;
import de.s42.mq.ui.PrepareWindowTask;
import de.s42.mq.ui.SwapBufferWindowTask;
import de.s42.mq.ui.UIComponent;
import de.s42.mq.ui.UIManager;
import de.s42.mq.ui.UIShader;
import de.s42.mq.ui.Window;
import de.s42.mq.ui.WindowTask;
import de.s42.mq.ui.actions.UIAction;
import de.s42.mq.ui.animations.FadeTintOnMouseOver;
import de.s42.mq.ui.layout.Layout;
import de.s42.mq.ui.layout.LayoutOptions;
import de.s42.mq.ui.layout.uilayout.UILayout;
import de.s42.mq.ui.layout.uilayout.UILayoutFit;
import de.s42.mq.ui.layout.uilayout.UILayoutOptions;
import de.s42.mq.util.Transform;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 *
 * @author Benjamin Schiller
 */
public class MQDLCore extends DefaultCore
{

	public final static String ARGUMENTS_IDENTIFIER = "arguments";

	public final static String WINDOW_TYPE_SYMBOL = "Window";
	public final static String TASK_TYPE_SYMBOL = "Task";
	public final static String TASKS_TYPE_SYMBOL = "Tasks";
	public final static String TASK_MANAGER_TYPE_SYMBOL = "TaskManager";
	public final static String CORE_DL_RESOURCE = "de/s42/mq/dl/core.dl";

	private boolean inited;

	public MQDLCore()
	{
	}

	public void init() throws IOException, DLException
	{
		// Annotations
		//redefineAnnotation(new MQIsFileDLAnnotation());
		//defineAnnotation(new MinDLAnnotation());
		//defineAnnotation(new MaxDLAnnotation());
		//defineAnnotation(new StepDLAnnotation());
		//defineAnnotation(new EditableDLAnnotation());
		//defineAnnotation(new InDegreesDLAnnotation());
		//defineAnnotation(new InRadiansDLAnnotation());
		//defineAnnotation(new InWorldUnitsDLAnnotation());
		//defineAnnotation(new InSecondsDLAnnotation());
		//defineAnnotation(new InScreenSpaceDLAnnotation());

		// Basic Data Types
		DLType objectType = getType(Object.class).orElseThrow();
		defineType(new Vector2DLType(objectType), Vector2f.class.getName(), "vec2");
		defineType(new Vector3DLType(objectType), Vector3f.class.getName(), "vec3");
		defineType(new Vector4DLType(objectType), Vector4f.class.getName(), "vec4");
		defineType(new ColorDLType(objectType), "MQColor", MQColor.class.getName());
		defineType(createType(AtomicInteger.class));
		defineType(createType(Matrix4f.class));
		defineType(createType(Quaternionf.class));
		defineType(createType(Transform.class));

		// Enum
		defineType(createEnum(CullType.class), "CullType");
		defineType(createEnum(BlendFunc.class), "BlendFunc");
		defineType(createEnum(TextureWrap.class), "TextureWrap");
		defineType(createEnum(TextureFormat.class), "TextureFormat");
		defineType(createEnum(TextureType.class), "TextureType");
		defineType(createEnum(TextureFiltering.class), "TextureFiltering");
		defineType(createEnum(ScrollKey.class), "ScrollKey");
		defineType(createEnum(InputAxisOverflowMode.class), "InputAxisOverflowMode");
		defineType(createEnum(BlitMode.class), "BlitMode");
		defineType(createEnum(VerticalAlignment.class), "VerticalAlignment");
		defineType(createEnum(HorizontalAlignment.class), "HorizontalAlignment");
		defineType(createEnum(UILayoutFit.class), "UILayoutFit");
		defineType(createEnum(DepthFunc.class), "DepthFunc");

		// Base
		defineType(createType(Entity.class), "Entity");
		defineType(createType(AbstractEntity.class), "AbstractEntity");

		// Data
		defineType(createType(Data.class), "Data");
		defineType(createType(AbstractData.class), "AbstractData");
		defineType(createType(AbstractNumberData.class), "AbstractNumberData");
		defineType(createType(FloatData.class), "FloatData");
		defineType(createType(BooleanData.class), "BooleanData");
		defineType(createType(StringData.class), "StringData");
		defineType(createType(IntegerData.class), "IntegerData");
		defineType(createType(LongData.class), "LongData");
		defineType(createType(ColorData.class), "ColorData");
		defineType(createType(Vector2Data.class), "Vector2Data");
		defineType(createType(Vector3Data.class), "Vector3Data");
		defineType(createType(Vector4Data.class), "Vector4Data");
		defineType(createType(PathData.class), "PathData");

		// Assets
		defineType(createType(AssetManager.class), "AssetManager");
		defineType(createType(Updateable.class), "Updateable");
		defineType(createType(Asset.class), "Asset");
		defineType(createType(AbstractAsset.class), "AbstractAsset");
		defineType(createType(Assets.class), "Assets");

		// Tasks
		defineType(createType(Task.class), "Task");
		defineType(createType(AbstractTask.class), "AbstractTask");
		defineType(createType(ReferenceTask.class), "ReferenceTask");
		defineType(createType(Tasks.class), "Tasks");
		defineType(createType(TaskManager.class), "TaskManager");
		defineType(createType(DefaultTaskManager.class), "DefaultTaskManager");
		defineType(createType(SequenceTask.class), "SequenceTask");
		defineType(createType(DebouncedSequenceTask.class), "DebouncedSequenceTask");

		// Specific Tasks
		defineType(createType(SetData.class), "SetData");
		defineType(createType(AbstractAssetsTask.class), "AbstractAssetsTask");
		defineType(createType(LoadAssetTask.class), "LoadAssetTask");
		defineType(createType(UnloadAssetTask.class), "UnloadAssetTask");
		defineType(createType(LoadAssetsTask.class), "LoadAssetsTask");
		defineType(createType(UnloadAssetsTask.class), "UnloadAssetsTask");
		defineType(createType(UpdateUpdateable.class), "UpdateUpdateable");
		defineType(createType(TaskQueue.class), "TaskQueue");

		// Debug
		defineType(createType(LogLevel.class), "LogLevel");
		defineType(createType(Log.class), "Log");

		// Inputs
		defineType(createType(InputAxis.class), "InputAxis");
		defineType(createType(AbstractInputAxis.class), "AbstractInputAxis");
		defineType(createType(InputKey.class), "InputKey");
		defineType(createType(AbstractInputKey.class), "AbstractInputKey");
		defineType(createType(InputHandler.class), "InputHandler");
		defineType(createType(MousePositionHandler.class), "MousePositionHandler");
		defineType(createType(MouseButtonKey.class), "MouseButtonKey");
		defineType(createType(MouseScrollWheelKey.class), "MouseScrollWheelKey");
		defineType(createType(ScrollInputAxis.class), "ScrollInputAxis");
		defineType(createType(KeyInputAxis.class), "KeyInputAxis");
		defineType(createType(KeyInputKey.class), "KeyInputKey");
		defineType(createType(Input.class), "Input");
		defineType(createType(InputTask.class), "InputTask");
		defineType(createType(AbstractInputTask.class), "AbstractInputTask");

		// Cameras
		defineType(createType(Camera.class), "Camera");
		defineType(createType(PerspectiveCamera.class), "PerspectiveCamera");
		defineType(createType(OrthographicCamera.class), "OrthographicCamera");

		// Textures
		defineType(createType(Texture.class), "Texture");
		defineType(createType(CubeTexture.class), "CubeTexture");

		// Buffers
		defineType(createType(FrameBuffer.class), "FrameBuffer");
		defineType(createType(FXBuffer.class), "FXBuffer");
		defineType(createType(GBuffer.class), "GBuffer");
		defineType(createType(ClearBufferTask.class), "ClearBufferTask");

		// Shaders
		defineType(createType(Shader.class), "Shader");

		// Materials
		defineType(createType(Material.class), "Material");
		defineType(createType(ShaderMaterial.class), "ShaderMaterial");
		defineType(createType(PBRMaterial.class), "PBRMaterial");

		// Meshes
		defineType(createType(MeshAnimation.class), "MeshAnimation");
		defineType(createType(Mesh.class), "Mesh");
		defineType(createType(MeshReference.class), "MeshReference");
		defineType(createType(MeshGroup.class), "MeshGroup");
		defineType(createType(Quad.class), "Quad");
		defineType(createType(ScreenQuad.class), "ScreenQuad");
		defineType(createType(Particles.class), "Particles");
		defineType(createType(ObjMesh.class), "ObjMesh");
		defineType(createType(FbxMesh.class), "FbxMesh");
		defineType(createType(Sphere.class), "Sphere");
		defineType(createType(Cube.class), "Cube");
		defineType(createType(AbstractMeshAnimation.class), "AbstractMeshAnimation");
		defineType(createType(UpdateMeshes.class), "UpdateMeshes");

		// Prefabs
		defineType(createType(Prefab.class), "Prefab");

		// Editors
		defineType(createType(DataEditor.class), "DataEditor");
		defineType(createType(AbstractDataEditor.class), "AbstractDataEditor");
		defineType(createType(FloatDataEditor.class), "FloatDataEditor");
		defineType(createType(ColorDataEditor.class), "ColorDataEditor");
		defineType(createType(BooleanDataEditor.class), "BooleanDataEditor");
		defineType(createType(IntegerDataEditor.class), "IntegerDataEditor");
		defineType(createType(StringDataEditor.class), "StringDataEditor");
		defineType(createType(Vector2DataEditor.class), "Vector2DataEditor");
		defineType(createType(Vector3DataEditor.class), "Vector3DataEditor");
		defineType(createType(Vector4DataEditor.class), "Vector4DataEditor");

		// Layouts
		defineType(createType(Layout.class), "Layout");
		defineType(createType(LayoutOptions.class), "LayoutOptions");
		defineType(createType(UILayout.class), "UILayout");
		defineType(createType(UILayoutOptions.class), "UILayoutOptions");

		// Fonts
		defineType(createType(Glyph.class), "Glyph");
		defineType(createType(GlyphPage.class), "GlyphPage");
		defineType(createType(Font.class), "Font");
		defineType(createType(Fonts.class), "Fonts");

		// L10Ns
		defineType(createType(Language.class), "Language");
		defineType(createType(L10N.class), "L10N");
		defineType(createType(LocalizedStringData.class), "LocalizedStringData", "L10NStringData");

		// UI
		defineType(createType(UIAction.class), "UIAction");
		defineType(createType(UIComponent.class), "UIComponent");
		defineType(createType(UIManager.class), "UIManager");
		defineType(createType(PanelOptions.class), "PanelOptions");
		defineType(createType(Panel.class), "Panel");
		defineType(createType(ImageOptions.class), "ImageOptions");
		defineType(createType(Image.class), "Image");
		defineType(createType(TextOptions.class), "TextOptions");
		defineType(createType(Text.class), "Text");
		defineType(createType(Button.class), "Button");
		defineType(createType(Cursor.class), "Cursor");
		defineType(createType(Cursors.class), "Cursors");
		defineType(createType(Window.class), "Window");
		defineType(createType(WindowTask.class), "WindowTask");
		defineType(createType(AbstractWindowTask.class), "AbstractWindowTask");
		defineType(createType(PrepareWindowTask.class), "PrepareWindowTask");
		defineType(createType(ActivateForRenderingTask.class), "ActivateForRenderingTask");
		defineType(createType(HandleWindowEventsTask.class), "HandleWindowEventsTask");
		defineType(createType(CloseWindowTask.class), "CloseWindowTask");
		defineType(createType(SwapBufferWindowTask.class), "SwapBufferWindowTask");
		defineType(createType(PrepareInputTask.class), "PrepareInputTask");
		defineType(createType(RenderMeshesTask.class), "RenderMeshesTask");
		defineType(createType(FadeTintOnMouseOver.class), "FadeTintOnMouseOver");
		defineType(createType(UIShader.class), "UIShader");
		defineType(createType(ComponentBackgroundShader.class), "ComponentBackgroundShader");

		// Scenes
		defineType(createType(Scene.class), "Scene");
		defineType(createType(SceneTransition.class), "SceneTransition");
		defineType(createType(Scenes.class), "Scenes");
		defineType(createType(RunScenes.class), "RunScenes");

		// Sound
		defineType(createType(AudioClip.class), "AudioClip");
		defineType(createType(Sound.class), "Sound");
		defineType(createType(SoundReceiver.class), "SoundReceiver");
		defineType(createType(Sounds.class), "Sounds");
		defineType(createType(UpdateSoundReceiver.class), "UpdateSoundReceiver");
		defineType(createType(UpdateSound.class), "UpdateSound");

		// Default Shaders
		defineType(createType(SSRRShader.class), "SSRRShader");
		defineType(createType(BasicShader.class), "BasicShader");

		// Data Maps
		DLInstance colors = createInstance(getType(MapDLType.DEFAULT_SYMBOL).orElseThrow(), "Colors");
		colors.set("WHITE", MQColor.White);
		colors.set("BLACK", MQColor.Black);
		addExported(colors);

		DLInstance math = createInstance(getType(MapDLType.DEFAULT_SYMBOL).orElseThrow(), "Math");
		math.set("PIHALF", 1.5707963267948966192313216916398);
		math.set("PI", 3.1415926535897932384626433832795);
		math.set("TAU", 6.283185307179586476925286766559);
		math.set("NPIHALF", -1.5707963267948966192313216916398);
		math.set("NPI", -3.1415926535897932384626433832795);
		math.set("NTAU", -6.283185307179586476925286766559);
		math.set("ONEDB1", 1.0 / 1.0);
		math.set("ONEDB2", 1.0 / 2.0);
		math.set("ONEDB4", 1.0 / 4.0);
		math.set("ONEDB8", 1.0 / 8.0);
		math.set("ONEDB16", 1.0 / 16.0);
		math.set("ONEDB32", 1.0 / 32.0);
		math.set("ONEDB64", 1.0 / 64.0);
		math.set("ONEDB128", 1.0 / 128.0);
		math.set("ONEDB256", 1.0 / 256.0);
		math.set("ONEDB512", 1.0 / 512.0);
		addExported(math);

		// Dynamic Type
		DefaultDLType dynamicType = (DefaultDLType) createType("Dynamic");
		dynamicType.addParent(getType(ObjectDLType.DEFAULT_SYMBOL).orElseThrow());
		dynamicType.addContainedType(getType(ObjectDLType.DEFAULT_SYMBOL).orElseThrow());
		dynamicType.setDynamic(true);
		//dynamicType.addAnnotation(getAnnotation(DynamicDLAnnotation.DEFAULT_SYMBOL).orElseThrow());
		//getAnnotation(DynamicDLAnnotation.DEFAULT_SYMBOL).orElseThrow().bindToType(this, dynamicType);
		defineType(dynamicType);

		//defineType(createType(.class), "");
		parse(CORE_DL_RESOURCE);

		inited = true;
	}

	protected void addArgsInstance(Map<String, String> arguments) throws DLException
	{
		DLInstance args = createInstance(getType(MapDLType.DEFAULT_SYMBOL).get(), ARGUMENTS_IDENTIFIER);
		for (Map.Entry<String, String> argument : arguments.entrySet()) {
			args.set(argument.getKey(), argument.getValue());
		}
		addExported(args);
	}

	public MQDLConfig parseApp(Path app) throws IOException, DLException
	{
		return parseApp(null, app, null);
	}

	public MQDLConfig parseApp(Path app, Map<String, String> arguments) throws IOException, DLException
	{
		return parseApp(null, app, arguments);
	}

	public MQDLConfig parseApp(Path config, Path app, Map<String, String> arguments) throws IOException, DLException
	{
		assert app != null;
		assert Files.isRegularFile(app);
		assert isInited();

		if (arguments != null) {
			addArgsInstance(arguments);
		}

		// parse config if given
		if (config != null) {
			parse(config.toString());
		}

		return new MQDLConfig(this, parse(app.toString()));
	}

	public DLType getWindowType() throws DLException
	{
		return getType(WINDOW_TYPE_SYMBOL).orElseThrow();
	}

	public DLType getTaskType() throws DLException
	{
		return getType(TASK_TYPE_SYMBOL).orElseThrow();
	}

	public DLType getTasksType() throws DLException
	{
		return getType(TASKS_TYPE_SYMBOL).orElseThrow();
	}

	public DLType getTaskManagerType() throws DLException
	{
		return getType(TASK_MANAGER_TYPE_SYMBOL).orElseThrow();
	}

	public boolean isInited()
	{
		return inited;
	}
}
