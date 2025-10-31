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

import de.s42.dl.DLInstance;
import de.s42.dl.DLModule;
import de.s42.dl.DLType;
import de.s42.dl.annotations.instances.ExportDLAnnotation;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.types.DefaultDLType;
import de.s42.dl.types.collections.MapDLType;
import de.s42.dl.types.primitive.ObjectDLType;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.MQColor;
import de.s42.mq.assets.*;
import de.s42.mq.buffers.FXBuffer;
import de.s42.mq.buffers.FrameBuffer;
import de.s42.mq.buffers.GBuffer;
import de.s42.mq.cameras.Camera;
import de.s42.mq.cameras.OrthographicCamera;
import de.s42.mq.cameras.PerspectiveCamera;
import de.s42.mq.core.AbstractEntity;
import de.s42.mq.core.Entity;
import de.s42.mq.data.*;
import de.s42.mq.debug.Log;
import de.s42.mq.debug.LogLevel;
import de.s42.mq.dl.types.ColorDLType;
import de.s42.mq.dl.types.Vector2DLType;
import de.s42.mq.dl.types.Vector3DLType;
import de.s42.mq.dl.types.Vector4DLType;
import de.s42.mq.editor.AbstractDataEditor;
import de.s42.mq.editor.DataEditor;
import de.s42.mq.editor.dataeditors.*;
import de.s42.mq.fonts.Text.HorizontalAlignment;
import de.s42.mq.fonts.Text.VerticalAlignment;
import de.s42.mq.fonts.*;
import de.s42.mq.i18n.L10N;
import de.s42.mq.i18n.Language;
import de.s42.mq.i18n.LocalizedStringData;
import de.s42.mq.input.InputAxis.InputAxisOverflowMode;
import de.s42.mq.input.*;
import de.s42.mq.input.keys.KeyInputAxis;
import de.s42.mq.input.keys.KeyInputKey;
import de.s42.mq.input.mouse.MouseButtonKey;
import de.s42.mq.input.mouse.MousePositionHandler;
import de.s42.mq.input.mouse.MouseScrollWheelKey;
import de.s42.mq.input.mouse.MouseScrollWheelKey.ScrollKey;
import de.s42.mq.input.mouse.ScrollInputAxis;
import de.s42.mq.loaders.fbx.FbxMesh;
import de.s42.mq.loaders.obj.ObjMesh;
import de.s42.mq.materials.*;
import de.s42.mq.materials.Texture.TextureFiltering;
import de.s42.mq.materials.Texture.TextureFormat;
import de.s42.mq.materials.Texture.TextureType;
import de.s42.mq.materials.Texture.TextureWrap;
import de.s42.mq.meshes.*;
import de.s42.mq.prefabs.BasePrefabLoader;
import de.s42.mq.prefabs.LoadPrefabTask;
import de.s42.mq.prefabs.Prefab;
import de.s42.mq.prefabs.PrefabLoader;
import de.s42.mq.rendering.*;
import de.s42.mq.scenes.RunScenes;
import de.s42.mq.scenes.Scene;
import de.s42.mq.scenes.SceneTransition;
import de.s42.mq.scenes.Scenes;
import de.s42.mq.shaders.Shader.BlendFunc;
import de.s42.mq.shaders.Shader.CullType;
import de.s42.mq.shaders.Shader.DepthFunc;
import de.s42.mq.shaders.*;
import de.s42.mq.shaders.postfx.BlitMode;
import de.s42.mq.sound.*;
import de.s42.mq.tasks.*;
import de.s42.mq.ui.*;
import de.s42.mq.ui.actions.UIAction;
import de.s42.mq.ui.animations.FadeTintOnMouseOver;
import de.s42.mq.ui.layout.Layout;
import de.s42.mq.ui.layout.LayoutOptions;
import de.s42.mq.ui.layout.uilayout.UILayout;
import de.s42.mq.ui.layout.uilayout.UILayoutFit;
import de.s42.mq.ui.layout.uilayout.UILayoutOptions;
import de.s42.mq.ui.textfield.TextFieldCaretAnimation;
import de.s42.mq.ui.textfield.TextFieldFocusAnimation;
import de.s42.mq.ui.textfield.Textfield;
import de.s42.mq.util.Transform;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.joml.*;

/**
 *
 * @author Benjamin Schiller
 */
public class MQDLCore extends DefaultCore
{

	private final static Logger log = LogManager.getLogger(MQDLCore.class.getName());

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
		defineType(createType(Worker.class));
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
		defineType(Key.class, "Key");
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
		defineType(createType(PlainShader.class), "PlainShader");
		defineType(createType(BasicShader.class), "BasicShader");
		defineType(createType(BasicFXShader.class), "BasicFXShader");
		defineType(createType(CombineFXShader.class), "CombineFXShader");
		defineType(createType(EquirectangularToCubemapShader.class), "EquirectangularToCubemapShader");
		defineType(createType(HighPassFilterShader.class), "HighPassFilterShader");
		defineType(createType(BlurShader.class), "BlurShader");
		defineType(createType(TaaShader.class), "TaaShader");
		defineType(createType(SkyShader.class), "SkyShader");
		defineType(createType(DenoiseShader.class), "DenoiseShader");
		defineType(createType(UpscaleShader.class), "UpscaleShader");
		defineType(createType(SSLFShader.class), "SSLFShader");
		//defineType(createType(PostFXShader.class), "PostFXShader");
		//defineType(createType(ParticlesShader.class), "ParticlesShader");
		defineType(createType(SSAOShader.class), "SSAOShader");
		defineType(createType(PBRShader.class), "PBRShader");
		defineType(createType(FogShader.class), "FogShader");
		defineType(createType(SSRRShader.class), "SSRRShader");

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
		defineType(PrefabLoader.class, "PrefabLoader");
		defineType(BasePrefabLoader.class, "BasePrefabLoader");
		defineType(Prefab.class, "Prefab");
		defineType(LoadPrefabTask.class, "LoadPrefabTask");

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
		defineType(UIComponent.class, "UIComponent");
		defineType(AbstractUIComponent.class, "AbstractUIComponent");
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

		// Textfield
		defineType(Textfield.class, "Textfield");
		defineType(TextFieldFocusAnimation.class, "TextFieldFocusAnimation");
		defineType(TextFieldCaretAnimation.class, "TextFieldCaretAnimation");

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

		// Tasks
		defineType(createType(RenderShaderTask.class), "RenderShaderTask");
		defineType(createType(RenderFXShaderTask.class), "RenderFXShaderTask");
		defineType(createType(RenderCombineFXShaderTask.class), "RenderCombineFXShaderTask");

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

		// Parse config if given
		if (config != null) {
			DLModule configModule = parse(config.toString());

			// Print debug of all exported config children
			for (DLInstance child : configModule.getChildren()) {
				if (child.hasAnnotation(ExportDLAnnotation.class)) {
					log.debug("Config exported", de.s42.dl.util.DLHelper.toString(child));
				}
			}
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
