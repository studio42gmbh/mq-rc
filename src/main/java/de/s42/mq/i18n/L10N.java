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
package de.s42.mq.i18n;

import de.s42.base.files.FilesHelper;
import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.DLCore;
import de.s42.dl.DLInstance;
import de.s42.dl.DLModule;
import de.s42.dl.DLType;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.exceptions.InvalidValue;
import de.s42.dl.types.DLContainer;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.assets.AbstractAsset;
import de.s42.mq.data.StringData;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Benjamin Schiller
 */
public class L10N extends AbstractAsset implements DLContainer
{

	private final static Logger log = LogManager.getLogger(L10N.class.getName());

	@AttributeDL(required = false)
	protected String locale;

	@AttributeDL(required = false)
	protected String defaultLocale;

	@AttributeDL(required = false)
	protected String[] loadSubDirectories;

	@AttributeDL(required = false)
	protected DLCore core;

	protected AtomicInteger revision = new AtomicInteger(1);
	protected final Map<String, Object> l10ns = new HashMap<>();
	protected final List<L10N> subL10Ns = new ArrayList<>();
	protected final Map<String, Language> languages = new HashMap<>();

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		super.load();

		// set locale to default locale
		if (locale == null) {
			locale = defaultLocale;
		}

		log.debug("Loading L10N " + getName() + " locale: " + locale);

		// load sub directories
		if (loadSubDirectories != null) {

			PathMatcher dlMatcher = FileSystems.getDefault().getPathMatcher("glob:**l10n.dl");
			PathMatcher propertiesMatcher = FileSystems.getDefault().getPathMatcher("glob:**l10n_*.properties");
			DLType l10nType = core.getType("de.s42.mq.i18n.L10N").get();

			for (String subDirectory : loadSubDirectories) {

				Path subDirectoryPath = Path.of(subDirectory);

				log.debug("Load sub directory " + subDirectoryPath.toAbsolutePath().toString());

				try {
					Files.walk(subDirectoryPath).forEach((Path file) -> {

						//log.debug("Checking " + file.toAbsolutePath().toString());
						if (Files.isRegularFile(file)) {

							if (dlMatcher.matches(file)) {

								log.debug("Loading l10n file " + FilesHelper.createMavenNetbeansFileConsoleLink(file));

								try {
									DLModule l10nModule = core.parse(file.toString());

									List<DLInstance> l10nInstances = l10nModule.getChildren(l10nType);

									for (DLInstance l10nInstance : l10nInstances) {

										L10N l10n = (L10N) l10nInstance.toJavaObject();

										l10n.load();

										log.trace("Loaded sub l10n " + l10n.getName() + " locale: " + l10n.getLocale());

										addSubL10n(l10n);
									}
								} catch (DLException ex) {
									throw new RuntimeException("Could not load l10n file '" + file.toAbsolutePath().toString() + "' - " + ex.getMessage(), ex);
								}
							} else if (propertiesMatcher.matches(file)) {

								log.debug("Loading properties file " + FilesHelper.createMavenNetbeansFileConsoleLink(file));

								Properties prop = new Properties();
								try (InputStream in = Files.newInputStream(file)) {
									prop.load(in);

									String loc = getDefaultLocale();

									// infer locale from file name *._<locale>.properties
									String fileName = file.getFileName().toString();
									int uI = fileName.lastIndexOf("_");
									int uD = fileName.lastIndexOf(".");
									if (uI > 0 && uI < uD) {
										loc = fileName.substring(uI + 1, uD);
									}

									log.trace("Locale " + loc);

									L10N l10n = new L10N();
									l10n.setLocale(loc);

									for (Map.Entry<Object, Object> entry : prop.entrySet()) {
										String key = (String) entry.getKey();
										String value = (String) entry.getValue();

										l10n.addChild(key, new StringData(key, value));
									}

									addSubL10n(l10n);
								} catch (IOException ex) {
									throw new RuntimeException("Could not load properties " + file.toAbsolutePath().toString() + " - " + ex.getMessage(), ex);
								}

							}
						}
					});
				} catch (IOException ex) {
					throw new InvalidValue("Could not load sub directories - " + ex.getMessage(), ex);
				}
			}
		}
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		l10ns.clear();

		for (L10N subL10n : subL10Ns) {
			subL10n.unload();
		}

		revision.incrementAndGet();

		super.unload();
	}

	public Optional<Object> get(String id)
	{
		Object value = l10ns.get(id);

		if (value != null) {
			return Optional.of(value);
		}

		for (L10N subL10n : subL10Ns) {

			// just look into l10ns which are in the given locale
			if (subL10n.getLocale().equals(getLocale())) {

				Optional<Object> subValue = subL10n.get(id);

				if (subValue.isPresent()) {
					return subValue;
				}
			}
		}

		return Optional.empty();
	}

	public Language getLanguage()
	{
		return getLanguage(locale);
	}

	public Language getLanguage(String loc)
	{
		return languages.get(loc);
	}

	public void addSubL10n(L10N subL10N)
	{
		assert subL10N != null;

		subL10Ns.add(subL10N);

		for (Language language : subL10N.getLanguages()) {
			addLanguage(language);
		}

		revision.incrementAndGet();
	}

	public void removeSubL10n(L10N subL10N)
	{
		assert subL10N != null;

		subL10Ns.remove(subL10N);
		revision.incrementAndGet();
	}

	public void addLanguage(Language language)
	{
		assert language != null;

		languages.put(language.getLocale(), language);
		revision.incrementAndGet();
	}

	public void removeLanguage(Language language)
	{
		assert language != null;

		languages.remove(language.getLocale());
		revision.incrementAndGet();
	}

	public Collection<Language> getLanguages()
	{
		return languages.values();
	}

	@Override
	public void addChild(String name, Object child)
	{
		l10ns.put(name, child);

		if (child instanceof Language) {
			addLanguage((Language) child);
		}

		revision.incrementAndGet();
	}

	@Override
	public List getChildren()
	{
		return Collections.unmodifiableList(new ArrayList<>(l10ns.values()));
	}

	public int getRevision()
	{
		return revision.get();
	}

	public String getLocale()
	{
		return locale;
	}

	public void setLocale(String locale)
	{
		this.locale = locale;
		revision.incrementAndGet();
	}

	public String getDefaultLocale()
	{
		return defaultLocale;
	}

	public void setDefaultLocale(String defaultLocale)
	{
		this.defaultLocale = defaultLocale;
		revision.incrementAndGet();
	}

	public String[] getLoadSubDirectories()
	{
		return loadSubDirectories;
	}

	public void setLoadSubDirectories(String[] loadSubDirectories)
	{
		this.loadSubDirectories = loadSubDirectories;
	}

	public DLCore getCore()
	{
		return core;
	}

	public void setCore(DLCore core)
	{
		this.core = core;
	}
}
