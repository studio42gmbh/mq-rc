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
package de.s42.mq.rc;

import de.s42.base.files.FilesHelper;
import de.s42.base.modules.ModuleHelper;
import de.s42.dl.exceptions.DLException;
import de.s42.mq.dl.MQDLConfig;
import de.s42.mq.dl.MQDLCore;
import static de.s42.mq.rc.Version.getVersion;
import de.s42.mq.tasks.TaskManager;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.awt.Toolkit;

/**
 *
 * @author Benjamin Schiller
 */
public class App
{

	private final static Logger log = LogManager.getLogger(App.class.getName());

	public final static String COMMANDLINE_ARGUMENT_CONFIG = "config";
	public final static String COMMANDLINE_ARGUMENT_APP = "app";

	@SuppressWarnings({"CallToPrintStackTrace", "UseSpecificCatch", "AssertWithSideEffects"})
	public static void run(MQDLCore core, String... args) throws IOException, DLException
	{
		boolean assertsEnabled = false;

		assert (assertsEnabled = true);

		log.info("Starting MerQry Rich Client", getVersion());

		log.info("OS name", System.getProperty("os.name"));
		log.info("OS version", System.getProperty("os.version"));
		log.info("LWJGL version", org.lwjgl.Version.getVersion());
		log.info("Base Path", Path.of(".").toAbsolutePath().toString());
		log.info("Asserts (-ea)", assertsEnabled);
		log.info("Arguments", Arrays.toString(args));
		log.info("Screen resolution", Toolkit.getDefaultToolkit().getScreenResolution());

		Map<String, String> arguments = ModuleHelper.parseArguments(args);

		// Load -app:...
		if (!arguments.containsKey(COMMANDLINE_ARGUMENT_APP)) {
			throw new IllegalArgumentException("Missing commandline argument 'app'");
		}

		Path appFile = Path.of(arguments.get(COMMANDLINE_ARGUMENT_APP));

		if (!appFile.toFile().isFile()) {
			throw new IllegalArgumentException("app file is not a regular file: " + appFile.toAbsolutePath().toString());
		}

		log.info("App File {}", FilesHelper.createMavenNetbeansFileConsoleLink(appFile));

		Path configFile = null;

		// Load config if given -config: ...
		if (arguments.containsKey(COMMANDLINE_ARGUMENT_CONFIG)) {

			configFile = Path.of(arguments.get(COMMANDLINE_ARGUMENT_CONFIG));

			if (!configFile.toFile().isFile()) {
				throw new IllegalArgumentException("config file is not a regular file: " + configFile.toAbsolutePath().toString());
			}

			log.info("Config File {}", FilesHelper.createMavenNetbeansFileConsoleLink(configFile));
		}

		MQDLConfig config = core.parseApp(configFile, appFile, arguments);

		TaskManager taskManager = config.getTaskManager();

		taskManager.load();

		taskManager.schedule(config.getTasks());

		taskManager.runMainThread();

		taskManager.unload();

		log.info("Bye");
	}
}
