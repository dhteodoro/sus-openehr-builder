package br.uerj.lampada.openehr.susbuilder.builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import openEHR.v1.template.TEMPLATE;

import org.openehr.am.archetype.Archetype;
import org.openehr.am.archetype.constraintmodel.CAttribute;
import org.openehr.am.archetype.constraintmodel.CComplexObject;
import org.openehr.am.archetype.constraintmodel.CObject;
import org.openehr.am.template.Flattener;
import org.openehr.am.template.FlatteningException;
import org.openehr.am.template.OETParser;
import org.openehr.rm.common.generic.PartyIdentified;
import org.openehr.rm.composition.Composition;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.datatypes.text.DvCodedText;
import org.openehr.rm.support.identification.TemplateID;
import org.openehr.rm.support.terminology.TerminologyService;

import se.liu.imt.mi.eee.ArchetypeAndTemplateRepository;
import br.uerj.lampada.openehr.susbuilder.mapping.PathState;

public class TemplateManager {

	private static final String adlExtension = "adl";
	private static final CodePhrase CHARSET = new CodePhrase(
			"IANA_character-sets", "UTF-8");

	private static final CodePhrase COUNTRY = new CodePhrase("ISO_3166-1", "BR");
	private static final CodePhrase LANGUAGE = new CodePhrase("ISO_639-1", "en");
	private static final String oetExtension = "oet";
	private static final PartyIdentified PARTY = PartyIdentified
			.named("Physician");
	private static final String RMVERSION = "1.0.2";
	private static final DvCodedText SETTING = new DvCodedText(
			"secondary medical", new CodePhrase("openehr", "232"));

	private Archetype archetype;
	private Map<String, Archetype> archetypeMap;
	private Composition composition;
	private CompositionPopulator compositionPopulator;

	// private String patientId;

	public TemplateManager(String template, String archetypeRepoPath,
			String templateRepoPath, boolean isPersistent)
			throws FlatteningException, Exception {

		ArchetypeAndTemplateRepository arTeRep = new ArchetypeAndTemplateRepository(
				new File(archetypeRepoPath), adlExtension, new File(
						templateRepoPath), oetExtension);

		OETParser parser = new OETParser();
		String templateFile = templateRepoPath + "/" + template + "."
				+ oetExtension;
		InputStream templateIS = new FileInputStream(templateFile);

		TEMPLATE templateObj = parser.parseTemplate(templateIS).getTemplate();

		// Load archetypes
		Map<String, Archetype> archetypeMap = arTeRep.getArchetypeMap();
		// Load templates
		Map<String, TEMPLATE> templateMap = arTeRep.getTemplateMap();

		// Serialize template as using the archetype model
		Flattener flattener = new Flattener();
		Archetype archetype = flattener.toFlattenedArchetype(templateObj,
				archetypeMap, templateMap);

		TemplateID templateId = new TemplateID(templateObj.getId());

		DvCodedText category = new DvCodedText("event", new CodePhrase(
				"openehr", "433"));

		if (isPersistent) {
			category = new DvCodedText("persistent", new CodePhrase("openehr",
					"431"));
		}

		this.archetype = archetype;
		this.archetypeMap = archetypeMap;
		initiateCompositionManager(LANGUAGE, CHARSET, templateId, RMVERSION,
				SETTING, COUNTRY, category, PARTY);
	}

	public void cleanTemplate(Map<String, PathState> pathState) {

		Map<String, CObject> archMap = archetype.getPathNodeMap();
		for (String archPath : archMap.keySet()) {
			CObject archObj = archMap.get(archPath);

			if (archObj instanceof CComplexObject) {

				CComplexObject ccobj = ((CComplexObject) archObj);

				if (!pathState.get(archPath).isMapped() && !ccobj.isRequired()) {
					// ccobj.setAnyAllowed(false);
					if (ccobj.getAttributes() != null
							&& ccobj.getAttributes().size() > 0) {

						List<CAttribute> cattrs = ccobj.getAttributes();
						Iterator<CAttribute> i = cattrs.iterator();
						while (i.hasNext()) {
							CAttribute cattr = i.next(); // must be called
															// before you can
															// call i.remove()
							// Do something
							// i.remove();
							((CComplexObject) archObj).removeAttribute(cattr
									.getRmAttributeName());
							break;
						}
					}
				} else {
					if (ccobj.isHiddenOnForm()) {
						System.out.println("hidden");
					}
					if (ccobj.hasAttribute("protocol")) {
						ccobj.removeAttribute("protocol");
					}
					if (ccobj.hasAttribute("state")) {
						ccobj.removeAttribute("state");
					}
					if (ccobj.hasAttribute("timing")) {
						ccobj.removeAttribute("timing");
					}
					if (ccobj.hasAttribute("other_context")) {
						ccobj.removeAttribute("other_context");
					}
				}

			}
		}
	}

	public void createComposition(String patientId, String uid,
			CompositionContent content) throws Exception {
		// if(composition == null || !this.patientId.equals(patientId)) {
		// this.patientId = patientId;
		composition = (Composition) compositionPopulator.create(patientId, uid,
				archetype, archetypeMap, content.getContent(),
				content.getPathState());
		// } else {
		// updateComposition(uid, content);
		// }
	}

	public Archetype getArchetype() {
		return archetype;
	}

	public Map<String, Archetype> getArchetypeMap() {
		return archetypeMap;
	}

	public Composition getComposition() {
		return composition;
	}

	public CompositionPopulator getGenerator() {
		return compositionPopulator;
	}

	public void setArchetype(Archetype archetype) {
		this.archetype = archetype;
	}

	public void setArchetypeMap(Map<String, Archetype> archetypeMap) {
		this.archetypeMap = archetypeMap;
	}

	private void initiateCompositionManager(CodePhrase lang,
			CodePhrase charset, TemplateID templateId, String rmVersion,
			DvCodedText setting, CodePhrase country, DvCodedText category,
			PartyIdentified party) throws Exception {
		compositionPopulator = new CompositionPopulator(lang, charset,
				templateId, rmVersion, setting, country, category, party);
	}

	private void updateComposition(String uid, CompositionContent content)
			throws Exception {
		// TODO update patient id
		Map<String, Object> acont = content.getContent();

		composition.set("/uid", acont);

		for (String path : acont.keySet()) {
			Object cont = acont.get(path);
			if (cont == null) {
				cont = new DvCodedText("unknown", new CodePhrase(
						TerminologyService.OPENEHR, "253"));
			}
			composition.set(path, cont);
		}
	}
}
